package com.songoda.skyblock.levelling;

import com.bgsoftware.wildstacker.api.objects.StackedSnapshot;
import org.bukkit.ChunkSnapshot;

public class WildStackerChunkSnapshotWrapper extends LevelChunkSnapshotWrapper {

    private StackedSnapshot stackedSnapshot;

    public WildStackerChunkSnapshotWrapper(ChunkSnapshot chunkSnapshot, StackedSnapshot stackedSnapshot) {
        super(chunkSnapshot);
        this.stackedSnapshot = stackedSnapshot;
    }

    @Override
    public boolean hasWildStackerData() {
        return true;
    }

    public StackedSnapshot getStackedSnapshot() {
        return this.stackedSnapshot;
    }

}
