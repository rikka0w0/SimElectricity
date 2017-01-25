package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.DataProvider.ISERegulatorData;
import simElectricity.API.SEAPI;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISETile;
import simElectricity.API.INetworkEventHandler;

import java.util.List;

public class TileSolarInverter extends TileEntitySE implements ISETile, ISERegulatorData, INetworkEventHandler{
	public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH;
    public ISESubComponent input = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);

    public float Vreg = 24;
    public float Ro = 0.001F;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        Vreg = tagCompound.getFloat("Vreg");
        Ro = tagCompound.getFloat("Ro");
        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("Vreg", Vreg);
        tagCompound.setFloat("Ro", Ro);
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
    }

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}


	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		//Handling on server side
		if (!worldObj.isRemote){
			for (String s:fields){
		        if (s.contains("inputSide") || s.contains("outputSide")) {
		        	SEAPI.energyNetAgent.reattachTile(this);
		            worldObj.notifyBlockChange(xCoord, yCoord, zCoord,
		            		worldObj.getBlock(xCoord, yCoord, zCoord));
		        } else if (s.contains("Ro") || s.contains("Vreg")) {
		        	SEAPI.energyNetAgent.markTileForUpdate(this);
		        }
			}

		}
	}

	@Override
	public void addNetworkFields(List fields) {
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
		if (side == outputSide)
			return input.getComplement();
		return null;
	}
	
	public ForgeDirection getPrimarySide(){
		return inputSide;
	}
	
	public ForgeDirection getSecondarySide(){
		return outputSide;
	}
	
	//Regulator
	@Override
	public double getRegulatedVoltage() {return Vreg;}

	@Override
	public double getOutputResistance() {return Ro;}
	
	@Override
	public double getDMax() {return 1;}

	@Override
	public double getRc() {return 1;}

	@Override
	public double getGain() {return 1e5;}

	@Override
	public double getRs() {return 1e6;}
}
