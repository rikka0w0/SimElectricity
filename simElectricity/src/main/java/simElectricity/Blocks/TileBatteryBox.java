package simElectricity.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.Common.TileStandardGenerator;
import simElectricity.API.Energy;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileBatteryBox extends TileStandardGenerator {
	
	private float chargingVoltage = 115;
	private float dischargeVoltage = 110;
	
	private float wattTickStore = 0;
	private float wattTickStoreMax = 1000000;
	
    @Override
    public void updateEntity() {
        super.updateEntity();

        //Server only
        if (worldObj.isRemote)
            return;

        if ((Energy.getVoltage(this) >= chargingVoltage) && (wattTickStore < wattTickStoreMax)) {
        	checkAndSendChange(0, 0.8F);
        	wattTickStore += (Energy.getPower(this) * 0.05);
        	
            System.out.printf("charging, wattTickStore: %f\n", wattTickStore);
        }else if ((Energy.getVoltage(this) <= dischargeVoltage) && (wattTickStore > 0)) {
        	checkAndSendChange(dischargeVoltage - 0.1F, 0.8F);
        	wattTickStore -= Energy.getWorkDonePerTick(this);
        	
            System.out.printf("discharge, wattTickStore: %f\n", wattTickStore);
		}else {
        	checkAndSendChange(0, Float.MAX_VALUE);
		}
        
    }

    void checkAndSendChange(float voltage, float resistance) {
        if (voltage != outputVoltage | resistance != outputResistance) {
            outputVoltage = voltage;
            outputResistance = resistance;
            Energy.postTileChangeEvent(this);
        }
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }
}
