package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;

public class SimpleRepairable implements Repairable {
    private final Material itemMaterial, repairMaterial;
    private final int minimumQuantity, minimumLevel;
    private final short maximumDurability, baseRepairDurability;
    private final byte repairMetadata;
    private final RepairItemType repairItemType;
    private final RepairMaterialType repairMaterialType;
    private final double xpMultiplier;

    protected SimpleRepairable(Material type, Material repairMaterial, byte repairMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, RepairItemType repairItemType, RepairMaterialType repairMaterialType, double xpMultiplier) {
        this.itemMaterial = type;
        this.repairMaterial = repairMaterial;
        this.repairMetadata = repairMetadata;
        this.repairItemType = repairItemType;
        this.repairMaterialType = repairMaterialType;
        this.minimumLevel = minimumLevel;
        this.minimumQuantity = minimumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseRepairDurability = (short) (maximumDurability / minimumQuantity);
        this.xpMultiplier = xpMultiplier;
    }

    @Override
    public Material getItemMaterial() {
        return itemMaterial;
    }

    @Override
    public Material getRepairMaterial() {
        return repairMaterial;
    }

    @Override
    public byte getRepairMaterialMetadata() {
        return repairMetadata;
    }

    @Override
    public RepairItemType getRepairItemType() {
        return repairItemType;
    }

    @Override
    public RepairMaterialType getRepairMaterialType() {
        return repairMaterialType;
    }

    @Override
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    @Override
    public short getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public short getBaseRepairDurability() {
        return baseRepairDurability;
    }

    @Override
    public int getMinimumLevel() {
        return minimumLevel;
    }

    @Override
    public double getXpMultiplier() {
        return xpMultiplier;
    }
}
