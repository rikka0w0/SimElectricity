package simelectricity.essential.api;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.blockentity.ISEGridBlockEntity;

public interface ISEHVCableConnector extends ISENodeDelegateBlock<ISEGridNode> {
    default ISEGridBlockEntity getGridTile(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        return te instanceof ISEGridBlockEntity ? (ISEGridBlockEntity) te : null;
    }
    
    @Override
    default ISEGridNode getNode(Level world, BlockPos pos) {
    	ISEGridBlockEntity gridTile = getGridTile(world, pos);
		return gridTile == null ? null : gridTile.getGridNode();
    }
}