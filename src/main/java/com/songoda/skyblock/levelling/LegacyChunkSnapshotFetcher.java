package com.songoda.skyblock.levelling;

import org.bukkit.ChunkSnapshot;

public class LegacyChunkSnapshotFetcher {

    // Uses a 1.12.2 jar in a separate project to avoid needing to use reflection during level scanning, much faster.
    @SuppressWarnings("deprecation")
	public static LegacyChunkSnapshotData fetch(ChunkSnapshot snapshot, int x, int y, int z) {
        return new LegacyChunkSnapshotData(snapshot.getBlockType(x, y, z), snapshot.getData(x, y, z));
    }

}