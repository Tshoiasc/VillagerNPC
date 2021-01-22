package cn.tshoiasc.villagernpc.gui.library;

import cn.tshoiasc.villagernpc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Button {
    ALL_CLOSE("全部关闭"), ALL_OPEN("全部开启"), LAST_PAGE("上一页"), NEXT_PAGE("下一页"), REFRESH("刷新"), USELESS("占位符"), NOT_GO("页码到头"), NEW_NPC("新建NPC");
    private ItemStack button;

    public ItemStack getButton() {
        return button;
    }

    private Button(String note) {
        switch (note) {
            case "全部关闭":
                button = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§4§l全部关闭").build();
                break;
            case "全部开启":
                button = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§l全部开启").build();
                break;
            case "上一页":
                button = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName("§5§l上一页").build();
                break;
            case "下一页":
                button = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName("§5§l下一页").build();
                break;
            case "刷新":
                button = new ItemBuilder(Material.STRING).setName("§b§l刷新").build();
                break;
            case "占位符":
                button = new ItemBuilder(Material.IRON_BARS).setName("§7占位符").build();
                break;
            case "页码到头":
                button = new ItemBuilder(Material.GLASS_PANE).setName("§7§l没有内容了").build();
                break;
            case "新建NPC":
                button = new ItemBuilder(Material.PAINTING).setName("§e§l新建NPC").build();
            default:
        }
    }

}
