package me.tedwoodworth.food;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {
    public static int getHungerAmplifier(Player player) {
        int highestHungerAmplifier = -1;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.HUNGER) && effect.getAmplifier() > highestHungerAmplifier) {
                highestHungerAmplifier = effect.getAmplifier();
            }
        }
        return highestHungerAmplifier;
    }

    public static void setHungerAmplifier(Player player, int amplifier) {
        player.removePotionEffect(PotionEffectType.HUNGER);
        if (amplifier >= 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1000000, amplifier), true);

        }
    }

}
