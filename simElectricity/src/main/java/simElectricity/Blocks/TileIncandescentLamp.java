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
	public float lightLevel = 0.0F;
	
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
		return 529; // 100 watt at 230V
	}

	@Override
	public int getInventorySize() {
		return 0;
	}

	@Override
	public void onEnergyNetUpdate() {
        lightLevel = Energy.getPower(this) / 100.0F;
        if (lightLevel>1)
        	lightLevel=1;
        
        //Util.updateTileEntityField(this, "lightLevel");
        Util.scheduleBlockUpdate(this,4);
	}
}
