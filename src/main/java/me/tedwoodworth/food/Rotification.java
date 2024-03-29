package me.tedwoodworth.food;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Rotification implements Listener {
    private static final Random random = new Random();

    public static void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Food.plugin, Rotification::onRotTimerTick, 0L, 1L);
    }

    private static void onRotTimerTick() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (int i = 0; i < 32; i++) {
                    rotChunk(chunk);
                }
            }

            for (Entity entity : world.getEntities()) {
                rotEntity(entity);
            }
        }

    }

    private static void rotChunk(Chunk chunk) {
        int x = random.nextInt(16);
        int y = random.nextInt(256);
        int z = random.nextInt(16);

        Block rotBlock = chunk.getBlock(x, y, z);
        BlockState blockState = rotBlock.getState();
        BlockData blockData = rotBlock.getBlockData();

        if (blockState instanceof Container) {
            Container container = (Container) blockState;
            Inventory inventory = container.getInventory();
            rotInventory(inventory);
        }

        if (blockData instanceof Cake) {
            Cake cake = (Cake) blockData;
            int newBites = cake.getBites() + 1;
            if (newBites <= cake.getMaximumBites()) {
                cake.setBites(newBites);
                rotBlock.setBlockData(cake);
            } else {
                rotBlock.setType(Material.AIR);
            }
        }
    }

    private static void rotEntity(Entity entity) {
        if (entity instanceof InventoryHolder) {
            InventoryHolder inventoryHolder = (InventoryHolder) entity;
            if (random.nextDouble() < 25.0 / 16384.0) {
                rotInventory(inventoryHolder.getInventory());
            }
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (random.nextDouble() < 1.0 / 3750.0) {
                rotInventory(player.getEnderChest());
            }
        }

        if (entity instanceof Item) {
            Item item = (Item) entity;
            if (random.nextDouble() < 1.0 / 1200.0) {
                rotDroppedItem(item);
            }
        }
        if (entity instanceof LivingEntity && entity.getTicksLived() > 120000 && entity instanceof Ageable) {
            LivingEntity livingEntity = (LivingEntity) entity;
            switch (entity.getType()) {
                case BAT:
                case COW:
                case PIG:
                case MULE:
                case WOLF:
                case HORSE:
                case LLAMA:
                case SHEEP:
                case DONKEY:
                case OCELOT:
                case PARROT:
                case RABBIT:
                case TURTLE:
                case DOLPHIN:
                case CHICKEN:
                case POLAR_BEAR:
                case MUSHROOM_COW:
                    if (random.nextDouble() < 1.0 / 120000.0) {
                        livingEntity.setHealth(0.0);
                    }
            }
        }
    }

    private static void rotDroppedItem(Item item) {
        if (canRot(item.getItemStack().getType())) {
            ItemStack rotItemStack = createRotItem();
            rotItemStack.setAmount(item.getItemStack().getAmount());
            item.setItemStack(rotItemStack);
        }
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

            Location inventoryLocation = inventory.getLocation();
            if (inventoryLocation != null) {
                Map<Integer, ItemStack> notAdded = inventory.addItem(createRotItem());
                for (ItemStack itemNotAdded : notAdded.values()) {
                    inventoryLocation.getWorld().dropItem(inventoryLocation, itemNotAdded);
                }
            }
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
        lore.add("Food stored in chests stay fresh longer.");
        return lore;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEatRot(PlayerItemConsumeEvent event) {
        if (isItemRot(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIntentoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        boolean clickedTopInventory = event.getRawSlot() < topInventory.getSize();
        if (clickedTopInventory && topInventory.getType() == InventoryType.MERCHANT) {
            if (isItemRot(event.getCursor())) {
                event.setCancelled(true);
            }
        }
    }


    public static boolean isItemRot(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (createRotItemLore().equals(meta.getLore())) {
                return true;
            }
        }

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    private static void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof LivingEntity) {
            if (isItemRot(event.getPlayer().getInventory().getItemInMainHand())) {
                event.setCancelled(true);
            }
        }
    }

    private static boolean canRot(Material type) {
        switch (type) {
            case GOLDEN_APPLE:
            case GOLDEN_CARROT:
            case ENCHANTED_GOLDEN_APPLE:
            case DRIED_KELP:
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
            case CHORUS_FRUIT:
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
            case KELP:
            case HAY_BLOCK:
            case SUNFLOWER:
            case LILAC:
            case LARGE_FERN:
            case TALL_GRASS:
            case ROSE_BUSH:
            case PEONY:
            case SUGAR_CANE:
            case CAKE:
                return true;

            default:
                return type.isEdible();
        }
    }
}

