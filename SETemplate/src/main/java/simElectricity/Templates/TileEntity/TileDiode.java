package simElectricity.Templates.TileEntity;

import simElectricity.API.SEAPI;

import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISETile;
import simElectricity.Templates.Common.TileEntitySE;
import simElectricity.Templates.Common.TileEntityTwoPort;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDiode extends TileEntityTwoPort implements ISEDiodeData{
	public ISESubComponent input = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);
	
	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////
	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == inputSide)
			return input;
		else if (side == outputSide)
			return input.getComplement();
		return null;
	}

	/////////////////////////////////////////////////////////
	///ISEDiodeData
	/////////////////////////////////////////////////////////
	@Override
	public double getForwardResistance() {return 0.1;}

	@Override
	public double getSaturationCurrent() {return 1e-6;}

	@Override
	public double getThermalVoltage() {return 26e-6;}
}
