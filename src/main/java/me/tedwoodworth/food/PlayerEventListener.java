package me.tedwoodworth.food;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class PlayerEventListener implements Listener {
    private final Map<UUID, Location> deathLocations = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Food.plugin, () -> afterPlayerRespawn(player), 1L);
        }
    }

    private void afterPlayerRespawn(Player player) {
        player.setGameMode(GameMode.ADVENTURE);

        Location deathLocation = deathLocations.get(player.getUniqueId());
        if (deathLocation != null) {
            player.teleport(deathLocation);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        deathLocations.put(player.getUniqueId(), player.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (isItemTokenOfLife(player.getInventory().getItemInMainHand()) &&
                event.getRightClicked() instanceof Player &&
                ((Player) event.getRightClicked()).getGameMode() == GameMode.ADVENTURE &&
                event.getPlayer().getHealth() > 3.0 &&
                event.getPlayer().getFoodLevel() >= 3
                ) {
            ((Player) event.getRightClicked()).setGameMode(GameMode.SURVIVAL);
            ((Player) event.getRightClicked()).removePotionEffect(PotionEffectType.INVISIBILITY);
            ((Player) event.getRightClicked()).removePotionEffect(PotionEffectType.GLOWING);
            ((Player) event.getRightClicked()).removePotionEffect(PotionEffectType.WEAKNESS);
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
            ((Player) event.getRightClicked()).setHealth(6);
            ((Player) event.getRightClicked()).setFoodLevel(6);
            event.getPlayer().setHealth(event.getPlayer().getHealth() - 6.0);
            event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() - 6);
            event.getRightClicked().getWorld().playSound(event.getRightClicked().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.0f);
            event.getRightClicked().getWorld().strikeLightningEffect(event.getRightClicked().getLocation());
        }
    }

    public static ItemStack createTokenOfLifeItem() {
        ItemStack tokenOfLifeItem = new ItemStack(Material.SUNFLOWER, 1);
        ItemMeta meta = tokenOfLifeItem.getItemMeta();

        meta.setDisplayName("Token of Life");
        meta.setLore(createTokenOfLifeItemLore());

        meta.addEnchant(Enchantment.PROTECTION_FIRE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tokenOfLifeItem.setItemMeta(meta);
        return tokenOfLifeItem;

    }

    private static List<String> createTokenOfLifeItemLore() {
        List<String> lore = new ArrayList<>();
        lore.add("Gives life to those that are dead.");
        lore.add("Right-click a ghost to bring them back to life.");
        lore.add("Single use only.");
        return lore;
    }

    private static boolean isItemTokenOfLife(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (createTokenOfLifeItemLore().equals(meta.getLore())) {
                return true;
            }
        }

        return false;
    }

    public static void addTokenOfLifeRecipe() {
        ItemStack tokenOfLife = new ItemStack(Material.SUNFLOWER, 1);
        ItemMeta tokenMeta = tokenOfLife.getItemMeta();
        tokenMeta.setDisplayName("Token of Life");
        tokenMeta.setLore(createTokenOfLifeItemLore());
        tokenMeta.addEnchant(Enchantment.PROTECTION_FIRE, 1, true);
        tokenMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tokenOfLife.setItemMeta(tokenMeta);

        NamespacedKey tokenKey = new NamespacedKey(Food.plugin, "tokenRecipe");
        ShapedRecipe tokenRecipe = new ShapedRecipe(tokenKey, tokenOfLife);

        tokenRecipe.shape("***", "*%*", "***");

        tokenRecipe.setIngredient('*', Material.GOLD_INGOT);
        tokenRecipe.setIngredient('%', Material.HEART_OF_THE_SEA);

        getServer().addRecipe(tokenRecipe);

    }

    @EventHandler(ignoreCancelled = true)
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.ADVENTURE) {
            player.setFlySpeed(0.04f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 255, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 255, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1000000, 255, false, false, false));
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
    private void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getTicksLived() < 24000 && entity instanceof Ageable) {
                LivingEntity livingEntity = (LivingEntity) entity;
                switch (entity.getType()) {
                    case COW:
                    case PIG:
                    case MULE:
                    case HORSE:
                    case LLAMA:
                    case SHEEP:
                    case DONKEY:
                    case OCELOT:
                    case PARROT:
                    case RABBIT:
                    case CHICKEN:
                    case MUSHROOM_COW:
                }
                ((Ageable) entity).setBaby();
            }
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

        if (isItemTokenOfLife(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);

            //Fixes a bug where the top half of the sunflower appears to be placed client-side.
            Location updateLocation = event.getClickedBlock().getLocation()
                    .add(event.getBlockFace().getDirection())
                    .add(0, 1, 0);
            player.sendBlockChange(updateLocation, player.getWorld().getBlockAt(updateLocation).getBlockData());
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
