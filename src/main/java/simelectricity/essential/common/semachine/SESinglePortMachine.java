package simelectricity.essential.common.semachine;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;
import simelectricity.essential.common.SEEnergyTile;

public abstract class SESinglePortMachine extends SEEnergyTile implements ISidedFacing, ISEWrenchable, ISETile, ISEComponentParameter {
	protected EnumFacing functionalSide = EnumFacing.SOUTH;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected ISESubComponent circuit = SEAPI.energyNetAgent.newComponent(this, this);
	
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        functionalSide = EnumFacing.getFront(tagCompound.getByte("functionalSide"));
        facing = EnumFacing.getFront((tagCompound.getByte("facing")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("functionalSide", (byte) functionalSide.ordinal());
        tagCompound.setByte("facing", (byte) facing.ordinal());
        return super.writeToNBT(tagCompound);
    }

    ///////////////////////////////////
    /// ISidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(EnumFacing newFacing) {
        facing = newFacing;
        
        this.markTileEntityForS2CSync();
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public boolean canSetFacing(EnumFacing newFacing) {
        return true;
    }
    
    
    ///////////////////////////////////
    /// ISEWrenchable
    ///////////////////////////////////
    @Override
    public void onWrenchAction(EnumFacing side, boolean isCreativePlayer) {
    	SetFunctionalSide(side);
    }

    @Override
    public boolean canWrenchBeUsed(EnumFacing side) {
        return true;
    }
    
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
    @Override
	public void prepareS2CPacketData(NBTTagCompound nbt){
		super.prepareS2CPacketData(nbt);
		
		nbt.setByte("functionalSide", (byte)functionalSide.ordinal());
		nbt.setByte("facing", (byte)facing.ordinal());
	}
	
    @SideOnly(value = Side.CLIENT)
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		functionalSide = EnumFacing.getFront(nbt.getByte("functionalSide"));
		facing = EnumFacing.getFront(nbt.getByte("facing"));
		
		// Flag 1 - update Rendering Only!
		markForRenderUpdate();
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////
	@Override
	public ISESubComponent getComponent(EnumFacing side){
		return side == functionalSide ? circuit : null;
	}
	
	/////////////////////////////////////////////////////////
	///Utils
	/////////////////////////////////////////////////////////
	public void SetFunctionalSide(EnumFacing side) {
        functionalSide = side;
        
        this.markTileEntityForS2CSync();
        world.notifyNeighborsOfStateChange(pos, this.getBlockType(), true);
        //this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());
        //TODO: QAQ!
        
        if (this.isAddedToEnergyNet)
        	SEAPI.energyNetAgent.updateTileConnection(this);
	}
}
