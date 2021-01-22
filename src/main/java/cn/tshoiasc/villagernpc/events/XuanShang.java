package cn.tshoiasc.villagernpc.events;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class XuanShang implements Runnable {
    public static Map<UUID, XuanShang> ThreadPool = new HashMap<>();

    public static XuanShang getThread(UUID uuid) {
        return ThreadPool.get(uuid);
    }

    Entity e;


    public XuanShang(Entity entity) {
        this.e = entity;
        ThreadPool.put(entity.getUniqueId(), this);
    }

    @Override
    public void run() {
        for (int g = 0; g < 100; g++) {
            if (!ThreadPool.containsValue(this)) return;
            if (e == null || e.isDead()) {
                ThreadPool.remove(e.getUniqueId());
                Bukkit.broadcastMessage("§6§l[§4§l公告§6§l] : §6§l悬赏目标(" + e.getName() + ")已被击败，悬赏结束");
                return;
            }
            for (double i = 0; i < 180; i += 180 / 6) {
                double radians = Math.toRadians(i);
                double radius = Math.sin(radians);
                double y = Math.cos(radians);
                for (double j = 0; j < 360; j += 180 / 6) {
                    double radiansCircle = Math.toRadians(j);
                    double x = Math.cos(radiansCircle) * radius;
                    double z = Math.sin(radiansCircle) * radius;
                    Location location = e.getLocation();
                    location.add(x, y, z);
                    location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
                    location.subtract(x, y, z);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        ThreadPool.remove(e.getUniqueId());
        Bukkit.broadcastMessage("§6[公告] : §4悬赏已结束。");
        Bukkit.broadcastMessage("§5[NPC] : 这么久都没人救我，真心酸。");

    }
}
