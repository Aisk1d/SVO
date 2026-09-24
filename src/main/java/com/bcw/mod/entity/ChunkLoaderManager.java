package com.bcw.mod.entity;

import com.bcw.mod.BallisticWarfareMod;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps a 3x3 chunk window loaded around an in-flight missile so its motion keeps
 * ticking even far outside normal player render distance. Every missile owns exactly
 * one instance and must call {@link #release(ServerLevel)} when it detonates or is
 * removed, or the forced chunks will leak.
 */
public final class ChunkLoaderManager {

    private final UUID ownerId;
    private final Set<ChunkPos> currentlyForced = new HashSet<>();
    private boolean released = false;

    public ChunkLoaderManager(UUID ownerId) {
        this.ownerId = ownerId;
    }

    /** Call once per tick with the missile's current chunk position. */
    public void updateAround(ServerLevel level, ChunkPos center) {
        if (released) return;

        Set<ChunkPos> desired = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                desired.add(new ChunkPos(center.x + dx, center.z + dz));
            }
        }

        for (ChunkPos pos : desired) {
            if (currentlyForced.add(pos)) {
                forceChunk(level, pos, true);
            }
        }

        currentlyForced.removeIf(pos -> {
            if (!desired.contains(pos)) {
                forceChunk(level, pos, false);
                return true;
            }
            return false;
        });
    }

    private void forceChunk(ServerLevel level, ChunkPos pos, boolean add) {
        boolean ok = ForgeChunkManager.forceChunk(
                level,
                BallisticWarfareMod.MOD_ID,
                ownerId,
                pos.x,
                pos.z,
                add,
                true
        );
        if (!ok) {
            BallisticWarfareMod.LOGGER.warn(
                    "Failed to {} chunk {} for missile {}",
                    add ? "force" : "release", pos, ownerId
            );
        }
    }

    /** Releases every chunk this missile is currently holding. Must be called on detonation/removal. */
    public void release(ServerLevel level) {
        if (released) return;
        for (ChunkPos pos : currentlyForced) {
            forceChunk(level, pos, false);
        }
        currentlyForced.clear();
        released = true;
    }

    public boolean isReleased() {
        return released;
    }

    public static ChunkPos chunkOf(double x, double z) {
        return new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
    }
}
