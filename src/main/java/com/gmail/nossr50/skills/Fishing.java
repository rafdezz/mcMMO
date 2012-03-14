package com.gmail.nossr50.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.config.LoadTreasures;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.locale.mcLocale;

public class Fishing {

    /**
     * Get the player's current fishing loot tier.
     *
     * @param PP The profile of the player
     * @return the player's current fishing rank
     */
    public static int getFishingLootTier(PlayerProfile PP) {
        int level = PP.getSkillLevel(SkillType.FISHING);
        int fishingTier;

        if (level >= LoadProperties.fishingTier5) {
            fishingTier = 5;
        }
        else if (level >= LoadProperties.fishingTier4) {
            fishingTier = 4;
        }
        else if (level >= LoadProperties.fishingTier3) {
            fishingTier =  3;
        }
        else if (level >= LoadProperties.fishingTier2) {
            fishingTier =  2;
        }
        else {
            fishingTier =  1;
        }

        return fishingTier;
    }

    /**
     * Get item results from Fishing.
     *
     * @param player The player that was fishing
     * @param event The event to modify
     */
    private static void getFishingResults(Player player, PlayerFishEvent event) {
        PlayerProfile PP = Users.getProfile(player);
        List<FishingTreasure> rewards = new ArrayList<FishingTreasure>();
        Item theCatch = (Item) event.getCaught();

        switch (getFishingLootTier(PP)) {
        case 1:
            rewards = LoadTreasures.fishingRewardsTier1;
            break;

        case 2:
            rewards = LoadTreasures.fishingRewardsTier2;
            break;

        case 3:
            rewards = LoadTreasures.fishingRewardsTier3;
            break;

        case 4:
            rewards = LoadTreasures.fishingRewardsTier4;
            break;

        case 5:
            rewards = LoadTreasures.fishingRewardsTier5;
            break;

        default:
            break;
        }

        if (LoadProperties.fishingDrops) {
            FishingTreasure treasure = rewards.get((int) (Math.random() * rewards.size()));

            if (Math.random() * 100 <= treasure.getDropChance()) {
                Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp(), player);
                theCatch.setItemStack(treasure.getDrop());
            }
        }
        else {
            theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
        }

        theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change durability to random value

        m.mcDropItem(player.getLocation(), new ItemStack(Material.RAW_FISH)); //Always drop a fish
        PP.addXP(SkillType.FISHING, LoadProperties.mfishing, player);
        Skills.XpCheckSkill(SkillType.FISHING, player);
    }

    /**
     * Process results from Fishing.
     *
     * @param event The event to modify
     */
    public static void processResults(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerProfile PP = Users.getProfile(player);

        getFishingResults(player, event);
        Item theCatch = (Item)event.getCaught();

        if (theCatch.getItemStack().getType() != Material.RAW_FISH) {
            final int ENCHANTMENT_CHANCE = 10;
            boolean enchanted = false;
            ItemStack fishingResults = theCatch.getItemStack();

            player.sendMessage(mcLocale.getString("Fishing.ItemFound"));
            if (Repair.isArmor(fishingResults) || Repair.isTools(fishingResults)) {
                if (Math.random() * 100 <= ENCHANTMENT_CHANCE) {
                    for (Enchantment newEnchant : Enchantment.values()) {
                        if (newEnchant.canEnchantItem(fishingResults)) {
                            Map<Enchantment, Integer> resultEnchantments = fishingResults.getEnchantments();

                            for (Enchantment oldEnchant : resultEnchantments.keySet()) {
                                if (oldEnchant.conflictsWith(newEnchant)) {
                                    return;
                                }
                            }

                            /* Actual chance to have an enchantment is related to your fishing skill */
                            if (Math.random() * 15 < Fishing.getFishingLootTier(PP)) {
                                enchanted = true;
                                int randomEnchantLevel = (int) (Math.random() * newEnchant.getMaxLevel()) + 1;

                                if (randomEnchantLevel < newEnchant.getStartLevel()) {
                                    randomEnchantLevel = newEnchant.getStartLevel();
                                }

                                fishingResults.addEnchantment(newEnchant, randomEnchantLevel);
                            }
                        }
                    }
                }
            }

            if (enchanted) {
                player.sendMessage(mcLocale.getString("Fishing.MagicFound"));
            }
        }
    }

    /**
     * Shake a mob, have them drop an item.
     *
     * @param event The event to modify
     */
    public static void shakeMob(PlayerFishEvent event) {
        final int DROP_NUMBER = (int) (Math.random() * 101);

        LivingEntity le = (LivingEntity) event.getCaught();
        EntityType type = le.getType();
        Location loc = le.getLocation();

        switch (type) {
        case BLAZE:
            m.mcDropItem(loc, new ItemStack(Material.BLAZE_ROD, 1));
            break;

        case CAVE_SPIDER:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.SPIDER_EYE, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.STRING, 1));
            }
            break;

        case CHICKEN:
            if (DROP_NUMBER > 66) {
                m.mcDropItem(loc, new ItemStack(Material.FEATHER, 1));
            }
            else if (DROP_NUMBER > 33) {
                m.mcDropItem(loc, new ItemStack(Material.RAW_CHICKEN, 1));
                }
            else {
                m.mcDropItem(loc, new ItemStack(Material.EGG, 1));
            }
            break;

        case COW:
            if (DROP_NUMBER > 99) {
                m.mcDropItem(loc, new ItemStack(Material.MILK_BUCKET, 1));
            }
            else if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.LEATHER, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.RAW_BEEF, 1));
            }
            break;

        case CREEPER:
            m.mcDropItem(loc, new ItemStack(Material.SULPHUR, 1));
            break;

        case ENDERMAN:
            m.mcDropItem(loc, new ItemStack(Material.ENDER_PEARL, 1));
            break;

        case GHAST:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.SULPHUR, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.GHAST_TEAR, 1));
            }
            break;

        case MAGMA_CUBE:
            m.mcDropItem(loc, new ItemStack(Material.MAGMA_CREAM, 1));
            break;

        case MUSHROOM_COW:
            if (DROP_NUMBER > 99) {
                m.mcDropItem(loc, new ItemStack(Material.MILK_BUCKET, 1));
            }
            else if (DROP_NUMBER > 98) {
                m.mcDropItem(loc, new ItemStack(Material.MUSHROOM_SOUP, 1));
            }
            else if (DROP_NUMBER > 66) {
                m.mcDropItem(loc, new ItemStack(Material.LEATHER, 1));
            }
            else if (DROP_NUMBER > 33) {
                m.mcDropItem(loc, new ItemStack(Material.RAW_BEEF, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.RED_MUSHROOM, 3));
            }
            break;

        case PIG:
            m.mcDropItem(loc, new ItemStack(Material.PORK, 1));
            break;

        case PIG_ZOMBIE:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.ROTTEN_FLESH, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.GOLD_NUGGET, 1));
            }
            break;

        case SHEEP:
            Sheep sheep = (Sheep) le;
            
            if (!sheep.isSheared()) {
                Wool wool = new Wool();
                wool.setColor(sheep.getColor());

                ItemStack theWool = wool.toItemStack();
                theWool.setAmount((int)(Math.random() * 6));

                m.mcDropItem(loc, theWool);
                sheep.setSheared(true);
            }
            break;

        case SKELETON:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.BONE, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.ARROW, 3));
            }
            break;

        case SLIME:
            m.mcDropItem(loc, new ItemStack(Material.SLIME_BALL, 1));
            break;

        case SNOWMAN:
            if (DROP_NUMBER > 99) {
                m.mcDropItem(loc, new ItemStack(Material.PUMPKIN, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.SNOW_BALL, 5));
            }
            break;

        case SPIDER:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.SPIDER_EYE, 1));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.STRING, 1));
            }
            break;

        case SQUID:
            m.mcDropItem(loc, new ItemStack(Material.INK_SACK, 1, (byte) 0x0, DyeColor.BLACK.getData()));
            break;

        case ZOMBIE:
            m.mcDropItem(loc, new ItemStack(Material.ROTTEN_FLESH, 1));
            break;

        default:
            break;
        }

        Combat.dealDamage(le, 1);
    }
}
