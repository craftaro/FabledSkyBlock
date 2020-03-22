package com.songoda.skyblock.levelling.rework.amount;

import com.songoda.skyblock.utils.version.Materials;

public class AmountMaterialPair {

    private final long amount;
    private final Materials material;

    public AmountMaterialPair(Materials type, long amount) {
        this.amount = amount;
        this.material = type;
    }

    public long getAmount() {
        return amount;
    }

    public Materials getType() {
        return material;
    }

}
