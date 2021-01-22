package cn.tshoiasc.villagernpc.gui.operator;

public class InventoryNotDoException extends IndexOutOfBoundsException{
    public InventoryNotDoException(){
        super("该格子禁止操作！");
    }
}
