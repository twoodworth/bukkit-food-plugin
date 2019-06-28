package me.tedwoodworth.food;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rotification implements Listener {
    private static final Random random = new Random();

    public static void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, Rotification::onRotTimerTick, 0L, 1L);
    }

    private static void onRotTimerTick() {
        for (int i = 0; i < 16; i++) {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    rotChunk(chunk);
                }
            }
        }

    }

    private static void rotChunk(Chunk chunk) {
        int x = random.nextInt(16);
        int y = random.nextInt(256);
        int z = random.nextInt(16);

        Block rotBlock = chunk.getBlock(x, y, z);
        BlockState blockState = rotBlock.getState();

        if (blockState instanceof Container) {
            Container container = (Container) blockState;
            Inventory inventory = container.getInventory();
            rotInventory(inventory);
        }

        // TODO rot entities
    }

    private static void rotInventory(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (random.nextDouble() < .125) {
                rotInventoryItem(inventory, slot);
            }

        }
    }

    private static void rotInventoryItem(Inventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);

        if (item != null && canRot(item.getType())) {
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                inventory.setItem(slot, null);
            }

            inventory.addItem(createRotItem());
        }
    }

    private static ItemStack createRotItem() {
        ItemStack rotItem = new ItemStack(Material.ROTTEN_FLESH, 1);
        ItemMeta meta = rotItem.getItemMeta();

        meta.setDisplayName("Rot");
        meta.setLore(createRotItemLore());

        meta.addEnchant(Enchantment.PROTECTION_FIRE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        rotItem.setItemMeta(meta);
        return rotItem;
    }

    public static List<String> createRotItemLore() {
        List<String> lore = new ArrayList<>();
        lore.add("It sat out too long.");
        lore.add("Cannot be eaten.");
        return lore;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEatRot(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (createRotItemLore().equals(meta.getLore())) {
                event.setCancelled(true);
            }
        }
    }

    private static boolean canRot(Material type) {
        switch (type) {
            case GOLDEN_APPLE:
            case GOLDEN_CARROT:
            case ENCHANTED_GOLDEN_APPLE:
            case CHORUS_PLANT:
            case ROTTEN_FLESH:
                return false;

            case SUGAR:
            case WHEAT:
            case PUMPKIN:
            case MELON:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case EGG:
            case OAK_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case JUNGLE_SAPLING:
            case ACACIA_SAPLING:
            case DARK_OAK_SAPLING:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
            case BIRCH_LEAVES:
            case JUNGLE_LEAVES:
            case GRASS:
            case FERN:
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case OXEYE_DAISY:
            case CACTUS:
            case VINE:
            case LILY_PAD:
            case NETHER_WART:
            case HAY_BLOCK:
            case SUNFLOWER:
            case LILAC:
            case LARGE_FERN:
            case TALL_GRASS:
            case ROSE_BUSH:
            case PEONY:
            case SUGAR_CANE:
                return true;

            default:
                return type.isEdible();
        }
    }
}

