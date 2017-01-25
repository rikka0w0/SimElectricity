package simElectricity.Common.Blocks.TileEntity;

import simElectricity.API.SEAPI;

import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISETile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDiode extends TileEntitySE implements ISETile, ISEDiodeData{
	public ISESubComponent input = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);
	public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH;
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
    }
	
	@Override
	public int getNumberOfComponents() {
		return 2;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{inputSide, outputSide};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == inputSide)
			return input;
		else if (side == outputSide)
			return input.getComplement();
		return null;
	}

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}

	//Diode data
	@Override
	public double getForwardResistance() {
		return 0.1;
	}
}
