package se.artheus.minecraft.theallcord.tick;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TickHandler {

    private static final TickHandler INSTANCE = new TickHandler();
    private final EntityRepo blockEntities = new EntityRepo();
    private final EntityRepo tickingEntities = new EntityRepo();

    private TickHandler() {
    }

    public static TickHandler instance() {
        return INSTANCE;
    }

    public void init() {
        ServerTickEvents.END_WORLD_TICK.register(this::serverTickBlockEntities);
        ServerTickEvents.END_WORLD_TICK.register(this::readyBlockEntities);
        ServerWorldEvents.LOAD.register((server, level) -> onLoadLevel(level));
    }

    public void destroy() {

    }

    private void readyBlockEntities(ServerLevel level) {
        var levelQueue = blockEntities.getEntities(level);

        long[] workSet = levelQueue.keySet().toLongArray();

        for (long packedChunkPos : workSet) {
            if (level.shouldTickBlocksAt(packedChunkPos)) {
                var chunkQueue = levelQueue.remove(packedChunkPos);
                if (chunkQueue==null) {
                    Mod.LOGGER.warn("Chunk {} was unloaded while we were readying block entities",
                        new ChunkPos(packedChunkPos));
                    continue;
                }

                for (var bt : chunkQueue) {
                    if (!bt.isRemoved()) {
                        try {
                            bt.initialize(level);
                        } catch (Throwable t) {
                            CrashReport crashReport = CrashReport.forThrowable(t, "Initializing cable block entity");
                            bt.fillCrashReportCategory(crashReport.addCategory("Block entity being initialized"));
                            throw new ReportedException(crashReport);
                        }
                    }
                }
            }
        }
    }

    private void serverTickBlockEntities(ServerLevel level) {
        var levelQueue = tickingEntities.getEntities(level);

        long[] workSet = levelQueue.keySet().toLongArray();
        for (long packedChunkPos : workSet) {
            if (level.shouldTickBlocksAt(packedChunkPos)) {
                var chunkQueue = levelQueue.get(packedChunkPos);

                for (var e : chunkQueue.stream().filter(ITickingEntity::isRemoved).toList()) {
                    removeTickingEntity(e);
                }

                chunkQueue.forEach(ticking -> {
                    if (ticking.shouldTick()) ticking.tick(level);
                });
            }
        }
    }

    public void addInit(@NotNull ITickingEntity entity) {
        if (entity.getLevel()==null || entity.getLevel().isClientSide()) return;

        this.blockEntities.addEntity(entity);
    }

    public void addTickingEntity(@NotNull ITickingEntity entity) {
        if (entity.getLevel()==null || entity.getLevel().isClientSide()) return;

        this.tickingEntities.addEntity(entity);
    }

    public void removeTickingEntity(@NotNull ITickingEntity entity) {
        if (entity.getLevel()==null || entity.getLevel().isClientSide()) return;

        tickingEntities.removeEntity(entity);
    }

    public void onLoadLevel(ServerLevel level) {
        this.blockEntities.addLevel(level);
        this.tickingEntities.addLevel(level);
    }

    private record EntityRepoEntry(long chunkPos, @Nullable Long2ObjectMap<List<ITickingEntity>> worldQueue) {
    }

    private static final class EntityRepo {
        private final Map<LevelAccessor, Long2ObjectMap<List<ITickingEntity>>> tickingEntities = new Object2ObjectOpenHashMap<>();

        private EntityRepoEntry toEntry(@NotNull ITickingEntity entity) {
            final LevelAccessor level = entity.getLevel();
            final int x = entity.getBlockPos().getX() >> 4;
            final int z = entity.getBlockPos().getZ() >> 4;

            return new EntityRepoEntry(
                ChunkPos.asLong(x, z),
                this.tickingEntities.get(level)
            );
        }

        synchronized void addEntity(@NotNull ITickingEntity entity) {
            var entry = toEntry(entity);

            if (entry.worldQueue==null) return;

            entry.worldQueue.computeIfAbsent(entry.chunkPos, key -> Collections.synchronizedList(new ArrayList<>())).add(entity);
        }

        synchronized void removeEntity(@NotNull ITickingEntity entity) {
            var entry = toEntry(entity);

            if (entry.worldQueue==null) return;

            var chunkList = entry.worldQueue.get(entry.chunkPos);
            if (chunkList!=null) chunkList.remove(entity);
        }

        synchronized void addLevel(LevelAccessor level) {
            this.tickingEntities.computeIfAbsent(level, key -> new Long2ObjectOpenHashMap<>());
        }

        public Long2ObjectMap<List<ITickingEntity>> getEntities(LevelAccessor level) {
            return tickingEntities.get(level);
        }
    }
}
