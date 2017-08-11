package simelectricity.essential.common.semachine;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;
import simelectricity.essential.common.SEEnergyTile;

public class SETwoPortMachine extends SEEnergyTile implements ISidedFacing, ISETile, ISEComponentParameter {
	public EnumFacing inputSide = EnumFacing.SOUTH;
	public EnumFacing outputSide = EnumFacing.NORTH;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected ISESubComponent input = SEAPI.energyNetAgent.newComponent(this, this);
	
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        inputSide = EnumFacing.getFront(tagCompound.getByte("inputSide"));
        outputSide = EnumFacing.getFront((tagCompound.getByte("outputSide")));
        facing = EnumFacing.getFront((tagCompound.getByte("facing")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
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
    
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
    @Override
	public void prepareS2CPacketData(NBTTagCompound nbt){
		super.prepareS2CPacketData(nbt);
		
        nbt.setByte("inputSide", (byte) inputSide.ordinal());
        nbt.setByte("outputSide", (byte) outputSide.ordinal());
		nbt.setByte("facing", (byte)facing.ordinal());
	}
	
    @SideOnly(value = Side.CLIENT)
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
        inputSide = EnumFacing.getFront(nbt.getByte("inputSide"));
        outputSide = EnumFacing.getFront((nbt.getByte("outputSide")));
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
		if (side == inputSide)
			return input;
		else if (side == outputSide)
			return input.getComplement();
		else 
			return null;
	}
	
	/////////////////////////////////////////////////////////
	/// Utils
	/////////////////////////////////////////////////////////
	public void setFunctionalSide(EnumFacing input, EnumFacing output){
		this.inputSide = input;
		this.outputSide = output;
		
        this.markTileEntityForS2CSync();
        world.notifyNeighborsOfStateChange(pos, this.getBlockType(), true);
        //this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());
        
        if (this.isAddedToEnergyNet)
        	SEAPI.energyNetAgent.updateTileConnection(this);
	}
}
