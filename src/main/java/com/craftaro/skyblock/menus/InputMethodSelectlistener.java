package com.craftaro.skyblock.menus;

public interface InputMethodSelectlistener {
    void choose(InputMethod inputMethod);

    enum InputMethod {
        ALL,
        CUSTOM,
        CANCELED
    }
}
