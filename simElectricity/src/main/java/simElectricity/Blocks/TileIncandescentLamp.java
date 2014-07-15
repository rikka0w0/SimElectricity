package simElectricity.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.Common.TileStandardGenerator;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileIncandescentLamp extends TileStandardSEMachine implements IEnergyNetUpdateHandler{
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
        if (lightLevel>15)
        	lightLevel=15;
        
        //Util.updateTileEntityField(this, "lightLevel");
        Util.scheduleBlockUpdate(this,4);
	}
}
