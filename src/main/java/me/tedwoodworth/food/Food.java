package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Food extends JavaPlugin {
    public static Food plugin;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        plugin = this;
//        SleepManager.startTimer();
        HealthManager.startTimer();
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new Rotification(), this);
        PlayerEventListener.addTokenOfLifeRecipe();
        Rotification.startTimer();
    }
}
