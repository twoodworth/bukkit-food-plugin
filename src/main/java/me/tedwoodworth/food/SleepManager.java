package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SleepManager implements Listener {
    private static final long TICKS_PER_NIGHT = 12000L;
    private static final double PERCENT_OF_MAX_HEALTH = .75;
    private static final long HEALS_PER_NIGHT = 600L;
    private static final long HEAL_DELAY = TICKS_PER_NIGHT / HEALS_PER_NIGHT;

    private static final double PLAYER_MAX_HEALTH = 20;

    public static void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, SleepManager::onSleepTimerTick, 0L, HEAL_DELAY);
    }

    private static void onSleepTimerTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isSleeping()) {
                double maxHealth = player.getMaxHealth();
                double newHealth = player.getHealth() + (maxHealth * PERCENT_OF_MAX_HEALTH / HEALS_PER_NIGHT);
                if (newHealth > maxHealth) {
                    newHealth = maxHealth;
                }
                player.setHealth(newHealth);
            }
        }
    }

}
