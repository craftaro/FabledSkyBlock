package com.songoda.skyblock.levelling.amount;

public class BlockAmount {
    private long amount;

    public BlockAmount(long amount) {
        this.amount = amount;
    }

    public long getAmount() {
        return this.amount;
    }

    public void increaseAmount(long by) {
        this.amount += by;
    }

    public void setAmount(long newValue) {
        this.amount = newValue;
    }
}
