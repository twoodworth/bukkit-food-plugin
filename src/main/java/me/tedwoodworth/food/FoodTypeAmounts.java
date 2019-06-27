package me.tedwoodworth.food;

import org.bukkit.Material;

public class FoodTypeAmounts {

    public static int getRealisticFoodAmount(Material food) {
        switch (food) {
            case COOKED_MUTTON:
                return 20;
            case COOKED_BEEF:
                return 20;
            case RABBIT_STEW:
                return 20;
            case MUTTON:
                return 20;
            case BEEF:
                return 19;
            case COOKED_RABBIT:
                return 17;
            case COOKED_PORKCHOP:
                return 13;
            case RABBIT:
                return 11;
            case BEETROOT_SOUP:
                return 9;
            case PORKCHOP:
                return 9;
            case COOKED_CHICKEN:
                return 8;
            case CHICKEN:
                return 6;
            case BREAD:
                return 5;
            case ROTTEN_FLESH:
                return 3;
            case SPIDER_EYE:
                return 3;
            case PUMPKIN_PIE:
                return 3;
            case APPLE:
                return 2;
            case POISONOUS_POTATO:
                return 1;
            case BEETROOT:
                return 1;
            case TROPICAL_FISH:
                return 1;
            case BAKED_POTATO:
                return 1;
            case COOKIE:
                return 1;
            case POTATO:
                return 1;
            case CARROT:
                return 1;
            case DRIED_KELP:
                return 1;
            case MUSHROOM_STEW:
                return 1;
            case PUFFERFISH:
                return 1;
            case COOKED_SALMON:
                return 1;
            case SALMON:
                return 1;
            case COOKED_COD:
                return 1;
            case COD:
                return 1;
            case CHORUS_FRUIT:
                return 1;
            case MELON_SLICE:
                return 1;
            case GOLDEN_APPLE:
                return 2;
            case ENCHANTED_GOLDEN_APPLE:
                return 2;
            case GOLDEN_CARROT:
                return 2;
            default:
                if (food.isEdible()) {
                    System.out.println("realistic food type not handled: " + food);
                }
                return 0;

        }
    }
}
