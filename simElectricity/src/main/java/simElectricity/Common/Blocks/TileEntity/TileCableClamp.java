package simElectricity.Common.Blocks.TileEntity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.IHVTower;
import simElectricity.API.Util;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IManualJunction;

public class TileCableClamp extends TileEntitySE implements IManualJunction, IConnectable, IHVTower{
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
	public double getResistance() {
		return 0.1;
	}

	@Override
	public void addNeighbors(List<IBaseComponent> list) {
		TileEntity te = worldObj.getTileEntity(xCoord,yCoord + 2,zCoord);
		if (te instanceof TileTower && te.getBlockMetadata() == 1)
    		list.add((IManualJunction) te);
		
		te = Util.getTileEntityonDirection(this, ForgeDirection.getOrientation(facing));
		if (te instanceof IConductor)
			list.add((IConductor) te);
	}

	@Override
	public double getResistance(IBaseComponent neighbor) {
		return 0;
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
	public void addNeighbor(TileEntity te) {
	}

	@Override
	public boolean hasVacant() {
		return false;
	}

	@Override
	public float getWireTension() {
		return 0.06F;
	}
}
