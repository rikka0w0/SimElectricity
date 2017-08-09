package simelectricity.essential.machines.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.util.EnumFacing;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileIncandescentLamp extends SESinglePortMachine implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider{
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
	public void onEnergyNetUpdate() {
		double voltage = SEAPI.energyNetAgent.getVoltage(this.circuit);
		double lightLevel = (voltage*voltage/getResistance() / 0.3D);
		
        if (lightLevel > 15)
            lightLevel = 15;
        
        this.lightLevel = (byte) lightLevel;
        
        this.markTileEntityForS2CSync();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(EnumFacing side) {
		return side == this.functionalSide ? 0 : -1;
	}
	
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		nbt.setByte("lightLevel", lightLevel);
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		super.onSyncDataFromServerArrived(nbt);
		lightLevel = nbt.getByte("lightLevel");
		this.markForRenderUpdate();
		world.checkLight(pos);	//checkLightFor
	}
}
