package com.songoda.skyblock.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatComponent {
    private final TextComponent textComponent;

    public ChatComponent(String text, boolean bold, ChatColor color, ClickEvent clickEvent, HoverEvent hoverEvent) {
        this.textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
        this.textComponent.setBold(bold);

        if (color != null) {
            this.textComponent.setColor(color);
        }

        if (clickEvent != null) {
            this.textComponent.setClickEvent(clickEvent);
        }

        if (hoverEvent != null) {
            this.textComponent.setHoverEvent(hoverEvent);
        }
    }

    public TextComponent addExtra(ChatComponent chatComponent) {
        this.textComponent.addExtra(chatComponent.getTextComponent());

        return this.textComponent;
    }

    public ChatComponent addExtra(TextComponent textComponent) {
        this.textComponent.addExtra(textComponent);

        return this;
    }

    public ChatComponent addExtraChatComponent(ChatComponent chatComponent) {
        this.textComponent.addExtra(chatComponent.getTextComponent());

        return this;
    }

    public TextComponent getTextComponent() {
        return this.textComponent;
    }
}
