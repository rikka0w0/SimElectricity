package simElectricity.Templates.TileEntity;

import java.util.List;

import simElectricity.API.INetworkEventHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileTransmissionTower extends TileEntity implements INetworkEventHandler{
	public int facing;
	
    //INetworkEventHandler --------------------------------------------------------------------------------
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {

	}

	@Override
	public void addNetworkFields(List fields) {
		fields.add("facing");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	
	//TileEntity
	@Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = tagCompound.getInteger("facing");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("facing", facing);
    }
    
	@SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 100000;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public AxisAlignedBB getRenderBoundingBox(){
    	return INFINITE_EXTENT_AABB;
    }
}
