package me.goodandevil.skyblock.utils.structure;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SchematicUtil {

    public static Float[] pasteSchematic(File schematicFile, org.bukkit.Location location) {
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit"))
            throw new IllegalStateException("Tried to generate an island using a schematic file without WorldEdit installed!");

        Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
            if (NMSUtil.getVersionNumber() > 12) { // WorldEdit 7
                com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat format = com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats.findByFile(schematicFile);
                try (com.sk89q.worldedit.extent.clipboard.io.ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                    com.sk89q.worldedit.extent.clipboard.Clipboard clipboard = reader.read();
                    try (com.sk89q.worldedit.EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().getEditSessionFactory().getEditSession(new com.sk89q.worldedit.bukkit.BukkitWorld(location.getWorld()), -1)) {
                        com.sk89q.worldedit.function.operation.Operation operation = new com.sk89q.worldedit.session.ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(com.sk89q.worldedit.math.BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                                .ignoreAirBlocks(true)
                                .build();
                        com.sk89q.worldedit.function.operation.Operations.complete(operation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // WorldEdit 6 or earlier
                // I don't want to use modules so reflection it is
                // TODO: Cache this later
                try {
                    Class<?> bukkitWorldClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitWorld");
                    Constructor bukkitWorldConstructor = bukkitWorldClass.getConstructor(World.class);
                    Class<?> editSessionClass = Class.forName("com.sk89q.worldedit.EditSession");
                    Class<?> localWorldClass = Class.forName("com.sk89q.worldedit.LocalWorld");
                    Constructor editSessionConstructor = editSessionClass.getConstructor(localWorldClass, int.class);
                    Class<?> cuboidClipboardClass = Class.forName("com.sk89q.worldedit.CuboidClipboard");
                    Method loadSchematicMethod = cuboidClipboardClass.getMethod("loadSchematic", File.class);
                    Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                    Constructor vectorConstructor = vectorClass.getConstructor(double.class, double.class, double.class);
                    Method pasteMethod = cuboidClipboardClass.getMethod("paste", editSessionClass, vectorClass, boolean.class);

                    Object editSessionObj = editSessionConstructor.newInstance(bukkitWorldConstructor.newInstance(location.getWorld()), 999999999);
                    Object cuboidClipboardObj = loadSchematicMethod.invoke(null, schematicFile);
                    Object vectorObj = vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ());

                    pasteMethod.invoke(cuboidClipboardObj, editSessionObj, vectorObj, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return new Float[] { location.getYaw(), location.getPitch() };
    }

}
