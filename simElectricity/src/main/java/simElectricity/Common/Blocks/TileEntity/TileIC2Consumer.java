package simElectricity.Common.Blocks.TileEntity;

import java.util.List;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.Common.TileSidedGenerator;

public class TileIC2Consumer extends TileSidedGenerator implements IEnergySink, INetworkEventHandler{
	public double bufferedEnergy = 0;
	public double maxBufferedEnergy = 10000;
	public double powerRate = 0;
	public double lastErr = 0;
	
	
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        
        double KP = 1, KI = 0.1;
        double Vo = Energy.getVoltage(this);
        double Po = Vo * (outputVoltage - Vo) / outputResistance;	//In SE unit
        double Pin = powerRate * Energy.ic2ConvertRatio;			//In SE unit
        double newR = outputResistance;
        
        double RSet = Vo * (outputVoltage - Vo) / Pin;
        if (Pin < 10E-14)	RSet = 10E5;	//Avoid NaH
	                
	    double Rerr = RSet - newR;
	    //double delta = KP*Rerr + KI*lastErr;	//For debugging
	    newR += KP*Rerr + KI*lastErr;
	    lastErr = Rerr;
	        
	    if (newR <0.1)
	    	newR = 0.1;
	    if (newR > 10E5)
	    	newR = 10E5;
        
        bufferedEnergy -= Math.min(powerRate,Po / Energy.ic2ConvertRatio);
        
        //Buffer runs out
        if (bufferedEnergy < 0){
        	newR = Float.MAX_VALUE;
        }
        
        //Large enough resistance error
        if (Math.abs(newR - outputResistance) > 10E-3){
        	outputResistance = newR;
        	Energy.postTileChangeEvent(this);
        }
	}
	
    @Override
	public void onLoad() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
        
    	if (this.outputResistance == Double.MAX_VALUE) {
            outputVoltage = 230;
        }
    }

    @Override
	public void onUnload() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
    }
    
	@Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return true;
    }

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return maxBufferedEnergy - bufferedEnergy > 0 ? maxBufferedEnergy - bufferedEnergy : 0;
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		powerRate = amount;	
		bufferedEnergy += amount;
		return 0;
	}

	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		//Handling on server side
		if (!worldObj.isRemote){
			for (String s:fields){
		        if (s.contentEquals("outputVoltage"))
		            Energy.postTileChangeEvent(this);
			}
		}
	}

	@Override
	public void addNetworkFields(List fields) {

	}
}
