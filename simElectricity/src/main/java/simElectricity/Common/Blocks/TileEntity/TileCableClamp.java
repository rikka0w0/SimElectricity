package simElectricity.Common.Blocks.TileEntity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.IHVTower;
import simElectricity.API.Util;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.ISEConnectable;

public class TileCableClamp extends TileEntitySE implements ISEConnectable, IHVTower{
	public int facing = 0;
	
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
	
	@Override
	public boolean attachToEnergyNet() {
		return true;
	}

	@Override
	public boolean canConnectOnSide(ForgeDirection side) {
		return side.ordinal() == facing;
	}

	@Override
	public float[] offsetArray() {
		return new float[]{
				-0.8F, 1.2F, 0.4F,
				0, 1.5F, 0.6F,
				0.8F, 1.2F, 0.4F};
	}

	@Override
	public int[] getNeighborInfo() {
		return new int[]{};
	}

	@Override
	public int getFacing() {
		return facing;
	}

	@Override
	public float getWireTension() {
		return 0.06F;
	}
}
