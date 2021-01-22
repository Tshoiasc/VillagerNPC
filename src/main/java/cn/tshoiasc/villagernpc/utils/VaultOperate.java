package cn.tshoiasc.villagernpc.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultOperate {
    private static Economy economy;
    private static boolean supportVault = false;

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = (Economy) economyProvider.getProvider();
        }
        supportVault = economy != null;
        return supportVault;
    }

    public static boolean isSupportVault() {
        if (economy != null) {
            return true;
        }
        return setupEconomy();
    }

    public static void give(String player, double money) {
        if (economy == null) {
            throw new UnsupportedOperationException("Vault错误");
        }
        economy.depositPlayer(player, money);
    }

    public static void take(String player, double money) {
        if (economy == null) {
            throw new UnsupportedOperationException("Vault错误");
        }
        economy.withdrawPlayer(player, money);
    }

    public static double getBalance(String player) {
        if (economy == null) {
            throw new UnsupportedOperationException("Vault错误");
        }
        return economy.getBalance(player);
    }

    public static boolean has(String player, double money) {
        if (economy == null) {
            throw new UnsupportedOperationException("Vault错误");
        }
        return getBalance(player) >= money;
    }
}