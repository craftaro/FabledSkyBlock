package com.songoda.skyblock.menus;

public interface InputMethodSelectlistener {

    public void choose(InputMethod inputMethod);

    public enum InputMethod {
        ALL,
        CUSTOM,
        CANCELED
    }
}
