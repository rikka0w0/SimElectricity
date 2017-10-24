package simelectricity.essential.grid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.ISEFacing8;

public abstract class TilePoleBranch extends TilePoleAccessory implements ISEFacing8{
	protected BlockPos neighbor;
	private int facing;
	
	@Override
	public void setFacingOnPlacement(int facing) {
		this.facing = facing;
	}

	@Override
	public int getRotation() {
		return facing;
	}
	
    /////////////////////////////////////////////////////////
    /////ISEPowerPole
    /////////////////////////////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public BlockPos[] getNeighborPosArray() {
        return new BlockPos[] {this.neighbor};
    }
	
    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    @Override
    public void onGridNeighborUpdated() {
    	this.host = null;
    	this.neighbor = null;
        
    	for (ISEGridNode node: this.gridNode.getNeighborList()) {
    		BlockPos nodePos = node.getPos();
    		if (nodePos.down().equals(this.pos))
    			this.host = nodePos;
    		else
    			this.neighbor = nodePos;
    	}

        markTileEntityForS2CSync();
    }
    
    @Override
    public boolean canConnect(BlockPos toPos) {
    	if (toPos == null) {
    		return this.host == null || this.neighbor == null;
    	}
    	
        return (this.host == null && toPos.down().equals(this.pos)) || this.neighbor == null;
    }
	
    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
    	super.prepareS2CPacketData(nbt);
    	nbt.setInteger("facing", this.facing);
    	Utils.saveToNbt(nbt, "neighbor", this.neighbor);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
    	this.facing = nbt.getInteger("facing");
    	this.neighbor = Utils.posFromNbt(nbt, "neighbor");
    	super.onSyncDataFromServerArrived(nbt);
    	
        if (this.neighbor != null) {
            TileEntity neighborTile = this.world.getTileEntity(this.neighbor);
            if (neighborTile instanceof ISEPowerPole) 
                PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighborTile);
        }
    }
	
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
		this.facing = nbt.getInteger("facing");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("facing", this.facing);
        return super.writeToNBT(nbt);
    }
    
	public static class Type10kV extends TilePoleBranch {
		@Override
		protected PowerPoleRenderHelper createRenderHelper() {
			PowerPoleRenderHelper helper = new PowerPoleRenderHelper(pos, getRotation(), 1, 3);

			helper.addInsulatorGroup(0, 0.9F, -0.6F,
					helper.createInsulator(0.5F, 1.2F, -0.74F, 0.1F, 0.2F),
					helper.createInsulator(0.5F, 1.2F, 0, 0.1F, 0.2F),
					helper.createInsulator(0.5F, 1.2F, 0.74F, 0.1F, 0.2F)
					);

			return helper;
		}
	}
}
