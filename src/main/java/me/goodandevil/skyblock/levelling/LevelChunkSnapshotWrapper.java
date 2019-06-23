package me.goodandevil.skyblock.levelling;

import org.bukkit.ChunkSnapshot;

public abstract class LevelChunkSnapshotWrapper {

    private ChunkSnapshot chunkSnapshot;

    public LevelChunkSnapshotWrapper(ChunkSnapshot chunkSnapshot) {
        this.chunkSnapshot = chunkSnapshot;
    }

    public ChunkSnapshot getChunkSnapshot() {
        return this.chunkSnapshot;
    }

    abstract boolean hasWildStackerData();

}
