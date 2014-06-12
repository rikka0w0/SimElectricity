package simElectricity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import simElectricity.API.Energy;
import simElectricity.API.Common.TileStandardSEMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;


public class TileIC2Emitter extends TileStandardSEMachine implements IEnergySource{
    public float resistance=32;
    public float IC2Voltage=32;
    public float buffer=0;
    
    
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (worldObj.isRemote)
			return;
		
		buffer+=Energy.getPower(this);
	}
	
	
    
	@Override 
	public void onLoad(){MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));}
	
	@Override 
	public void onUnload(){MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));}	
	
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction){return true;}

	@Override
	public void drawEnergy(double arg0) {
		buffer-=arg0;
	}

	@Override
	public double getOfferedEnergy() {return Math.min(IC2Voltage, buffer);}

	@Override
	public float getMaxSafeVoltage() {return 0;}

	@Override
	public void onOverVoltage() {}

	@Override
	public float getOutputVoltage() {return 0;}

	@Override
	public float getResistance() {return resistance;}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public void onOverloaded() {}

	@Override
	public int getInventorySize() {	return 0;}
}
