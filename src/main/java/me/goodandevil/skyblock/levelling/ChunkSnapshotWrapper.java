package me.goodandevil.skyblock.levelling;

import org.bukkit.ChunkSnapshot;

public class ChunkSnapshotWrapper extends LevelChunkSnapshotWrapper {

    public ChunkSnapshotWrapper(ChunkSnapshot chunkSnapshot) {
        super(chunkSnapshot);
    }

    @Override
    public boolean hasWildStackerData() {
        return false;
    }

}
