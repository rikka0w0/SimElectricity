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
	public void onLoad() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
    }

    @Override
	public void onUnload() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
    }
	
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        
        float convertRatio = 1F;
        double vo = Energy.getVoltage(this);
        double po = convertRatio * 0.05 * outputVoltage * (outputVoltage-vo)/outputResistance;
        if (bufferedEnergy > 0 && po >= 0){
	        bufferedEnergy -= po;
	        
	        double error = powerRate - po;
	        if (Math.abs(error) > 0.1){
	        	outputVoltage += 10 * error;
	        	outputVoltage += 0.000001 * aError;
				aError += error;
				

	        }else {
				aError = 0;
			}
	        
	        if (aError>10000)
	        	aError = 10000;
	        if (aError<-10000)
	        	aError = -10000;        
	        
			if (outputVoltage>230)
				outputVoltage=230;
			if (outputVoltage<0)
				outputVoltage=0;
	        
	        outputResistance = 1F;
	        
        }else{
        	outputResistance = Float.MAX_VALUE;
        	aError = 0;
        }
        
       
        Energy.postTileChangeEvent(this);
        
        //System.out.print(po);
        //System.out.print(":");
        //System.out.print(powerRate); 
        //System.out.print(":");
        //System.out.println(bufferedEnergy);
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
		bufferedEnergy += powerRate;
		return 0;
	}

}
