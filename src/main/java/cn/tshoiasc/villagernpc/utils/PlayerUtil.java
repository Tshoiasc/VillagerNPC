package cn.tshoiasc.villagernpc.utils;

public class PlayerUtil {
    public static String sendNormalMessage(String message) {
        return "§7§l[§eNPC§7§l]§f§l ： §a§l" + message + "§f";
    }

    public static String sendWarnMessage(String message) {
        return "§7§l[§eNPC§7§l]§f§l ： §4§l" + message + "§f";
    }
}
