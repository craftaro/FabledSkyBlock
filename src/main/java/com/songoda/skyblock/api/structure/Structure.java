package com.songoda.skyblock.api.structure;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;

import java.util.List;

public interface Structure {
    String getName();

    XMaterial getMaterial();

    void setMaterial(XMaterial material);

    String getOverworldFile();

    void setOverworldFile(String file);

    String getNetherFile();

    void setNetherFile(String file);

    String getDisplayname();

    void setDisplayname(String displayName);

    boolean isPermission();

    String getPermission();

    void setPermission(boolean permission);

    List<String> getDescription();

    void addLine(String line);

    void removeLine(int index);

    List<String> getCommands();

    void addCommand(String command);

    void removeCommand(int index);
}
