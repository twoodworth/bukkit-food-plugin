package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.xml.bind.Marshaller;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        System.out.println("respawn");
        if (player.getGameMode() == GameMode.SURVIVAL) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Food.plugin, () -> afterPlayerRespawn(player), 1L);
        }
    }

    public void afterPlayerRespawn(Player player) {
        player.setGameMode(GameMode.ADVENTURE);

    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.ADVENTURE) {
            player.setFlySpeed(0.04f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 255, false, true, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255, false, true, false));
        } else {
            player.setFlySpeed(0.1f);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
                player.setFireTicks(0);

            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        Entity attacker = event.getAttacker();
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entered = event.getEntered();
        if (entered instanceof Player) {
            Player player = (Player) entered;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }
}
