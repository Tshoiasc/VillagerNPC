package cn.tshoiasc.villagernpc.command;

import cn.tshoiasc.villagernpc.VillagerNPC;
import cn.tshoiasc.villagernpc.gui.AllOption;
import cn.tshoiasc.villagernpc.gui.AllTraderListUI;
import cn.tshoiasc.villagernpc.gui.TradeRecord;
import cn.tshoiasc.villagernpc.gui.VillagerSetting;
import cn.tshoiasc.villagernpc.object.TraderObjectStorage;
import cn.tshoiasc.villagernpc.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    VillagerNPC plugin;

    public Commands(VillagerNPC plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(PlayerUtil.sendNormalMessage(" §4控制台不被允许使用该指令"));
            return true;
        }
        Player p = (Player) sender;
        if (!p.isOp() || !p.hasPermission("VillagerNpc.admin")) {
            p.sendMessage(PlayerUtil.sendWarnMessage("您的权限不足。"));
            return true;
        }
        if (args.length == 0) {
            p.sendMessage("-----------------§7[§e§lNPC]§f---------------------");
            p.sendMessage("§e§l管理员指令: /vnpc admin");
            p.sendMessage("§e§l管理员指令: /vnpc reload");
            p.sendMessage("--------------------------------------------");
            return true;
        }
        //测试节点↓--------------------------------------------------------------
//        if (args[0].equalsIgnoreCase("test")) {
//
//            return true;
//        }
        //测试节点↑---------------------------------------------------------------
        if (args[0].equalsIgnoreCase("admin")) {

            VillagerSetting.open(p);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            p.sendMessage(PlayerUtil.sendNormalMessage("重新加载插件配置…………"));
            TraderObjectStorage.FIRST_PREUI.refreshUI();
            TradeRecord.FIRST_TRADE_RECORD.refreshUI();
            new AllOption();
            p.sendMessage(PlayerUtil.sendNormalMessage("加载完成…………"));
            return true;
        }


        return true;
    }

}
