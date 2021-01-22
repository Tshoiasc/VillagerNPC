package cn.tshoiasc.villagernpc.entity;

import cn.tshoiasc.villagernpc.VillagerNPC;
import org.bukkit.entity.WanderingTrader;

public class CustomWanderHandler {
    private static Class CB_WANDERINGTRADER;
    private static Class NMS_WANDERINGTRADER;
    private static VillagerNPC PLUGIN;

    public static void init(VillagerNPC plugin, Class Nms_WanderingTrader, Class Cb_WanderingTrader) {
        CB_WANDERINGTRADER = Cb_WanderingTrader;
        NMS_WANDERINGTRADER = Nms_WanderingTrader;
        PLUGIN = plugin;
    }
    public static WanderingTrader newTrader(){
        return null;


    }
    public static void handle(WanderingTrader wt) {


    }
}
