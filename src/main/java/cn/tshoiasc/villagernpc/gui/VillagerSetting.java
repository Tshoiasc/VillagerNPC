package cn.tshoiasc.villagernpc.gui;

import cn.tshoiasc.villagernpc.object.TraderObjectStorage;
import cn.tshoiasc.villagernpc.VillagerNPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class VillagerSetting implements Listener {
    private final static Inventory GUI = Bukkit.createInventory(null, InventoryType.HOPPER, "§e§l[NPC]");

    /**
     * 村民NPC界面的初始化
     * 一共五个格子 1.普通NPC设置界面 2.流浪商人设置界面 3.查看交易记录（做个网页，可以一键退款） 4.全服设置
     **/
    public VillagerSetting(VillagerNPC plugin) {
        //设置按钮图标
        //--普通NPC按钮
        ItemStack Button_NPC = new ItemStack(Material.BIRCH_SIGN);
        ItemMeta Button_NPC_Meta = Button_NPC.getItemMeta();
        Button_NPC_Meta.setDisplayName("§a§l普通NPC设置");
        List<String> Button_NPC_Lore = new ArrayList<>();
        Button_NPC_Lore.add("§4左键--§d进入NPC设置界面");
        Button_NPC_Lore.add("§5右键--§7无效");
        Button_NPC_Meta.setLore(Button_NPC_Lore);
        Button_NPC.setItemMeta(Button_NPC_Meta);
        //--流浪商人设置按钮
        ItemStack Button_Trader = new ItemStack(Material.JUNGLE_SIGN);
        ItemMeta Button_Trader_Meta = Button_Trader.getItemMeta();
        Button_Trader_Meta.setDisplayName("§2§l流浪商人NPC设置");
        List<String> Button_Trader_Lore = new ArrayList<>();
        Button_Trader_Lore.add("§4左键--§b进入NPC设置界面");
        Button_Trader_Lore.add("§5右键--§7无效");
        Button_Trader_Meta.setLore(Button_Trader_Lore);
        Button_Trader.setItemMeta(Button_Trader_Meta);
        //--查看交易记录按钮
        ItemStack Button_TradeList = new ItemStack(Material.BOOK);
        ItemMeta Button_TradeList_Meta = Button_TradeList.getItemMeta();
        Button_TradeList_Meta.setDisplayName("§6§l交易记录");
        List<String> Button_TradeList_Lore = new ArrayList<>();
        Button_TradeList_Lore.add("§4左键--§b进入交易记录界面");
        Button_TradeList_Lore.add("§5右键--§d进入网站");
        Button_TradeList_Meta.setLore(Button_TradeList_Lore);
        Button_TradeList.setItemMeta(Button_TradeList_Meta);
        //--全服设置按钮
        ItemStack Button_All = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta Button_All_Meta = Button_All.getItemMeta();
        Button_All_Meta.setDisplayName("§c§l全服设置");
        List<String> Button_All_Lore = new ArrayList<>();
        Button_All_Lore.add("§4左键--§b进入全局设置界面");
        Button_All_Lore.add("§5右键--§7无效");
        Button_All_Meta.setLore(Button_All_Lore);
        Button_All.setItemMeta(Button_All_Meta);
        //--未开发按钮
        ItemStack Button_NONE = new ItemStack(Material.RED_WOOL);
        ItemMeta Button_NONE_Meta = Button_NONE.getItemMeta();
        Button_NONE_Meta.setDisplayName("§c§l占位符");
        List<String> Button_NONE_Lore = new ArrayList<>();
        Button_NONE_Lore.add("§4左键--§7无效");
        Button_NONE_Lore.add("§5右键--§7无效");
        Button_NONE_Meta.setLore(Button_NONE_Lore);
        Button_NONE.setItemMeta(Button_NONE_Meta);
        //构建到gui里
        //GUI.setItem(0, Button_NPC);
        GUI.setItem(0, Button_Trader);
        GUI.setItem(1, Button_TradeList);
        GUI.setItem(2, Button_All);
        GUI.setItem(3, Button_NONE);
        GUI.setItem(4, Button_NONE);
    }

    public static void open(Player player) {
        player.openInventory(GUI);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory().equals(GUI)) {
            e.setCancelled(true);
            switch (e.getRawSlot()) {
                case 0:
                    //流浪商人设置界面
                    p.openInventory(TraderObjectStorage.FIRST_PREUI.getUI());
                    break;
                case 1:
                    p.openInventory(TradeRecord.FIRST_TRADE_RECORD.getUI());
                    //查看交易记录
                    break;
                case 2:
                    p.openInventory(AllOption.GUI);
                    //全服设置
                    break;
                case 3:

                default:
                    //其他
                    break;

            }
        }
    }

}
