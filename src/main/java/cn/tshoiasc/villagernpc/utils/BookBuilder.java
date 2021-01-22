package cn.tshoiasc.villagernpc.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BookBuilder extends ItemBuilder {
    List<String> pages = new ArrayList<>();
    private int line = 0;
    private int page = 0;

    public boolean isEmpty() {
        return pages.isEmpty();
    }

    public BookBuilder() {
        super(Material.WRITTEN_BOOK);
    }

    @Nullable
    public BookBuilder addLine(String text) {
        if (pages.isEmpty()) pages.add("");
        if (line == 5) {
            page++;
            line = 0;
            pages.add("");
        }
        if (page == 51) return null;
        String txt = pages.get(page);
        pages.set(page, txt + "\n" + text);
        line++;
        return this;
    }

    @Override
    public ItemStack build() {
        this.addLore("§6点击查看交易记录");
        ItemStack item = new ItemStack(material);
        BookMeta im = (BookMeta) item.getItemMeta();
        for (String page : pages)
            im.addPage(page);
        im.setAuthor("NPC");
        im.setTitle(this.name);
        im.setDisplayName(name);
        item.setItemMeta(im);
        return quantity == 0 ? item.asOne() : item.asQuantity(quantity);
    }

    @Override
    public BookBuilder setAmount(int amount) {
        return (BookBuilder) super.setAmount(amount);
    }

    @Override
    public BookBuilder setName(String name) {
        return (BookBuilder) super.setName(name);
    }

    @Override
    public BookBuilder addLore(String lore) {
        return (BookBuilder) super.addLore(lore);
    }

    @Override
    public BookBuilder setLore(int index, String lore) {
        return (BookBuilder) super.setLore(index, lore);
    }

    @Override
    public BookBuilder deleteLore(int index) {
        return (BookBuilder) super.deleteLore(index);
    }
}
