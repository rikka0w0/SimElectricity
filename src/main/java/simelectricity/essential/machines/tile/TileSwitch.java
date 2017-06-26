package simelectricity.essential.machines.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.SETwoPortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileSwitch extends SETwoPortMachine implements ISESwitch, IEnergyNetUpdateHandler, ISESocketProvider{
	public double current;
    
    public double resistance = 0.001;
    public double maxCurrent = 1;
    public boolean isOn = false;
	
	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getDouble("resistance");
        maxCurrent = tagCompound.getDouble("maxCurrent");
        isOn = tagCompound.getBoolean("isOn");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("resistance", resistance);
        tagCompound.setDouble("maxCurrent", maxCurrent);
        tagCompound.setBoolean("isOn", isOn);
    }
    
	/////////////////////////////////////////////////////////
	///IEnergyNetUpdateHandler
	/////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
    	if (isOn){
        	current = SEAPI.energyNetAgent.getCurrentMagnitude(this.input);
    	}else{
    		current = 0;
    	}

        if (current > maxCurrent)
        	setSwitchStatus(false);
    }
    
	/////////////////////////////////////////////////////////
	///ISESwitchData
	/////////////////////////////////////////////////////////
	@Override
	public boolean isOn(){
		return isOn;
	}

	@Override
	public double getResistance() {
		return resistance;
	}
	
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		nbt.setBoolean("isOn", isOn);
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){	
		isOn = nbt.getBoolean("isOn");

		super.onSyncDataFromServerArrived(nbt);
	}
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side) {
		if (side == inputSide)
			return 2;
		else if (side == outputSide)
			return 4;
		else 
			return -1;
	}
	
    ///////////////////////////////////
    /// Utils
    ///////////////////////////////////
	public void setSwitchStatus(boolean isOn){
        this.isOn = isOn;
        SEAPI.energyNetAgent.updateTileParameter(this);
        
        this.markTileEntityForS2CSync();
	}
}
