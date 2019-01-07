package simelectricity.essential.machines.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;

public class TileIncandescentLamp extends SESinglePortMachine implements ISEVoltageSource, ISEEnergyNetUpdateHandler, ISESocketProvider {
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
        double voltage = SEAPI.energyNetAgent.getVoltage(circuit);
        double lightLevel = voltage * voltage / this.getResistance() / 0.3D;

        if (lightLevel > 15)
            lightLevel = 15;

        this.lightLevel = (byte) lightLevel;
        markTileEntityForS2CSync();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
        return side == functionalSide ? 0 : -1;
    }

    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);
        nbt.setByte("lightLevel", this.lightLevel);
    }

    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        super.onSyncDataFromServerArrived(nbt);
        this.lightLevel = nbt.getByte("lightLevel");
        markForRenderUpdate();
        this.world.checkLight(this.pos);    //checkLightFor
    }
}
