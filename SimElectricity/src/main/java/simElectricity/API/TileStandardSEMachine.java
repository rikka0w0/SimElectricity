package simElectricity.API;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

/**A standard SE machine can inherits this class, make things easier and less confusion */
public abstract class TileStandardSEMachine extends TileEntity implements IEnergyTile,ISidedFacing{
	protected ForgeDirection functionalSide=ForgeDirection.NORTH;
	protected ForgeDirection facing=ForgeDirection.NORTH;
	protected boolean isAddedToEnergyNet = false;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			Util.postTileAttachEvent(this);
			this.isAddedToEnergyNet=true;
			Util.scheduleBlockUpdate(this);
		}
	}
	
	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet)
			Util.postTileDetachEvent(this);
	}
	
	@Override
	public void setFacing(ForgeDirection newFacing){facing=newFacing;}
	
	@Override
	public ForgeDirection getFacing() {return facing;}

	@Override
	public boolean canSetFacing(ForgeDirection newFacing) {return true;}
	

	@Override
	public ForgeDirection getFunctionalSide() {return functionalSide;}
	
	@Override
	public void setFunctionalSide(ForgeDirection newFunctionalSide) {functionalSide=newFunctionalSide;}

	@Override
	public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {return true;}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
    	super.readFromNBT(tagCompound);
    	
    	facing=Util.byte2Direction(tagCompound.getByte("facing"));
    	functionalSide=Util.byte2Direction(tagCompound.getByte("functionalSide"));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
    	super.writeToNBT(tagCompound);
    	
    	tagCompound.setByte("facing", Util.direction2Byte(facing));
    	tagCompound.setByte("functionalSide", Util.direction2Byte(functionalSide));
    }
}
