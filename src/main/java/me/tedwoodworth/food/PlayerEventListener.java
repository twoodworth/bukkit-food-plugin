package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Food.plugin, () -> afterPlayerRespawn(player), 1L);
        }
    }

    private void afterPlayerRespawn(Player player) {
        player.setGameMode(GameMode.ADVENTURE);

    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.setBedSpawnLocation(player.getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.ADVENTURE) {
            player.setFlySpeed(0.04f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 255, false, true, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255, false, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 255, false, true, false));
        } else {
            player.setFlySpeed(0.1f);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityBreedEvent(EntityBreedEvent event) {
        Entity entity = event.getEntity();
        if (entity.getTicksLived() > 48000) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Ageable) {
            ((Ageable) entity).setBaby();
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityTarget(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
                player.setFireTicks(0);

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onVehicleDamage(VehicleDamageEvent event) {
        Entity attacker = event.getAttacker();
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onVehicleEnter(VehicleEnterEvent event) {
        Entity entered = event.getEntered();
        if (entered instanceof Player) {
            Player player = (Player) entered;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        int realisticAmount = FoodTypeAmounts.getRealisticFoodAmount(item.getType());
        adjustFoodLevel(player, realisticAmount);

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onCakeSliceConsume(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.CAKE) {
                int realisticAmount = FoodTypeAmounts.getRealisticFoodAmount(type);
                adjustFoodLevel(event.getPlayer(), realisticAmount);
            }
        }
    }

    private void adjustFoodLevel(Player player, int realisticAmount) {
        int foodLevel = player.getFoodLevel();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Food.plugin, () -> {
            int adjustedFoodLevel = foodLevel + realisticAmount;
            if (adjustedFoodLevel > 20) {
                adjustedFoodLevel = 20;
            } else if (adjustedFoodLevel < 0) {
                adjustedFoodLevel = 0;
            }

            player.setFoodLevel(adjustedFoodLevel);
            player.setSaturation(0);
        }, 1L);
    }
}
