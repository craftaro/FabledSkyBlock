package com.songoda.skyblock.api.structure;

import com.songoda.core.compatibility.CompatibleMaterial;

import java.util.List;

public interface Structure {
    String getName();

    CompatibleMaterial getMaterial();

    void setMaterial(CompatibleMaterial material);

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
