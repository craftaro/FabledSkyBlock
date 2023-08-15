package com.craftaro.skyblock.levelling.amount;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;


public class AmountMaterialPair {
    private final long amount;
    private final XMaterial material;

    public AmountMaterialPair(XMaterial type, long amount) {
        this.amount = amount;
        this.material = type;
    }

    public long getAmount() {
        return this.amount;
    }

    public XMaterial getType() {
        return this.material;
    }
}
