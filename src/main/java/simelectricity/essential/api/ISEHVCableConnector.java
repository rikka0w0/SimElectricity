package simelectricity.essential.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;

public interface ISEHVCableConnector extends ISENodeDelegateBlock<ISEGridNode> {
    default ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof ISEGridTile ? (ISEGridTile) te : null;
    }
    
    @Override
    default ISEGridNode getNode(World world, BlockPos pos) {
    	ISEGridTile gridTile = getGridTile(world, pos);
		return gridTile == null ? null : gridTile.getGridNode();
    }
}