package com.songoda.skyblock.api.structure;

import com.songoda.skyblock.utils.version.Materials;

import java.util.List;

public interface Structure {

    String getName();

    Materials getMaterials();

    void setMaterials(Materials materials);

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
