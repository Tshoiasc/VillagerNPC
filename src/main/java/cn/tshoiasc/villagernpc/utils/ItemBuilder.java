package cn.tshoiasc.villagernpc.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    List<String> lore;
    int quantity;
    String name;
    Material material;

    public ItemBuilder(Material material) {
        lore = new ArrayList<String>();
        this.material = material;

    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.setLore(lore);
        im.setDisplayName(name);
        item.setItemMeta(im);
        return quantity == 0 ? item.asOne() : item.asQuantity(quantity);
    }

    public ItemBuilder setAmount(int amount) {
        this.quantity = amount;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder setLore(int index, String lore) {
        if (index + 1 > this.lore.size())
            throw new ArrayIndexOutOfBoundsException("lore越界");
        else {
            this.lore.set(index, lore);
        }
        return this;
    }

    public ItemBuilder deleteLore(int index) {
        if (index + 1 > this.lore.size()) throw new ArrayIndexOutOfBoundsException("lore越界");
        else {
            this.lore.remove(index);
        }
        return this;
    }

}
