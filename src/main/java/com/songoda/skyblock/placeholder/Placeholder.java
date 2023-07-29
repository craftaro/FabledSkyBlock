package com.songoda.skyblock.placeholder;

public class Placeholder {
    private final String placeholder;
    private final String result;

    public Placeholder(String placeholder, String result) {
        this.placeholder = placeholder;
        this.result = result;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public String getResult() {
        return this.result;
    }
}
