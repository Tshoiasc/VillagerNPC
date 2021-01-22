package cn.tshoiasc.villagernpc.gui;

import cn.tshoiasc.villagernpc.VillagerNPC;
import cn.tshoiasc.villagernpc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class AllOption implements Listener {

    public static int HELP_KILL_REWARD;
    public static boolean WORLD_CAN_SPAWN;
    public static int DISCOVER_REWARD;
    public static boolean WILD_CAN_TRADE;
    public static boolean EGG_CAN_SPAWN;
    public static Inventory GUI;

    private static ItemStack Button_wild_true = new ItemBuilder(Material.LIME_WOOL).setName("§d野生商人交易 : §b已开启").build();
    private static ItemStack Button_wild_false = new ItemBuilder(Material.RED_WOOL).setName("§d野生商人交易 : §4已关闭").build();
    private static ItemStack Button_egg_cant_spawn = new ItemBuilder(Material.RED_WOOL).setName("§d商人可蛋生 : §4已关闭").build();
    private static ItemStack Button_egg_can_spawn = new ItemBuilder(Material.LIME_WOOL).setName("§d商人可蛋生 : §b已开启").build();
    private static ItemStack Button_world_cant_spawn = new ItemBuilder(Material.RED_WOOL).setName("§d商人可自然生成 : §4已关闭").build();
    private static ItemStack Button_world_can_spawn = new ItemBuilder(Material.LIME_WOOL).setName("§d商人可自然生成 : §b已开启").build();

    public AllOption() {
        GUI = Bukkit.createInventory(null, InventoryType.HOPPER, "§6§l全局设置");
        if (WILD_CAN_TRADE)
            GUI.setItem(0, Button_wild_true);
        else
            GUI.setItem(0, Button_wild_false);
        if (EGG_CAN_SPAWN)
            GUI.setItem(1, Button_egg_can_spawn);
        else GUI.setItem(1, Button_egg_cant_spawn);
        if (WORLD_CAN_SPAWN) GUI.setItem(2, Button_world_can_spawn);
        else GUI.setItem(2, Button_world_cant_spawn);
        Bukkit.getPluginManager().registerEvents(this, VillagerNPC.plugin);
    }

    @EventHandler
    private void InventoryClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().equals(GUI)) {
            File file = new File(VillagerNPC.plugin.getDataFolder(), "config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            e.setCancelled(true);
            switch (e.getRawSlot()) {
                case 0:
                    if (WILD_CAN_TRADE) {
                        WILD_CAN_TRADE = false;
                        config.set("wild_can_trade", false);
                        GUI.setItem(0, Button_wild_false);
                    } else {
                        GUI.setItem(0, Button_wild_true);
                        config.set("wild_can_trade", true);
                        WILD_CAN_TRADE = true;
                    }
                    break;
                case 1:
                    if (EGG_CAN_SPAWN) {
                        EGG_CAN_SPAWN = false;
                        config.set("egg_spawn", false);
                        GUI.setItem(1, Button_egg_cant_spawn);
                    } else {
                        EGG_CAN_SPAWN = true;
                        config.set("egg_spawn", true);
                        GUI.setItem(1, Button_egg_can_spawn);
                    }
                    break;
                case 2:
                    if (WORLD_CAN_SPAWN) {
                        WORLD_CAN_SPAWN = false;
                        config.set("world_spawn", false);
                        GUI.setItem(2, Button_world_cant_spawn);
                    } else {
                        WORLD_CAN_SPAWN = true;
                        config.set("world_spawn", true);
                        GUI.setItem(2, Button_world_can_spawn);
                    }
                    break;

            }
            try {
                config.save(file);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
    }

}
