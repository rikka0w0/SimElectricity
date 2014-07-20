package simElectricity.Blocks;

import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.IUpdateOnWatch;
import simElectricity.API.Util;

public class TileIncandescentLamp extends TileStandardSEMachine implements IEnergyNetUpdateHandler,IUpdateOnWatch {
    public int lightLevel = 0;

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }

    @Override
    public float getOutputVoltage() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 9900; // 5 watt at 220V
    }

    @Override
    public int getInventorySize() {
        return 0;
    }

    @Override
    public void onEnergyNetUpdate() {
        lightLevel = (int) (Energy.getPower(this) / 0.3F);
        if (lightLevel > 15)
            lightLevel = 15;
        if (Energy.getVoltage(this) > 265)
            worldObj.createExplosion(null, xCoord, yCoord, zCoord, 4F + Energy.getVoltage(this) / 265, true);
        //Util.updateTileEntityField(this, "lightLevel");
        Util.scheduleBlockUpdate(this, 4);
    }

	@Override
	public void onWatch() {
		Util.scheduleBlockUpdate(this, 4);
	}
}
