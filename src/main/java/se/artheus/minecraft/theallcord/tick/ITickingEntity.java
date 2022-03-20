package se.artheus.minecraft.theallcord.tick;

import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public interface ITickingEntity extends InitializableEntity {
    default boolean shouldTick() {
        return !this.isRemoved();
    }

    default void tick(ServerLevel level) {

    }

    @Override
    @OverridingMethodsMustInvokeSuper
    default void initialize(ServerLevel level) {
        TickHandler.instance().addTickingEntity(this);
    }

    BlockPos getBlockPos();

    Level getLevel();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isRemoved();

    default void fillCrashReportCategory(@NotNull CrashReportCategory reportCategory) {
        reportCategory.setDetail("Name", () -> this.getClass().getCanonicalName());
        if (this.getLevel()==null) {
            return;
        }

        CrashReportCategory.populateBlockDetails(reportCategory, this.getLevel(), this.getBlockPos(), this.getLevel().getBlockState(getBlockPos()));
    }
}
