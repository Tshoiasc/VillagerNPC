package cn.tshoiasc.villagernpc.events;

import cn.tshoiasc.villagernpc.gui.AllOption;
import cn.tshoiasc.villagernpc.VillagerNPC;
import cn.tshoiasc.villagernpc.object.TraderObjectStorage;
import cn.tshoiasc.villagernpc.utils.PlayerUtil;
import cn.tshoiasc.villagernpc.utils.VaultOperate;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventHandlers implements Listener {

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent deathEvent) {
        if (AllOption.HELP_KILL_REWARD == 0) return;
        Entity deathEntity = deathEvent.getEntity();
        if (XuanShang.getThread(deathEntity.getUniqueId()) == null) return;
        //判断击杀者
        EntityDamageEvent damageEvent = deathEntity.getLastDamageCause();
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEvent = (EntityDamageByEntityEvent) damageEvent;
            Entity damagingEntity = damageByEvent.getDamager();
            if (damagingEntity instanceof Projectile) {
                Projectile projectile = (Projectile) damagingEntity;
                damagingEntity = (LivingEntity) projectile.getShooter();
            }
            if (damagingEntity instanceof Player) {
                Bukkit.broadcastMessage("§6§l[§4§l公告§6§l] : §6§l玩家" + ((Player) damagingEntity).getPlayer().getName() + "§6§l帮助了NPC，奖励" + AllOption.HELP_KILL_REWARD + "元。");
                XuanShang.ThreadPool.remove(deathEntity.getUniqueId());
                VaultOperate.give(damagingEntity.getName(), AllOption.HELP_KILL_REWARD);
                return;
            }
        }
        Bukkit.broadcastMessage("§6§l[§4§l公告§6§l] : §6§l悬赏目标(" + deathEntity.getName() + ")已被不明力量击败，悬赏结束");
        XuanShang.ThreadPool.remove(deathEntity.getUniqueId());
    }
//
//    @EventHandler
//    private void entityDeathEvent(EntityDeathEvent deathEvent) {
//        LivingEntity killed = deathEvent.getEntity();
//        if (!(killed instanceof WanderingTrader)) {
//            return;
//        }
//        TraderObjectStorage vos = TraderObjectStorage.getVillagerObjectStorage((WanderingTrader) killed);
//        if (vos == null) return;
//        EntityDamageEvent damageEvent = killed.getLastDamageCause();
//        if (damageEvent instanceof EntityDamageByEntityEvent) {
//            EntityDamageByEntityEvent damageByEvent = (EntityDamageByEntityEvent) damageEvent;
//            Entity damagingEntity = damageByEvent.getDamager();
//            if (damagingEntity instanceof Projectile) {
//                Projectile projectile = (Projectile) damagingEntity;
//                damagingEntity = (LivingEntity) projectile.getShooter();
//            }
//            if (damagingEntity != null) {
//                if (damagingEntity instanceof Player) {
//                    Player player = (Player) damagingEntity;
//                    if (!player.isOp() && !player.hasPermission("VillagerNpc.admin")) {
//                        Bukkit.getServer().broadcastMessage("§e§l[§4公告§e§l] §f§l： §4§l玩家§e§l(" + player.getName() + ")§4§l击杀了 §e§lNPC(" + killed.getCustomName() + ")§4§l已被系统处罚");
//                        player.kickPlayer("违反服务器规定，你已被踢出服务器");
//                    }
//
//                }
//
//            }
//        } else if (damageEvent instanceof EntityDamageByBlockEvent) {
////            EntityDamageByBlockEvent damageByEvent = (EntityDamageByBlockEvent) damageEvent;
////            Block damagingBlock = damageByEvent.getDamager();
////            damagingBlock.breakNaturally(null);
//        }
////        WanderingTrader e = (WanderingTrader) killed.getWorld().spawn(killed.getLocation(), WanderingTrader.class);
//        WanderingTrader a = (WanderingTrader) killed;
//        vos.setMerchant_recipes(a.getRecipes());
//
//    }


    @EventHandler
    private void entityDamageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof WanderingTrader)) return;
        WanderingTrader wt = (WanderingTrader) entity;
        TraderObjectStorage vos = TraderObjectStorage.getVillagerObjectStorage(wt);
        if (vos == null) return;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            e.setCancelled(true);
            wt.getWorld().playEffect(wt.getLocation(), Effect.ANVIL_LAND, null, 15);
            if (!(e.getDamager() instanceof Player)) {
                if (e.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) e.getDamager();
                    LivingEntity lv = (LivingEntity) projectile.getShooter();
                    if (lv instanceof Player) {
                        lv.sendMessage(PlayerUtil.sendWarnMessage("服务器禁止伤害NPC！"));
                        return;
                    }
                    projectile.remove();
                    if (lv == null) return;
                    if (!XuanShang.ThreadPool.containsKey(lv.getUniqueId()))
                        if (AllOption.HELP_KILL_REWARD != 0) {
                            xuanshang(lv);
                        }
                    return;
                }
                if (!XuanShang.ThreadPool.containsKey(e.getDamager().getUniqueId()))
                    if (AllOption.HELP_KILL_REWARD != 0) {
                        xuanshang(e.getDamager());
                    }
            } else {

                Player p = (Player) e.getDamager();
//            if (p.isOp()) {
//                e.setCancelled(false);
//                return;
//            }
                if (p.isOp()) {
                    e.setCancelled(false);
                    return;
                }
                p.sendMessage(PlayerUtil.sendWarnMessage("服务器禁止伤害NPC！"));
            }
            return;
        } else if (event.getCause().

                equals(EntityDamageEvent.DamageCause.LAVA)) {
            event.setCancelled(true);
        } else if (event.getCause().

                equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            event.setCancelled(true);
        } else if (event.getCause().

                equals(EntityDamageEvent.DamageCause.WITHER)) {
            event.setCancelled(true);
        } else if (event.getCause().

                equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            event.setCancelled(true);
        } else if (event.getCause().

                equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)) {
            event.setCancelled(true);
        }


    }

    private void xuanshang(Entity lv) {
        Bukkit.broadcastMessage("§6§l[NPC] ： 我在" + lv.getWorld().getName() + "被攻击了，快来救我");
        new Thread(new XuanShang(lv)).start();

    }

    @EventHandler
    private void playerInteractEntityEvent(PlayerInteractEntityEvent e) {
//        System.out.println(e.getRightClicked().getUniqueId());
        //获取交互对象
        Entity obj = e.getRightClicked();
        //看看是否是流浪商人
        if (obj.getType().equals(EntityType.WANDERING_TRADER)) {
            WanderingTrader current = (WanderingTrader) obj;
            TraderObjectStorage vos = TraderObjectStorage.getVillagerObjectStorage(current);
            //看看玩家是否有对应权限
            if (e.getPlayer().isOp() || e.getPlayer().hasPermission("VillagerNpc.admin")) {
                if (vos == null) {
                    //e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("这只商人是野生的！无法编辑"));
                    e.setCancelled(false);
                    return;
                }
                ItemStack im = e.getPlayer().getInventory().getItemInMainHand();
                if (im.getType().equals(Material.NAME_TAG) && im.hasItemMeta() && im.getItemMeta().hasDisplayName()) {
                    e.setCancelled(false);
                    vos.setName(im.getItemMeta().getDisplayName());
                    current.setCustomName(im.getItemMeta().getDisplayName());
                    e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("该NPC已命名为（" + im.getItemMeta().getDisplayName() + ")"));
                    TraderObjectStorage.FIRST_PREUI.refreshUI();
                } else {
                    e.getPlayer().openInventory(vos.getEditui(null).getUI());
                }
                e.setCancelled(true);
                return;

                //打开编辑面板
                //System.out.println(e.getRightClicked().getUniqueId());
            } else {
                //玩家不是管理员
                if (vos == null) {
                    if (AllOption.WILD_CAN_TRADE) {
                        e.setCancelled(false);
                        return;
                    } else {
                        if (AllOption.DISCOVER_REWARD != 0) {
                            Bukkit.getServer().broadcastMessage("§e§l[§4公告§e§l] §f§l： §5§l玩家§e§l(" + e.getPlayer().getName() + ")§5§l发现了 §e§l一只野生的流浪商人 §5§l奖励" + AllOption.DISCOVER_REWARD + "§5§l元巨款");
                            if(VaultOperate.isSupportVault())VaultOperate.give(e.getPlayer().getName(), AllOption.DISCOVER_REWARD);
                        } else {
                            e.getPlayer().sendMessage(PlayerUtil.sendNormalMessage("服务器禁止与野生商人交易"));
                            e.setCancelled(true);
                            return;
                        }

                        obj.remove();

                    }


                } else {
                    if (!vos.isOpen()) {
                        e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("该商人现处于关闭状态，无法使用。"));
                        e.setCancelled(true);
                        return;
                    } else {
                        if (vos.getEditui(null).isEditing()) {
                            e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("有管理员正在对该商人进行修改，无法使用。"));
                            e.setCancelled(true);
                            return;
                        } else {
                            ItemStack im = e.getPlayer().getInventory().getItemInMainHand();
                            if (im.getType().equals(Material.NAME_TAG) && im.hasItemMeta() && im.getItemMeta().hasDisplayName()) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("禁止对NPC命名"));
                                return;
                            }
                            if (current.isTrading()) {
                                e.getPlayer().sendMessage(PlayerUtil.sendNormalMessage("该商人正与" + current.getTrader().getName() + "交易。"));
                                e.setCancelled(true);
                                return;
                            }
                            e.setCancelled(false);
                        }

                    }
                }

            }
        }
    }

    public static int SPAWN_ENTITY = 2;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void CreatureSpawnEvent(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof WanderingTrader)) return;
        if (SPAWN_ENTITY == 2 && !e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))
            e.setCancelled(true);
        if (AllOption.EGG_CAN_SPAWN && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG))
            e.setCancelled(false);
        if (AllOption.WORLD_CAN_SPAWN && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL))
            e.setCancelled(false);

    }

    @EventHandler
    private void WorldLoadEvent(WorldLoadEvent e) {
        TraderObjectStorage.FIRST_PREUI.refreshUI();
    }

    @EventHandler
    private void InventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof MerchantInventory) {
            if (e.getInventory().getHolder() instanceof WanderingTrader) {
                WanderingTrader wt = (WanderingTrader) e.getInventory().getHolder();
                TraderObjectStorage tos = TraderObjectStorage.getVillagerObjectStorage(wt);
                if (tos == null) return;
                tos.currentTrade = 0;
            }
        }
    }

    @EventHandler
    private void TradeSelectEvent(TradeSelectEvent e) {
        if (e.getMerchant() instanceof WanderingTrader) {
            WanderingTrader wt = (WanderingTrader) e.getMerchant();
            TraderObjectStorage tos = TraderObjectStorage.getVillagerObjectStorage(wt);
            if (tos != null) {
                tos.currentTrade = e.getIndex();
            }
        }
    }

    private void store(Player p, ItemStack beTrade, ItemStack TradeResult) {
        if (beTrade == null || TradeResult == null || beTrade.getType().equals(Material.AIR) || TradeResult.getType().equals(Material.AIR))
            return;
        new Thread(() -> {
            File storage = new File(VillagerNPC.plugin.getDataFolder(), "TradeMemory.yml");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(storage);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = new Date();
            String title = sdf.format(d);
            if (yaml.get(title) == null) yaml.createSection(sdf.format(d));
            //MemorySection ms = (MemorySection) yaml.get(title);
            List<String> l = yaml.getStringList(title);
            //System.out.println(l);
            l.add(new SimpleDateFormat("hh:mm:ss").format(d) + " " + p.getName() + "用（" + beTrade.getI18NDisplayName() + "×" + beTrade.getAmount() + "）换了（" + TradeResult.getI18NDisplayName() + "×" + TradeResult.getAmount() + ")");
            //System.out.println(l);
            yaml.set(title, l);
            try {
                yaml.save(storage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv == null) return;
        if (!inv.getType().equals(InventoryType.MERCHANT)) return;
        MerchantInventory mi = (MerchantInventory) e.getInventory();
        if (!(mi.getHolder() instanceof WanderingTrader)) return;
        WanderingTrader entity = (WanderingTrader) mi.getHolder();

        TraderObjectStorage tos = TraderObjectStorage.getVillagerObjectStorage(entity);
        if (tos == null) return;
        if (e.getRawSlot() != 2) return;
        ItemStack result = e.getCurrentItem();
        if (result == null || result.getType().equals(Material.AIR)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        //计入交易记录
        if (e.getClick().isKeyboardClick()) {
            e.setCancelled(true);
            return;
        }
        ItemStack material = mi.getItem(0) == null ? mi.getItem(1) : mi.getItem(0);
        assert material != null;
        //被放进背包的物品
        ItemStack hasPut = e.getCurrentItem();
        if (tos.currentTrade == 0) {
            List<MerchantRecipe> recipes = mi.getMerchant().getRecipes();
            for (MerchantRecipe mr : recipes) {
                List<ItemStack> ing = mr.getIngredients();
                if (material.getAmount() < ing.get(0).getAmount() || !ing.get(0).isSimilar(material)) continue;
                if (!mr.getResult().isSimilar(hasPut)) continue;
                tos.currentTrade = recipes.indexOf(mr);
                break;
            }
        }
        MerchantRecipe current_recipe = entity.getRecipe(tos.currentTrade);
        if (!e.getClick().equals(ClickType.SHIFT_LEFT) && !e.getClick().equals(ClickType.SHIFT_RIGHT)) {
            store(p, current_recipe.getIngredients().get(0), current_recipe.getResult());
        } else {
            int canPutInAmount = 0;

            //查找背包内所有可以放该物品的数量
            //先看看相同材质有几个
            Inventory playerInventory = e.getWhoClicked().getInventory();
            //再看空格子
            ItemStack[] storageContents = playerInventory.getStorageContents();
            for (ItemStack i : storageContents) {
                if (i == null) {
                    canPutInAmount += hasPut.getMaxStackSize();
                } else if (i.isSimilar(hasPut)) canPutInAmount += hasPut.getMaxStackSize() - i.getAmount();
            }
            //再看看能合成出来多少个
            int material_Amount = material.getAmount();
            int recipeInt = material_Amount / current_recipe.getIngredients().get(0).getAmount();
            int canRecipeAmount = current_recipe.getResult().getAmount() * recipeInt;
            int canIngAmount = current_recipe.getIngredients().get(0).getAmount() * recipeInt;

            if (canPutInAmount < canRecipeAmount) {
                recipeInt = canPutInAmount / current_recipe.getResult().getAmount();

                canRecipeAmount = current_recipe.getResult().getAmount() * recipeInt;
                canIngAmount = current_recipe.getIngredients().get(0).getAmount() * recipeInt;
                int yu = canPutInAmount % current_recipe.getResult().getAmount();
                if (yu != 0) {
                    canRecipeAmount += yu;
                    canIngAmount += current_recipe.getIngredients().get(0).getAmount();
                }
            }
            store(p, material.asQuantity(canIngAmount), hasPut.asQuantity(canRecipeAmount));

        }


    }
}