package cn.tshoiasc.villagernpc.gui.library;

import cn.tshoiasc.villagernpc.gui.operator.InventoryNotDoException;
import cn.tshoiasc.villagernpc.gui.operator.UI_NPC;
import cn.tshoiasc.villagernpc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * 这是一个前置UI
 * 一个大箱子 最后一行有 上一页 全部开启 打开网页 全部关闭 下一页
 **/
public abstract class PreUI implements Listener {
    //    private static boolean main = false;
    private final Inventory inventory;
    private PreUI last = null;
    private int page = 1;
    private PreUI next = null;
    private String title;

    class Entry {

    }

    public PreUI(@Nullable String title, @Nullable PreUI last) {
        //双向链表联结
        if (last != null) {//不是首位
            this.last = last;
            last.next = this;
            page = last.page + 1;
        }
        this.title = title == null ? "第" + page + "页" : title;
        inventory = Bukkit.createInventory(null, 54, this.title);


        //把按钮放在UI里
        for (int i = 36; i <= 44; i++) {
            this.addButton(i, Button.USELESS);
        }
        if (this.last == null) this.addButton(45, Button.NOT_GO);
        else {
            this.addButton(45, Button.LAST_PAGE);
            this.last.addButton(53, Button.NEXT_PAGE);
        }
        this.addButton(53, Button.NOT_GO);
        this.addButton(52, Button.REFRESH);
        boolean isInstanceOfNPC = this instanceof UI_NPC;
        if (isInstanceOfNPC) {
            this.addButton(47, Button.ALL_OPEN);
            this.addButton(49, Button.NEW_NPC);
            this.addButton(51, Button.ALL_CLOSE);
        }

    }

    public abstract void refreshUI();



    public abstract void allOpen();

    public abstract void allClose();

    public abstract void newNPC(HumanEntity creator);
    public abstract boolean newObject(ItemStack object);
    private void addButton(int index, Button button) {
        inventory.setItem(index, button.getButton());
    }

    public void addButton(int index, Button button, Thread execute) {
        boolean isInstanceOfNPC = this instanceof UI_NPC;
        if (isInstanceOfNPC) {
            if (index != 46 && index != 48 && index != 50) {
                throw new InventoryNotDoException();
            }
        } else {
            if (index < 46 || index > 51) throw new InventoryNotDoException();
        }

        addButton(index, button);
    }




//    public static PreUI findUI(Inventory inv) {
//        PreUI cursor = TraderObjectStorage.FIRST_PREUI;
//        PreUI end = cursor.getEnd();
//        while (cursor.next != null) {
//            if (cursor.getUI().equals(inv)) return cursor;
//            cursor = cursor.next;
//        }
//        return null;
//    }

    public int getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    //满了返回-1
    public int getFirstEmpty() {
        return this.inventory.firstEmpty() < 36 ? this.inventory.firstEmpty() : -1;
    }

    public PreUI getEnd() {
        PreUI cur = this;
        while (true) {
            if (cur.next == null) {
                return cur;

            }
            cur = cur.getNext();
        }
    }

    public PreUI getFirst() {
        PreUI cur = this;
        while (true) {
            if (cur.last == null) {
                return cur;
            }
            cur = cur.getLast();
        }
    }

    public int getSize() {
        PreUI ui = this.getFirst();
        int i = 1;
        for (; ui.next != null; i++) {
            ui = ui.next;
        }
        return i;
    }

    public Inventory getUI() {
        return inventory;
    }

    public PreUI getLast() {
        return this.last;
    }

    //设置链表上一接点
    public void setLast(PreUI last) {
        this.last = last;
        last.next = this;
    }

    public PreUI getNext() {
        return this.next;
    }

    public void setNext(@Nullable PreUI next) {
        this.next = next;
        if (next == null) this.getUI().setItem(53, new ItemBuilder(Material.GLASS_PANE).setName("§7§l没有内容了").build());
        else {
            this.getUI().setItem(53, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName("§5§l下一页").build());
            next.page = this.page + 1;
        }
    }
}
