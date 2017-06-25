package simelectricity.essential.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;

public class SETwoPortMachine extends SEEnergyTile implements ISidedFacing, ISETile, ISEComponentParameter {
	public ForgeDirection inputSide = ForgeDirection.SOUTH;
	public ForgeDirection outputSide = ForgeDirection.NORTH;
	protected ForgeDirection facing = ForgeDirection.NORTH;
	protected ISESubComponent input = SEAPI.energyNetAgent.newComponent(this, this);
	
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation((tagCompound.getByte("outputSide")));
        facing = ForgeDirection.getOrientation((tagCompound.getByte("facing")));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
        tagCompound.setByte("facing", (byte) facing.ordinal());
    }

    ///////////////////////////////////
    /// ISidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(ForgeDirection newFacing) {
        facing = newFacing;
        
        this.markTileEntityForS2CSync();
    }

    @Override
    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
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
        inputSide = ForgeDirection.getOrientation(nbt.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation((nbt.getByte("outputSide")));
		facing = ForgeDirection.getOrientation(nbt.getByte("facing"));
		
		// Flag 1 - update Rendering Only!
		markForRenderUpdate();
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////
	@Override
	public ISESubComponent getComponent(ForgeDirection side){
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
	public void setFunctionalSide(ForgeDirection input, ForgeDirection output){
		this.inputSide = input;
		this.outputSide = output;
		
        this.markTileEntityForS2CSync();
        this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());
        
        if (this.isAddedToEnergyNet)
        	SEAPI.energyNetAgent.updateTileConnection(this);
	}
}
