package cn.tshoiasc.villagernpc.object;

import cn.tshoiasc.villagernpc.gui.AllTraderListUI;
import cn.tshoiasc.villagernpc.gui.TraderEditUI;
import cn.tshoiasc.villagernpc.gui.library.PreUI;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;

public class TraderObjectStorage {
    public int currentTrade;
    public static final List<TraderObjectStorage> VILLAGER = new ArrayList<>();
    public static final List<TraderObjectStorage> TRADER = new ArrayList<>();
    public static AllTraderListUI FIRST_PREUI;
    boolean isOpen;
    private PreUI current_edit_preui;
    private TraderEditUI editui;
    private UUID uuid;
    private String name;
    private List<MerchantRecipe> merchant_recipes;
    private String lastTime;
    private Location createLoc;
    private String creator;
    private boolean isDead = false;


    public TraderObjectStorage(boolean isOpen, UUID uuid, @Nullable String name, String time, String creator, Location createLoc) {
        this.isOpen = isOpen;
        this.uuid = uuid;
        //this.loc = loc;
        this.name = name;
        this.lastTime = time;
        this.creator = creator;
        this.createLoc = createLoc;
    }

    public static TraderObjectStorage getVillagerObjectStorage(WanderingTrader e) {
        for (TraderObjectStorage vos : TRADER) {
            if (e.getUniqueId().compareTo(vos.uuid) == 0) return vos;
        }
        return null;
    }

    public static void newTrader(WanderingTrader e, HumanEntity p) {
        TraderObjectStorage vos = new TraderObjectStorage(false, e.getUniqueId(), e.getCustomName() == null ? "NPC商人" : e.getCustomName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), p.getName(), e.getLocation());
        e.setRecipes(new ArrayList<MerchantRecipe>());
        e.setInvisible(false);
        vos.editui = new TraderEditUI(vos);

        TRADER.add(vos);
        AllTraderListUI.addTrader(vos);
        vos.merchant_recipes = e.getRecipes();


    }


    public static void initTraderObjectStorage(YamlConfiguration TraderYAML) {
        Map<String, Object> keys = TraderYAML.getValues(false);
        Collection<Object> key = keys.values();
        for (Object i : key) {
            MemorySection inf = (MemorySection) i;
            UUID uuid = UUID.fromString(inf.getString("UUID"));
            String name = inf.getString("Name");
            String creator = (String) inf.get("Creator");
            String lastTime = (String) keys.get("LastModifyTime");
            boolean isOpen = inf.getBoolean("isOpen");
            Location loc = inf.getLocation("createLocation");
            TraderObjectStorage n = new TraderObjectStorage(isOpen, uuid, name, lastTime, creator, loc);
            List<MerchantRecipe> mrlist = new ArrayList<>();
            MemorySection ms = (MemorySection) inf.get("MerchantRecipe");
            for (int c = 1; c <= 6; c++) {
                MemorySection tms = null;
                if (ms != null) {
                    tms = (MemorySection) ms.get("" + c);
                }
                if (tms == null) {
                    n.setMerchant_recipes(new ArrayList<MerchantRecipe>());
                    break;
                }
                ItemStack from = tms.getItemStack("from");
                ItemStack to = tms.getItemStack("to");
                MerchantRecipe mr = new MerchantRecipe(to, Integer.MAX_VALUE);
                List<ItemStack> l = new ArrayList<>();
                l.add(from);
                mr.setIngredients(l);
                mrlist.add(mr);
            }
            n.setMerchant_recipes(mrlist);
            n.editui = new TraderEditUI(n);
            TRADER.add(n);

        }
        FIRST_PREUI = AllTraderListUI.initTradeList(TRADER);

    }

    public void setEditui(TraderEditUI ui) {
        this.editui = ui;
    }

    public TraderEditUI getEditui(@Nullable PreUI preui) {
        current_edit_preui = preui;
        if (preui == null) current_edit_preui = FIRST_PREUI;
        return editui;
    }

    //    public String getName(){
//
//    }
    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast() {
        return lastTime;
    }

    public List<MerchantRecipe> getMerchant_recipes() {
        return merchant_recipes;
    }

    public void setMerchant_recipes(List<MerchantRecipe> merchant_recipes) {
        this.merchant_recipes = merchant_recipes;
    }

    public Location getCreateLoc() {
        return createLoc;
    }

    public void setCreateLoc(Location createLoc) {
        this.createLoc = createLoc;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Name", this.name == null ? "商人" : this.name);
        result.put("Creator", this.creator);
        result.put("LastModifyTime", this.lastTime);
        result.put("isOpen", this.isOpen);
        result.put("createLocation", this.createLoc);
        return result;
    }

    public PreUI getCurrent_edit_preui() {
        return current_edit_preui;
    }
}
