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
        
        double KP = 0.5, KI = 0.1;
        double Vo = Energy.getVoltage(this);
        double Pin = powerRate * Energy.ic2ConvertRatio;
        double RSet = Vo * (outputVoltage - Vo) / Pin;
        
        if (Pin < 10E-14){
        	RSet = 10E5;
        }
        
        double po = Vo * (outputVoltage - Vo) / outputResistance;
        
        double Rerr = RSet - outputResistance;
        double delta = KP*Rerr + KI*lastErr;
        outputResistance += delta;
        lastErr = Rerr;
        
        if (outputResistance <0.1)
        	outputResistance = 0.1;
        if (outputResistance > 10E4)
        	outputResistance = 10E4;
        
        bufferedEnergy -= Math.min(powerRate,po / Energy.ic2ConvertRatio);
        
        if (bufferedEnergy < 0)
        	outputResistance = Float.MAX_VALUE;
        
        Energy.postTileChangeEvent(this);
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
