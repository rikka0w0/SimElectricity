package simelectricity.essential.machines.tile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.SETwoPortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileCurrentSensor extends SETwoPortMachine implements ISESwitch, IEnergyNetUpdateHandler, ISESocketProvider{
	public double current;
	public boolean emitRedstoneSignal;
    
    public double resistance = 0.001;
    public double thresholdCurrent = 1;
    public boolean absMode, inverted;
    
 
	
	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getDouble("resistance");
        thresholdCurrent = tagCompound.getDouble("thresholdCurrent");
        absMode = tagCompound.getBoolean("absMode");
        inverted = tagCompound.getBoolean("inverted");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("resistance", resistance);
        tagCompound.setDouble("thresholdCurrent", thresholdCurrent);
        tagCompound.setBoolean("absMode", absMode);
        tagCompound.setBoolean("inverted", inverted);
        
        return super.writeToNBT(tagCompound);
    }
    
	/////////////////////////////////////////////////////////
	///IEnergyNetUpdateHandler
	/////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
    	current = SEAPI.energyNetAgent.getCurrentMagnitude(this.input);
    	
    	checkRedstoneStatus();
    }
    
	/////////////////////////////////////////////////////////
	///ISESwitchData
	/////////////////////////////////////////////////////////
	@Override
	public boolean isOn(){
		return true;
	}

	@Override
	public double getResistance() {
		return resistance;
	}
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(EnumFacing side) {
		if (side == inputSide)
			return 2;
		else if (side == outputSide)
			return 4;
		else 
			return -1;
	}
	
	///////////////////////////////////
	private boolean setRedstone(boolean status){
		if (emitRedstoneSignal != status){
			emitRedstoneSignal = status;
			world.notifyNeighborsOfStateChange(getPos(), this.getBlockType(), false);
			return true;
		}
		return false;
	}
	
	public void checkRedstoneStatus(){
		double current = absMode ? Math.abs(this.current) : this.current;
		setRedstone((current > thresholdCurrent) ^ inverted);
	}
}
