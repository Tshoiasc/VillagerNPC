package cn.tshoiasc.villagernpc.gui.operator;

import cn.tshoiasc.villagernpc.gui.library.PreUI;
import org.bukkit.entity.HumanEntity;

import javax.annotation.Nullable;

public abstract class UI_OTHER extends PreUI {
    public UI_OTHER(@Nullable String title, @Nullable PreUI last) {
        super(title, last);
    }

    @Override
    public void newNPC(HumanEntity p) {
    }


    @Override
    public void allOpen() {

    }

    @Override
    public void allClose() {

    }

}
