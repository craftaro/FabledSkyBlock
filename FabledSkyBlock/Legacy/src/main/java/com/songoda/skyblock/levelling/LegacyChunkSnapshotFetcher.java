package com.songoda.skyblock.levelling;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

public class LegacyChunkSnapshotFetcher {

    // Uses a 1.12.2 jar in a separate project to avoid needing to use reflection during level scanning, much faster.
    public static LegacyChunkSnapshotData fetch(ChunkSnapshot snapshot, int x, int y, int z) {
        return new LegacyChunkSnapshotData(Material.getMaterial(snapshot.getBlockTypeId(x, y, z)), snapshot.getBlockData(x, y, z));
    }

}