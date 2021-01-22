package cn.tshoiasc.villagernpc;

import cn.tshoiasc.villagernpc.command.Commands;
import cn.tshoiasc.villagernpc.entity.CustomWanderHandler;
import cn.tshoiasc.villagernpc.events.EventHandlers;
import cn.tshoiasc.villagernpc.gui.AllOption;
import cn.tshoiasc.villagernpc.gui.AllTraderListUI;
import cn.tshoiasc.villagernpc.gui.TradeRecord;
import cn.tshoiasc.villagernpc.gui.VillagerSetting;
import cn.tshoiasc.villagernpc.object.TraderObjectStorage;
import cn.tshoiasc.villagernpc.utils.PlayerUtil;
import cn.tshoiasc.villagernpc.utils.VaultOperate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class VillagerNPC extends JavaPlugin implements Listener {
    public static VillagerNPC plugin;
    private String version;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("插件加载成功"));
        // Plugin startup logic

        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("加载已设置NPC信息…………"));

        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("├读取流浪商人npc…………"));
        try {
            File storage = new File(this.getDataFolder(), "TraderStorage.yml");

            if (!storage.exists()) plugin.saveResource("TraderStorage.yml", false);
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(storage);


            TraderObjectStorage.initTraderObjectStorage(yaml);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("├读取交易记录…………"));
        TradeRecord.initTradeRecord();
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("├读取config…………"));
        FileConfiguration config;
        try {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                plugin.saveDefaultConfig();
            }
            config = plugin.getConfig();
            AllOption.DISCOVER_REWARD = config.getInt("discover_wild_reward");
            AllOption.HELP_KILL_REWARD = config.getInt("help_kill_reward");
            AllOption.WORLD_CAN_SPAWN = config.getBoolean("world_spawn");
            AllOption.WILD_CAN_TRADE = config.getBoolean("wild_can_trade");
            AllOption.EGG_CAN_SPAWN = config.getBoolean("egg_spawn");
            if (AllOption.DISCOVER_REWARD > 0) AllOption.WILD_CAN_TRADE = false;
            new AllOption();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("├事件监听器加载中…………"));
        getServer().getPluginManager().registerEvents(new EventHandlers(), this);
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("├事件监听器加载成功"));
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("初始化设置GUI…………"));
        getServer().getPluginManager().registerEvents(new VillagerSetting(this), this);
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("GUI初始化成功"));
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("命令执行器加载中…………"));
        Objects.requireNonNull(getServer().getPluginCommand("vnpc")).setExecutor(new Commands(this));
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("命令执行器加载完成"));
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("Vault插件挂载中…………"));
        if (VaultOperate.setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("Vault插件挂载成功…………"));
        } else {
            Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendWarnMessage("Vault插件挂载失败…………"));
        }
        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("<-------加载NMS------->"));
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            CustomWanderHandler.init(this, getNmsClass("EntityVillagerTrader"), getCbClass("entity.CraftWanderingTrader"));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage("插件加载完成"));

        //输出测试数据


    }

    @Override
    public void onDisable() {
//        List<TraderObjectStorage> sources = TraderObjectStorage.TRADER;
//        File storage = new File(this.getDataFolder(), "TraderStorage.yml");
//        try {
//            FileWriter fw = new FileWriter(storage);
//            fw.write("");
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(storage);
//        int i = 1;
//        for (TraderObjectStorage vos : sources) {
//            yaml.createSection("" + i, vos.toMap());
//            yaml.set(i + ".UUID", vos.getUuid().toString());
//            i++;
//        }
//        try {
//            yaml.save(storage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        TraderObjectStorage.FIRST_PREUI.refreshUI();


        Bukkit.getConsoleSender().sendMessage("§5§l插件卸载成功");
    }

    public Class getNmsClass(String name) throws ClassNotFoundException {

        return Class.forName("net.minecraft.server." + version + "." + name);
    }

    public Class getCbClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }


}


