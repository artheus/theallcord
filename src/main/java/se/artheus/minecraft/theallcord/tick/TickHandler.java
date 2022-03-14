package se.artheus.minecraft.theallcord.tick;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import org.apache.commons.collections4.list.SetUniqueList;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.block.entity.AbstractEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TickHandler {

    private static final TickHandler INSTANCE = new TickHandler();

    private final EntityRepo blockEntities = new EntityRepo();

    private final Collection<ITickingBlockEntity> tickingBlockEntities = SetUniqueList.setUniqueList(new ArrayList<>());

    private TickHandler() {
    }

    public static TickHandler instance() {
        return INSTANCE;
    }

    public void init() {
        ClientTickEvents.START_WORLD_TICK.register(this::clientTickBlockEntities);
        ServerTickEvents.START_WORLD_TICK.register(this::serverTickBlockEntities);
        ServerTickEvents.END_WORLD_TICK.register(this::onServerLevelTickEnd);
        ServerWorldEvents.LOAD.register((server, level) -> onLoadLevel(level));
    }

    private void onServerLevelTickEnd(ServerLevel level) {
        this.readyBlockEntities(level);
    }

    private void readyBlockEntities(ServerLevel level) {
        var levelQueue = blockEntities.getBlockEntities(level);

        long[] workSet = levelQueue.keySet().toLongArray();

        for (long packedChunkPos : workSet) {
            if (level.shouldTickBlocksAt(packedChunkPos)) {
                var chunkQueue = levelQueue.remove(packedChunkPos);
                if (chunkQueue == null) {
                    Mod.LOGGER.warn("Chunk {} was unloaded while we were readying block entities",
                            new ChunkPos(packedChunkPos));
                    continue;
                }

                for (var bt : chunkQueue) {
                    if (!bt.isRemoved()) {
                        try {
                            bt.initialize();
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
        for (var entity :
                this.tickingBlockEntities) {
            entity.serverTick(level);
        }
    }

    private void clientTickBlockEntities(ClientLevel level) {
        for (var entity :
                this.tickingBlockEntities) {
            entity.clientTick(level);
        }
    }

    public void addInit(@NotNull AbstractEntity blockEntity) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) return;

        this.blockEntities.addBlockEntity(blockEntity);
    }

    public void addTickingEntity(@NotNull ITickingBlockEntity entity) {
        this.tickingBlockEntities.add(entity);
    }

    public void onLoadLevel(ServerLevel level) {
        this.blockEntities.addLevel(level);
    }

    private static final class EntityRepo {
        private final Map<LevelAccessor, Long2ObjectMap<List<AbstractEntity>>> blockEntities = new Object2ObjectOpenHashMap<>();

        synchronized void addBlockEntity(AbstractEntity blockEntity) {
            final LevelAccessor level = blockEntity.getLevel();
            final int x = blockEntity.getBlockPos().getX() >> 4;
            final int z = blockEntity.getBlockPos().getZ() >> 4;
            final long chunkPos = ChunkPos.asLong(x, z);

            Long2ObjectMap<List<AbstractEntity>> worldQueue = this.blockEntities.get(level);

            worldQueue.computeIfAbsent(chunkPos, key -> new ArrayList<>()).add(blockEntity);
        }

        synchronized void addLevel(LevelAccessor level) {
            this.blockEntities.computeIfAbsent(level, key -> new Long2ObjectOpenHashMap<>());
        }

        public Long2ObjectMap<List<AbstractEntity>> getBlockEntities(LevelAccessor level) {
            return blockEntities.get(level);
        }
    }
}
