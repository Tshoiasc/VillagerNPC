package cn.tshoiasc.villagernpc.gui;

import cn.tshoiasc.villagernpc.VillagerNPC;
import cn.tshoiasc.villagernpc.gui.library.PreUI;
import cn.tshoiasc.villagernpc.gui.operator.UI_OTHER;
import cn.tshoiasc.villagernpc.utils.BookBuilder;
import cn.tshoiasc.villagernpc.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Set;

public class TradeRecord extends UI_OTHER {
    public static TradeRecord FIRST_TRADE_RECORD;

    public static void initTradeRecord() {
        File file = new File(VillagerNPC.plugin.getDataFolder(), "TradeMemory.yml");
        if (!file.exists()) VillagerNPC.plugin.saveResource("TradeMemory.yml", false);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Set<String> keys = yaml.getKeys(false);
        FIRST_TRADE_RECORD = new TradeRecord(null, null);
        TradeRecord cursor = FIRST_TRADE_RECORD;
        for (String key : keys) {
            List<String> current_list = yaml.getStringList(key);
            if (current_list.isEmpty()) continue;
            BookBuilder bd = new BookBuilder();
            bd.setName(key);
            for (String line : current_list) {
                if (bd.addLine(line) == null) {
                    //把bd放进去
                    if (!cursor.newObject(bd.build())) {
                        cursor = new TradeRecord(null, cursor);
                        cursor.newObject(bd.build());
                    }
                    bd = new BookBuilder();
                    bd.setName(key);
                    bd.addLine(line);
                }

            }
            if (bd.isEmpty()) break;
            if (!cursor.newObject(bd.build())) {
                cursor = new TradeRecord(null, cursor);
                cursor.newObject(bd.build());

            }


        }
    }

    public TradeRecord(@Nullable String title, @Nullable PreUI last) {
        super(title, last);
        Bukkit.getPluginManager().registerEvents(this, VillagerNPC.plugin);
    }

    @Override
    public void refreshUI() {
        HumanEntity[] main = new HumanEntity[0];
        for (TradeRecord i = FIRST_TRADE_RECORD; i.getNext() != null; i = (TradeRecord) i.getNext()) {
            HumanEntity[] viewers = i.getUI().getViewers().toArray(new HumanEntity[0]);
            for (HumanEntity h : viewers) {
                h.closeInventory();
            }
            main = viewers;
        }
        initTradeRecord();
        for (HumanEntity he : main) he.openInventory(FIRST_TRADE_RECORD.getUI());
    }


    @Override
    public boolean newObject(ItemStack object) {
        int first_empty_index = this.getUI().firstEmpty();
        if (first_empty_index > 35) return false;
        this.getUI().setItem(first_empty_index, object);
        return true;
    }

    @EventHandler
    private void InventoryClickEvent(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getInventory().equals(this.getUI())) {
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            switch (e.getRawSlot()) {
                case 45:
                    //上一页
                    if (getLast() != null) {
                        e.getView().close();
                        p.openInventory(getLast().getUI());
                    }
                    break;
                case 52:
                    //重新加载
                    refreshUI();
                    p.sendMessage(PlayerUtil.sendNormalMessage("刷新成功"));
                case 53:
                    //下一页
                    if (this.getNext() != null) {
                        e.getView().close();
                        p.openInventory(this.getNext().getUI());
                    }
                    break;
                default:
                    if (e.getRawSlot() < 36) {
                        if (e.getCurrentItem() != null) p.openBook(e.getCurrentItem());
                    }
            }

        }
    }


}
