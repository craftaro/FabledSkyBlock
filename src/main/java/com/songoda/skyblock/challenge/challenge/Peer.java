package com.songoda.skyblock.challenge.challenge;

public class Peer<E, F> {
    private final E key;
    private final F value;

    public Peer(E key, F value) {
        this.key = key;
        this.value = value;
    }

    public E getKey() {
        return this.key;
    }

    public F getValue() {
        return this.value;
    }
}
