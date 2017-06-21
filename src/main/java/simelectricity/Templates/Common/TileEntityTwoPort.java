package simelectricity.Templates.Common;

import simelectricity.api.ISidedFacing;
import simelectricity.api.tile.ISETile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTwoPort extends TileEntitySE implements ISidedFacing, ISETile{
	public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH, facing = ForgeDirection.WEST;
	
	/////////////////////////////////////////////////////////
	///TileEntitySE
	/////////////////////////////////////////////////////////
	@Override
	public boolean attachToEnergyNet() {return true;}

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
        facing = ForgeDirection.getOrientation(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
        tagCompound.setByte("facing", (byte) facing.ordinal());
    }

	/////////////////////////////////////////////////////////
	///ISidedFacing
	/////////////////////////////////////////////////////////
	@Override
    public ForgeDirection getFacing() {return facing;}

	@Override
	public void setFacing(ForgeDirection newFacing) {
        facing = newFacing;
        
        this.markTileEntityForS2CSync();
	}

	@Override
	public boolean canSetFacing(ForgeDirection newFacing) {
		return newFacing != inputSide && newFacing != outputSide;
	}

	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);

		nbt.setByte("inputSide", (byte) inputSide.ordinal());
		nbt.setByte("outputSide", (byte) outputSide.ordinal());
		nbt.setByte("facing", (byte) facing.ordinal());
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){	
        inputSide = ForgeDirection.getOrientation(nbt.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(nbt.getByte("outputSide"));
        facing = ForgeDirection.getOrientation(nbt.getByte("facing"));

		super.onSyncDataFromServerArrived(nbt);
	}
}
