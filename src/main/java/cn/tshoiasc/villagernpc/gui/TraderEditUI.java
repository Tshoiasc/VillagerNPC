package cn.tshoiasc.villagernpc.gui;

import cn.tshoiasc.villagernpc.VillagerNPC;
import cn.tshoiasc.villagernpc.gui.library.PreUI;
import cn.tshoiasc.villagernpc.object.TraderObjectStorage;
import cn.tshoiasc.villagernpc.utils.ItemBuilder;
import cn.tshoiasc.villagernpc.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 这是一个前置UI
 * 一个大箱子 最后一行有 上一页 全部开启 打开网页 全部关闭 下一页
 **/
public class TraderEditUI implements Listener {
    //按钮
    //没有内容按钮
    public static ItemStack Button_notGo = new ItemBuilder(Material.GLASS_PANE).setName("§7§l没有内容了").build();
    //上一页按钮 链表非首位需设置
    public static ItemStack Button_last = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName("§5§l上一页").build();
    //下一页按钮 链表非末位需设置
    public static ItemStack Button_next = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName("§5§l下一页").build();
    //开启按钮
    public static ItemStack Button_Open = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§3§l状态：§a§l已开启").build();
    //关闭按钮
    public static ItemStack Button_Close = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§3§l状态：§4§l已关闭").build();
    //删除按钮
    public static ItemStack Button_Delete = new ItemBuilder(Material.SHEARS).setName("§4§l删除").build();
    //占位符
    public static ItemStack Button_None = new ItemBuilder(Material.IRON_BARS).setName("§7§l占位符").build();
    //返回
    public static ItemStack Button_Return = new ItemBuilder(Material.LEAD).setName("§5§l返回").build();
    public static ItemStack recipe_none = new ItemBuilder(Material.GLASS_PANE).setName("§7§l未定义").build();
    //
    public static ItemStack Button_Edit = new ItemBuilder(Material.LIME_WOOL).setName("§a§l点击编辑").build();
    public static ItemStack Button_UnEdit = new ItemBuilder(Material.RED_WOOL).setName("§4§l结束编辑").build();
    public static ItemStack recipe = new ItemBuilder(Material.IRON_NUGGET).setName("§d§l兑换-->").build();
    private final Inventory inventory;
    private final TraderObjectStorage controller;
    private String name;
    private boolean isEditing;

    public TraderEditUI(TraderObjectStorage vos) {
        this.controller = vos;
        this.name = vos.getName();
        inventory = Bukkit.createInventory(null, 54, name);

//        //把按钮放在UI里
//        int[] index = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34};
        for (int index = 3, i = 0; i < 6; index += 9, i++) inventory.setItem(index, Button_None);
        for (int index = 1, i = 0; i < 6; index += 9, i++) inventory.setItem(index, recipe);
        //21
        inventory.setItem(21, Button_Edit);
        inventory.setItem(49, vos.isOpen() ? Button_Open : Button_Close);
        inventory.setItem(53, Button_Return);
        inventory.setItem(51, Button_Delete);

        //注册监听器
        Bukkit.getPluginManager().registerEvents(this, VillagerNPC.plugin);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditing() {
        return isEditing;
    }

    @EventHandler
    private void inventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().equals(inventory)) {
            if (e.getViewers().size() != 1) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("有管理员在编辑这个NPC！"));
                return;
            }
            WanderingTrader wt = (WanderingTrader) Bukkit.getEntity(controller.getUuid());
            //assert wt != null;
            if (wt != null && wt.isTrading()) {
                e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("有玩家交易中，请等待交易结束。"));
                return;
            }
            inventory.setItem(21, Button_Edit);
            isEditing = false;
            TraderObjectStorage.FIRST_PREUI.refreshUI();
            refreshRecipe();
        }


    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent e) {
        //看看是否是编辑状态
        if (e.getInventory().equals(inventory))
            if (isEditing) {
                isEditing = false;
                e.getInventory().setItem(21, Button_Edit);
                e.getPlayer().sendMessage(PlayerUtil.sendWarnMessage("您关闭了界面，系统已自动结束编辑"));
            }

    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
            final Player p = (Player) e.getView().getPlayer();
            switch (e.getRawSlot()) {
                case 21:
                    if (e.getCurrentItem().getType() == Material.RED_WOOL) {
                        e.setCurrentItem(Button_Edit);
                        //关闭编辑模式
                        isEditing = false;
                        List<MerchantRecipe> mrlist = new ArrayList<>();
                        for (int a = 0, b = 2, i = 1; a <= 45; a += 9, b += 9, i++) {
                            boolean flag = true;
                            ItemStack i1 = inventory.getItem(a);
                            ItemStack i2 = inventory.getItem(b);
                            if (i1 == null || i1.isSimilar(recipe_none)) flag = false;
                            if (i2 == null || i2.isSimilar(recipe_none)) flag = false;
                            if (flag) {
                                MerchantRecipe mr = new MerchantRecipe(i2, Integer.MAX_VALUE);
                                List<ItemStack> l = new ArrayList<>();
                                l.add(i1);
                                mr.setIngredients(l);
                                mrlist.add(mr);
                                e.getWhoClicked().sendMessage(PlayerUtil.sendNormalMessage("第" + i + "行:" + i1.getI18NDisplayName() + "兑换" + i2.getI18NDisplayName() + ",条目生效"));
                            } else {
                                e.getWhoClicked().sendMessage(PlayerUtil.sendWarnMessage("第" + i + "行不符合规定，不做修改"));
                            }
                        }
                        ((WanderingTrader) Bukkit.getEntity(controller.getUuid())).setRecipes(mrlist);

                        refreshRecipe();
                        e.getWhoClicked().sendMessage(PlayerUtil.sendWarnMessage("编辑模式已关闭，修改完成。"));
                        TraderObjectStorage.FIRST_PREUI.refreshUI();
                    } else {
                        e.setCurrentItem(Button_UnEdit);
                        //开启编辑模式
                        isEditing = true;
                        for (int a = 0, b = 2; a <= 45; a += 9, b += 9) {
                            if (inventory.getItem(a) != null && inventory.getItem(a).isSimilar(recipe_none))
                                inventory.setItem(a, null);
                            if (inventory.getItem(b) != null && inventory.getItem(b).isSimilar(recipe_none))
                                inventory.setItem(b, null);
                        }
                        e.getWhoClicked().sendMessage(PlayerUtil.sendWarnMessage("编辑模式开启"));
                    }
                    break;
                case 49:
                    if (e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                        this.controller.setOpen(true);
                        e.setCurrentItem(Button_Open);
                    } else {
                        this.controller.setOpen(false);
                        e.setCurrentItem(Button_Close);
                    }
                    TraderObjectStorage.FIRST_PREUI.refreshUI();

                    break;
                case 51:
                    delete();
                    e.getWhoClicked().openInventory(TraderObjectStorage.FIRST_PREUI.getEnd().getUI());
                    break;
                case 53:
                    e.getView().close();
                    e.getWhoClicked().openInventory(controller.getCurrent_edit_preui().getUI());
                    break;
                default:
                    if (e.getRawSlot() > 53) {
                        if (e.getClick() == ClickType.LEFT) e.setCancelled(false);
                        else {
                            e.setCancelled(true);
                        }
                    }
                    if (isEditing) if (e.getRawSlot() % 9 == 0 || e.getRawSlot() % 9 == 2) {
                        e.setCancelled(false);
                    }

            }
        }
    }

    private void refreshRecipe() {
        Integer[][] valid_index =
                {
                        {0, 2},
                        {9, 11},
                        {18, 20},
                        {27, 29},
                        {36, 38},
                        {45, 47}
                };
        for (Integer[] a : valid_index) {
            for (Integer b : a) {
                inventory.setItem(b, recipe_none);
            }
        }
        WanderingTrader wt = (WanderingTrader) Bukkit.getEntity(controller.getUuid());
        if (wt == null) {
            TraderObjectStorage.FIRST_PREUI.refreshUI();
        }
        assert wt != null;
        List<MerchantRecipe> mrlist2 = wt.getRecipes();
        Iterator<MerchantRecipe> it_mrlist = mrlist2.iterator();
        for (Integer[] a : valid_index) {
            if (!it_mrlist.hasNext()) break;
            MerchantRecipe current = it_mrlist.next();
            inventory.setItem(a[0], current.getIngredients().get(0));
            inventory.setItem(a[1], current.getResult());
        }
    }

    public void delete() {
        HumanEntity[] human_array = this.getUI().getViewers().toArray(new HumanEntity[this.getUI().getViewers().size()]);
        for (HumanEntity human : human_array) {
            human.sendMessage(PlayerUtil.sendWarnMessage("配置变动，该页面已关闭。"));
            human.closeInventory();
        }
//        System.out.println(controller.getName() + "----" + controller.getUuid() + "\n");
//        System.out.println("-----------------------------------------------------------");
//        TraderObjectStorage.TRADER.forEach(System.out::println);
//        System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
        TraderObjectStorage.TRADER.remove(controller);
        Bukkit.getEntity(controller.getUuid()).remove();
        TraderObjectStorage.FIRST_PREUI.refreshUI();
        HandlerList.unregisterAll(this);
    }

    public Inventory getUI() {
        return inventory;
    }

}
