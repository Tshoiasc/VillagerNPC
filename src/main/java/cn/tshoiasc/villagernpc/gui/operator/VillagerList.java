package cn.tshoiasc.villagernpc.gui.operator;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerList {
    private final static Map<UUID, Inventory> V_MAP = new HashMap<>();

    //构造函数
    public VillagerList() {


    }

    public static boolean hasVillager(UUID uuid) {
        return V_MAP.containsKey(uuid);
    }

    public static Inventory getInventory(UUID uuid) {
        return V_MAP.get(uuid);
    }

    //加载文件中已设置的村民
    public static void InitialVillagerMap() {

    }

}
