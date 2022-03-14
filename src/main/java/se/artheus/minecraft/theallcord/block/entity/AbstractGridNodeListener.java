package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractGridNodeListener<E extends BlockEntity> implements IGridNodeListener<E> {
    @Override
    public void onSecurityBreak(E e, IGridNode iGridNode) {

    }

    @Override
    public void onSaveChanges(E e, IGridNode iGridNode) {

    }
}
