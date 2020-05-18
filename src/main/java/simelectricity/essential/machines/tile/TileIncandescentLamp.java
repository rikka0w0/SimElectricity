package simelectricity.essential.machines.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISE2StateTile;
import simelectricity.essential.common.semachine.SESinglePortMachine;

public class TileIncandescentLamp extends SESinglePortMachine<ISEVoltageSource> implements 
		ISEVoltageSource, ISE2StateTile, ISEEnergyNetUpdateHandler {
    public byte lightLevel;

    @Override
    public double getResistance() {
        return 9900; // 5 watt at 220V
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    @Override
    public void onEnergyNetUpdate() {
        double voltage = this.circuit.getVoltage();
        double power = voltage * voltage / this.cachedParam.getResistance();
        byte lightLevelCalc = (byte) (power / 0.3D);
        final byte lightLevel = lightLevelCalc > 15 ? 15 : lightLevelCalc;
        
        Utils.enqueueServerWork(() -> {
            if (this.lightLevel != lightLevel) {
            	this.lightLevel = lightLevel;
            	
            	this.setSecondState(this.lightLevel > 8);
            	
            	markTileEntityForS2CSync();
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == functionalSide ? 0 : -1;
    }

    @Override
    public void prepareS2CPacketData(CompoundNBT nbt) {
        super.prepareS2CPacketData(nbt);
        nbt.putByte("lightLevel", this.lightLevel);
    }

    @Override
    public void onSyncDataFromServerArrived(CompoundNBT nbt) {
        super.onSyncDataFromServerArrived(nbt);
        this.lightLevel = nbt.getByte("lightLevel");
        markForRenderUpdate();
        this.world.getLightManager().checkBlock(this.pos); //checkLightFor
    }
}
