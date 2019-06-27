package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealthManager {
    private static final long HEALTH_DELAY = 1L;
    private static final double HEALTH_PER_TICK = 0.0005;


    public static void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, HealthManager::onHealthTimerTick, 0L, HEALTH_DELAY);
    }

    private static void onHealthTimerTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getFoodLevel() > 6 && !player.isDead()) {
                double maxHealth = player.getMaxHealth();
                double newHealth = player.getHealth() + HEALTH_PER_TICK;
                if (newHealth > maxHealth) {
                    newHealth = maxHealth;
                }
                player.setHealth(newHealth);
            }
        }
    }

}
