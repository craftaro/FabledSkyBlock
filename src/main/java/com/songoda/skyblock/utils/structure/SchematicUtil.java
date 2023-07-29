package com.songoda.skyblock.utils.structure;

import com.craftaro.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class SchematicUtil {
    public static Float[] pasteSchematic(File schematicFile, org.bukkit.Location location) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("WorldEdit") && !pluginManager.isPluginEnabled("AsyncWorldEdit") && !pluginManager.isPluginEnabled("FastAsyncWorldEdit")) {
            throw new IllegalStateException("Tried to generate an island using a schematic file without WorldEdit installed!");
        }

        Runnable pasteTask = () -> {
            if (ServerVersion.isServerVersionAbove(ServerVersion.V1_12)) { // WorldEdit 7
                com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat format = com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats.findByFile(schematicFile);
                try (com.sk89q.worldedit.extent.clipboard.io.ClipboardReader reader = format.getReader(Files.newInputStream(schematicFile.toPath()))) {
                    com.sk89q.worldedit.extent.clipboard.Clipboard clipboard = reader.read();
                    try (com.sk89q.worldedit.EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().getEditSessionFactory().getEditSession(new com.sk89q.worldedit.bukkit.BukkitWorld(location.getWorld()), -1)) {
                        com.sk89q.worldedit.function.operation.Operation operation = new com.sk89q.worldedit.session.ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(com.sk89q.worldedit.math.BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                                .ignoreAirBlocks(true)
                                .build();
                        com.sk89q.worldedit.function.operation.Operations.complete(operation);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else { // WorldEdit 6 or earlier
                // I don't want to use modules so reflection it is
                // TODO: Cache this later
                try {
                    Class<?> bukkitWorldClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitWorld");
                    Constructor<?> bukkitWorldConstructor = bukkitWorldClass.getConstructor(World.class);
                    Class<?> editSessionClass = Class.forName("com.sk89q.worldedit.EditSession");
                    Class<?> localWorldClass = Class.forName("com.sk89q.worldedit.LocalWorld");
                    Constructor<?> editSessionConstructor = editSessionClass.getConstructor(localWorldClass, int.class);
                    Class<?> cuboidClipboardClass = Class.forName("com.sk89q.worldedit.CuboidClipboard");
                    Method loadSchematicMethod = cuboidClipboardClass.getMethod("loadSchematic", File.class);
                    Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                    Constructor<?> vectorConstructor = vectorClass.getConstructor(double.class, double.class, double.class);
                    Method pasteMethod = cuboidClipboardClass.getMethod("paste", editSessionClass, vectorClass, boolean.class);

                    Object editSessionObj = editSessionConstructor.newInstance(bukkitWorldConstructor.newInstance(location.getWorld()), 999999999);
                    Object cuboidClipboardObj = loadSchematicMethod.invoke(null, schematicFile);
                    Object vectorObj = vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ());

                    pasteMethod.invoke(cuboidClipboardObj, editSessionObj, vectorObj, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit") || Bukkit.getPluginManager().isPluginEnabled("AsyncWorldEdit")) {
            Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getPlugin(SkyBlock.class), pasteTask);
        } else {
            Bukkit.getScheduler().runTask(SkyBlock.getPlugin(SkyBlock.class), pasteTask);
        }

        return new Float[]{location.getYaw(), location.getPitch()};
    }
}
