package se.artheus.minecraft.theallcord.networking;

import appeng.api.networking.IInWorldGridNodeHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import se.artheus.minecraft.theallcord.entities.AbstractNetworkEntity;

public class CableConnections {

    public static boolean isConnectable(Level level, BlockPos neighborPos, Direction dir, BlockEntity neighbor) {
        if (level==null || neighborPos==null || dir==null || neighbor==null) return false;

        return neighbor instanceof AbstractNetworkEntity ||
            neighbor instanceof IInWorldGridNodeHost;
    }
}
