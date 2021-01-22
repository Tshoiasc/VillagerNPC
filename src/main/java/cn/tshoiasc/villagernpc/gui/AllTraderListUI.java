package cn.tshoiasc.villagernpc.gui;

import cn.tshoiasc.villagernpc.VillagerNPC;
import cn.tshoiasc.villagernpc.events.EventHandlers;
import cn.tshoiasc.villagernpc.gui.library.PreUI;
import cn.tshoiasc.villagernpc.gui.operator.UI_NPC;
import cn.tshoiasc.villagernpc.object.TraderObjectStorage;
import cn.tshoiasc.villagernpc.utils.ItemBuilder;
import cn.tshoiasc.villagernpc.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AllTraderListUI extends UI_NPC {
    public AllTraderListUI(@Nullable String title, @Nullable PreUI last) {
        super(title, last);
        Bukkit.getPluginManager().registerEvents(this, VillagerNPC.plugin);
    }

    @Override
    public void refreshUI() {
//看一下PreUI链表中有几个节点
        int size = this.getSize();
        //防止并发修改异常 备份
        TraderObjectStorage[] vos_array = TraderObjectStorage.TRADER.toArray(new TraderObjectStorage[0]);
        //删除trader中死亡实体
        for (TraderObjectStorage v : vos_array) {
            Entity o = Bukkit.getEntity(v.getUuid());
            Entity[] entities = v.getCreateLoc().getWorld().getChunkAt(v.getCreateLoc()).getEntities();
            int i = 1;
            for (Entity e : entities) {
                if (!e.getLocation().equals(v.getCreateLoc())) continue;
                if (e.getType().equals(EntityType.WANDERING_TRADER) && e.getCustomName() != null && e.getCustomName().equals(v.getName())) {
                    //Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendWarnMessage("实体：" + e.getUniqueId() + " 在此区块堆叠，已去除"));
                    if (TraderObjectStorage.getVillagerObjectStorage((WanderingTrader) e) != null) {
                        if (i == 1) {
                            //e.remove();
                            i++;
                            continue;
                        }
                        //v.getEditui(null).delete();
                        TraderObjectStorage.TRADER.remove(TraderObjectStorage.getVillagerObjectStorage((WanderingTrader) e));
                    }
                    e.remove();
                }

            }
            if (o == null) {
                EventHandlers.SPAWN_ENTITY = 1;
                //Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendWarnMessage("实体：" + v.getUuid() + " 不存在，已重新创建"));
                EventHandlers.SPAWN_ENTITY = 2;

                o = v.getCreateLoc().getWorld().spawnEntity(v.getCreateLoc(), EntityType.WANDERING_TRADER);
                v.setUuid(o.getUniqueId());
                o.setCustomName(v.getName());
                ((WanderingTrader) o).setRecipes(v.getMerchant_recipes());

            } else if (o.isDead()) {
                EventHandlers.SPAWN_ENTITY = 1;
                //Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendWarnMessage("实体：" + v.getUuid() + " 已死亡，已重新创建"));
                EventHandlers.SPAWN_ENTITY = 2;
                o = (WanderingTrader) v.getCreateLoc().getWorld().spawnEntity(v.getCreateLoc(), EntityType.WANDERING_TRADER);
                v.setUuid(o.getUniqueId());
                o.setCustomName(v.getName());
                ((WanderingTrader) o).setRecipes(v.getMerchant_recipes());
            } else {

            }
            WanderingTrader a = (WanderingTrader) o;
            a.setAI(false);
            a.setInvisible(false);
            a.setCustomNameVisible(true);
            v.setMerchant_recipes(a.getRecipes());
        }
        //看一下有几个Trader
        int amount = TraderObjectStorage.TRADER.size();
        int list = amount % 36 == 0 ? amount / 36 : (amount / 36) + 1;
        PreUI cursor = this.getEnd();
        if (size > list) {
            //断开关键节点
            for (int i = 1; i < size - list; i++) {
                List<HumanEntity> viewers = cursor.getUI().getViewers();
                for (HumanEntity p : viewers) {
                    p.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                    p.sendMessage(PlayerUtil.sendWarnMessage("管理员已重载插件，该页面被删除"));
                }
                cursor = cursor.getLast();

            }
            cursor.setNext(null);

        } else if (size < list) {
            //往后拓
            for (int i = 0; i < list - size; i++) {
                cursor = new AllTraderListUI(null, cursor);
            }
        }
        //处理单项
        cursor = TraderObjectStorage.FIRST_PREUI;

        if (amount == 0) {
            cursor.getUI().setItem(0, null);
        } else {
            for (int i = 1, index = 0; i <= list * 36; i++, index++) {
                if (i > amount) {
                    cursor.getUI().setItem(index, null);
                    continue;
                }
                TraderObjectStorage obj = TraderObjectStorage.TRADER.get(i - 1);
                boolean isOpen = obj.isOpen();
                cursor.getUI().setItem(index, new ItemBuilder(isOpen ? Material.PLAYER_HEAD : Material.ZOMBIE_HEAD).setName(obj.getName()).addLore("§4左键--§b进入编辑界面").addLore("§5右键--§2传送到NPC身边").addLore(isOpen ? "§a§l已开启" : "§4§l已关闭").addLore("§d编号：§6" + i).addLore("§b创建者：§e" + obj.getCreator()).build());
                obj.getEditui(cursor).getUI().setItem(49, obj.isOpen() ? TraderEditUI.Button_Open : TraderEditUI.Button_Close);
                obj.setEditui(new TraderEditUI(obj));
                if (i % 36 == 0) {
                    cursor = cursor.getNext();//翻页
                    index = -1;//索引归零
                }
            }
        }

        List<TraderObjectStorage> sources = TraderObjectStorage.TRADER;

        File storage = new File(VillagerNPC.plugin.getDataFolder(), "TraderStorage.yml");
        try {
            FileWriter fw = new FileWriter(storage);
            fw.write("");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(storage);
        int i = 1;
        for (TraderObjectStorage vos : sources) {
            yaml.createSection("" + i, vos.toMap());
            yaml.set(i + ".UUID", vos.getUuid().toString());
            int b = 1;

            for (MerchantRecipe mr : vos.getMerchant_recipes()) {
                ItemStack from = mr.getIngredients().get(0);
                ItemStack to = mr.getResult();
                yaml.set(i + ".MerchantRecipe." + b + ".from", from);
                yaml.set(i + ".MerchantRecipe." + b + ".to", to);
                b++;

            }
            //System.out.println("-------------------------------------");
            i++;
        }
        try {
            yaml.save(storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void allOpen() {
        for (TraderObjectStorage i : TraderObjectStorage.TRADER) i.setOpen(true);
        refreshUI();
    }

    @Override
    public void allClose() {
        for (TraderObjectStorage i : TraderObjectStorage.TRADER) i.setOpen(false);
        refreshUI();
    }

    @Override
    public void newNPC(HumanEntity p) {
        EventHandlers.SPAWN_ENTITY = 1;
        WanderingTrader entity = (WanderingTrader) p.getWorld().spawnEntity(p.getLocation(), EntityType.WANDERING_TRADER, CreatureSpawnEvent.SpawnReason.CUSTOM);
        EventHandlers.SPAWN_ENTITY = 2;
        entity.setCustomName("NPC商人");
        entity.setCustomNameVisible(true);
        entity.setAware(false);
        entity.setAI(false);
        TraderObjectStorage.newTrader(entity, p);
        PreUI end = this.getEnd();
        if (p.getOpenInventory().getTopInventory() != end) {
            p.closeInventory();
            p.openInventory(end.getUI());
        }
        refreshUI();
    }

    @EventHandler
    private void InventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().equals(this.getUI()))
            refreshUI();

    }

    public static void addTrader(TraderObjectStorage one) {
        //获取末链表
        PreUI lastone = TraderObjectStorage.FIRST_PREUI.getEnd();
        int index = lastone.getFirstEmpty();
        int g;
        if (index == -1) {
            g = Integer.parseInt(lastone.getUI().getItem(35).getItemMeta().getLore().get(3).substring(7));
            lastone = new AllTraderListUI(null, lastone);
            index = 0;
        } else if (index == 0) {
            if (lastone.getLast() == null) g = 0;
            else {
                PreUI lastPre = lastone.getLast();
                g = Integer.parseInt(lastPre.getUI().getItem(35).getItemMeta().getLore().get(3).substring(7));
            }

        } else {
            g = Integer.parseInt(lastone.getUI().getItem(index - 1).getItemMeta().getLore().get(3).substring(7));
        }
        lastone.getUI().setItem(index, new ItemBuilder(one.isOpen() ? Material.PLAYER_HEAD : Material.ZOMBIE_HEAD).setName(one.getName()).addLore("§4左键--§b进入编辑界面").addLore("§5右键--§2传送到NPC身边").addLore(one.isOpen() ? "§a§l已开启" : "§4§l已关闭").addLore("§d编号：§6" + (g + 1)).addLore("§b创建者：§e" + one.getCreator()).build());


    }

    public static AllTraderListUI initTradeList(List<TraderObjectStorage> trader) {
        PreUI current = null;
        int amount = trader.size();
        //System.out.println("amount " +amount);
        for (int Villager_index = 0; ; ) {
            current = new AllTraderListUI(null, current);
            TraderObjectStorage[] vos_array = trader.toArray(new TraderObjectStorage[0]);
            for (int i = 0; i < 36 && Villager_index < amount; i++, Villager_index++) {
                TraderObjectStorage obj = vos_array[Villager_index];
                Entity o = Bukkit.getEntity(obj.getUuid());
                //System.out.println(trader.get(Villager_index).getName());
                current.getUI().setItem(i, new ItemBuilder(trader.get(Villager_index).isOpen() ? Material.PLAYER_HEAD : Material.ZOMBIE_HEAD).setName(obj.getName()).addLore("§4左键--§b进入编辑界面").addLore("§5右键--§2传送到NPC身边").addLore(trader.get(Villager_index).isOpen() ? "§a§l已开启" : "§4§l已关闭").addLore("§d编号：§6" + (Villager_index + 1)).addLore("§b创建者：§e" + obj.getCreator()).build());
            }
            if (Villager_index == amount) {
                break;
            }
        }

        return TraderObjectStorage.FIRST_PREUI = (AllTraderListUI) current.getFirst();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory().equals(this.getUI())) {
            e.setCancelled(true);
            final Player p = (Player) e.getView().getPlayer();
            switch (e.getRawSlot()) {
                case 45:
                    //上一页
                    if (getLast() != null) {
                        e.getView().close();
                        e.getView().getPlayer().openInventory(getLast().getUI());
                    }
                    break;
                case 47:
                    new Thread(this::allOpen).start();
                    break;
                case 49:
                    newNPC(e.getWhoClicked());
                    break;


                case 51:
                    new Thread(this::allClose).start();
                    break;
                case 52:
                    //重新加载
                    refreshUI();
                    e.getView().getPlayer().sendMessage(PlayerUtil.sendNormalMessage("刷新成功"));
                case 53:
                    //下一页
                    if (this.getNext() != null) {
                        e.getView().close();
                        e.getView().getPlayer().openInventory(this.getNext().getUI());
                    }
                    break;
                default:
                    if (e.getRawSlot() > 53) return;
                    if (e.getRawSlot() < 36 && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                        //获取编号
                        ItemMeta im = e.getCurrentItem().getItemMeta();
                        List<String> lore = im.getLore();
                        assert lore != null;
                        int index = Integer.parseInt(lore.get(3).substring(7));
                        if (e.getClick() == ClickType.RIGHT) {
                            p.teleport(Objects.requireNonNull(Bukkit.getEntity(TraderObjectStorage.TRADER.get(index - 1).getUuid())));
                        } else if (e.getClick() == ClickType.LEFT) {
                            //打开编辑面板
                            TraderObjectStorage vos1 = TraderObjectStorage.TRADER.get(index - 1);
                            TraderEditUI eu = vos1.getEditui(this);
                            Inventory inv = eu.getUI();

                            e.getView().getPlayer().openInventory(inv);

                        }

                    }
            }
        }
    }

}
