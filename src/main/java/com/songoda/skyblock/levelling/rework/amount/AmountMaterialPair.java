package com.songoda.skyblock.levelling.rework.amount;

import com.songoda.core.compatibility.CompatibleMaterial;
 

public class AmountMaterialPair {

    private final long amount;
    private final CompatibleMaterial material;

    public AmountMaterialPair(CompatibleMaterial type, long amount) {
        this.amount = amount;
        this.material = type;
    }

    public long getAmount() {
        return amount;
    }

    public CompatibleMaterial getType() {
        return material;
    }

}
