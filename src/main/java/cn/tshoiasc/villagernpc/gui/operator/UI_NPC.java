package cn.tshoiasc.villagernpc.gui.operator;

import cn.tshoiasc.villagernpc.gui.library.PreUI;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class UI_NPC extends PreUI {
    public UI_NPC(@Nullable String title, @Nullable PreUI last) {
        super(title, last);
    }

    @Override
    public boolean newObject(ItemStack im) {
        return false;
    }
}
