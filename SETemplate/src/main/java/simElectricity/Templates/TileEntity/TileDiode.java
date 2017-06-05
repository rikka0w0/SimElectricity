package simelectricity.Templates.TileEntity;

import simelectricity.api.SEAPI;

import simelectricity.api.components.ISEDiodeData;
import simelectricity.api.node.ISESubComponent;
import simelectricity.Templates.Common.TileEntityTwoPort;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDiode extends TileEntityTwoPort implements ISEDiodeData{
	public ISESubComponent input = SEAPI.energyNetAgent.newComponent(this, this);
	
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
