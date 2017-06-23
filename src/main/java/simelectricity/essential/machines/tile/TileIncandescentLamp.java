package simelectricity.essential.machines.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;
import simelectricity.essential.utils.ITileRenderingInfoSyncHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileIncandescentLamp extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider, ITileRenderingInfoSyncHandler{
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
		
        lightLevel = (byte) (voltage*voltage/getResistance() / 0.3F);
        if (lightLevel > 15)
            lightLevel = 15;
        
        sendRenderingInfoToClient();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side) {
		return side == this.functionalSide ? 0 : -1;
	}
	
	@Override
	public void sendRenderingInfoToClient() {
		this.markTileEntityForS2CSync();
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
		worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);	//checkLightFor
	}
}
