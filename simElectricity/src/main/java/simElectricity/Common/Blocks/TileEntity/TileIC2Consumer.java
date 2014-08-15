package simElectricity.Common.Blocks.TileEntity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.Common.TileSidedGenerator;

public class TileIC2Consumer extends TileSidedGenerator implements IEnergySink{
	public double bufferedEnergy = 0;
	public double maxBufferedEnergy = 10000;
	public double powerRate = 0;
	public double aError = 0;
	
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        
        outputVoltage = 230;
        double vo = Energy.getVoltage(this);
        double po = vo * (outputVoltage - vo) / outputResistance;
        boolean update = false;
        if (powerRate > 0 && Math.abs(powerRate - po) > 0.1){
        	outputResistance = (float) (vo * (outputVoltage - vo) / powerRate); 
        	update = true;
        }
        
        if (outputResistance <1)
        	outputResistance = 1;
        
        bufferedEnergy -= Math.min(powerRate,po);
        
        if (bufferedEnergy < 0)
        	outputResistance = Float.MAX_VALUE;
        
        if (update)
        	Energy.postTileChangeEvent(this);
	}
	
    @Override
	public void onLoad() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
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

}
