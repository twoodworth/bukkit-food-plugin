package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class HealthManager {
    private static final long HEALTH_DELAY = 1L;
    private static final double HEALTH_PER_TICK = 0.0005;
    private static final long HUNGER_2_HEALTH_DELAY = 2000L;
    private static final long HUNGER_3_HEALTH_DELAY = 400L;
    private static final long SICKNESS_DELAY = 200L;

    private static final Random random = new Random();

    public static void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, HealthManager::onHealthTimerTick, 0L, HEALTH_DELAY);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, HealthManager::onHunger2TimerTick, 0L, HUNGER_2_HEALTH_DELAY);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, HealthManager::onHunger3TimerTick, 0L, HUNGER_3_HEALTH_DELAY);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, HealthManager::onSicknessTimerTick, 0L, SICKNESS_DELAY);

    }

    private static void onHunger2TimerTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasHungerEffect(player, 2)) {
                changeHealth(player, -1);
            }
        }
    }

    private static void onHunger3TimerTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasHungerEffect(player, 3)) {
                changeHealth(player, -1);
            }
        }
    }

    private static void onHealthTimerTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasHungerEffect(player, 0)) {
                changeHealth(player, 0.2 * HEALTH_PER_TICK);
            } else if (hasHungerEffect(player, 1)) {
                // no-op
            } else if (player.getFoodLevel() > 6 && !player.isDead() && !hasHungerEffect(player, -1)) {
                changeHealth(player, HEALTH_PER_TICK);
            }
        }
    }

    private static boolean hasHungerEffect(Player player, int amplifier) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.HUNGER) && (amplifier == -1 || effect.getAmplifier() == amplifier)) {
                return true;
            }
        }
        return false;
    }

    private static void changeHealth(Player player, double amount) {
        double newHealth = player.getHealth() + amount;
        if (newHealth < 0) {
            newHealth = 0;
        } else if (newHealth > player.getMaxHealth()) {
            newHealth = player.getMaxHealth();
        }
        player.setHealth(newHealth);
    }

    private static void randomlyAdjustHungerAmplifier(Player player) {
        int currentAmplifier = PlayerUtil.getHungerAmplifier(player);
        if (currentAmplifier >= 0 && random.nextDouble() < 1.0 / 3.0) {
            if (player.getFoodLevel() > 14) {
                PlayerUtil.setHungerAmplifier(player, currentAmplifier - 1);
            } else if (player.getFoodLevel() < 7 && currentAmplifier != 120) {
                PlayerUtil.setHungerAmplifier(player, currentAmplifier + 1);
            }
        }
    }

    private static void onSicknessTimerTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            randomlyAdjustHungerAmplifier(player);
        }
    }
}
