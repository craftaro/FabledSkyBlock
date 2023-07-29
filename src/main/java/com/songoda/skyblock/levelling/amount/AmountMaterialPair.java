package com.songoda.skyblock.levelling.amount;

import com.craftaro.core.compatibility.CompatibleMaterial;


public class AmountMaterialPair {
    private final long amount;
    private final CompatibleMaterial material;

    public AmountMaterialPair(CompatibleMaterial type, long amount) {
        this.amount = amount;
        this.material = type;
    }

    public long getAmount() {
        return this.amount;
    }

    public CompatibleMaterial getType() {
        return this.material;
    }
}
