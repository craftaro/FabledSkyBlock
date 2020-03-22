package com.songoda.skyblock.levelling.rework.amount;

public class BlockAmount {

    private long amount;

    public BlockAmount(long amount) {
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }

    public void increaseAmount(long by) {
        this.amount += by;
    }

    public void setAmount(long newValue) {
        this.amount = newValue;
    }

}
