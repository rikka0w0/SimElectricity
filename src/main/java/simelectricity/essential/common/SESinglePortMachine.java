package simelectricity.essential.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;

public abstract class SESinglePortMachine<TYPE extends ISEComponentParameter> extends SEEnergyTile implements ISidedFacing, ISEWrenchable, ISETile, ISEComponentParameter {
	protected ForgeDirection functionalSide = ForgeDirection.SOUTH;
	protected ForgeDirection facing = ForgeDirection.NORTH;
	protected ISESubComponent circuit = SEAPI.energyNetAgent.newComponent(this, this);
	
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        functionalSide = ForgeDirection.getOrientation(tagCompound.getByte("functionalSide"));
        facing = ForgeDirection.getOrientation((tagCompound.getByte("facing")));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("functionalSide", (byte) functionalSide.ordinal());
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
    
    
    ///////////////////////////////////
    /// ISEWrenchable
    ///////////////////////////////////
    @Override
    public void setFunctionalSide(ForgeDirection newFunctionalSide) {
        functionalSide = newFunctionalSide;
        
        this.markTileEntityForS2CSync();
        this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());
        SEAPI.energyNetAgent.updateTileConnection(this);
    }
    
    @Override
    public ForgeDirection getFunctionalSide() {
        return functionalSide;
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
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
		functionalSide = ForgeDirection.getOrientation(nbt.getByte("functionalSide"));
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
		return side == functionalSide ? circuit : null;
	}
}
