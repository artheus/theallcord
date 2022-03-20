package se.artheus.minecraft.theallcord.networking;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.impl.lookup.block.BlockApiCacheImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AbstractOfferedType<A, C> {
    private final ServerLevel level;
    private final BlockPos blockPos;
    private final BlockApiLookup<A, C> lookup;
    private final C context;
    private BlockApiCache<A, C> cache;

    public AbstractOfferedType(ServerLevel level, BlockPos blockPos, C context, BlockApiLookup<A, C> lookup) {
        this.level = level;
        this.blockPos = blockPos;
        this.context = context;
        this.lookup = lookup;
    }

    @Nullable
    public A find() {
        if (Objects.isNull(cache)) {
            cache = BlockApiCache.create(lookup, level, blockPos);
        }

        var found = cache.find(context);

        if (Objects.nonNull(found) && Objects.isNull(cache.getBlockEntity()))
            ((BlockApiCacheImpl<A, C>) cache).invalidate();

        return found;
    }

    public C context() {
        return context;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public ServerLevel level() {
        return level;
    }
}
