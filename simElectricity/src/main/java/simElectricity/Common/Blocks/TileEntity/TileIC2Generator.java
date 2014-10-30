package simElectricity.Common.Blocks.TileEntity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.Common.TileStandardSEMachine;

public class TileIC2Generator extends TileStandardSEMachine implements IEnergySource{
	public double bufferedEnergy = 0;
	public double maxBufferedEnergy = 10000;
	public double powerRate = 0;
	public double lastErr = 0;
	
	public double resistance = Double.MAX_VALUE;
	public double outputVoltage = 1;	//IC2 voltage!
	
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        
        double KP = 1, KI = 0.1;
        double Vi = Energy.getVoltage(this);
        double Pin = Vi * Vi / resistance;				//Power injected in SE unit
        double Pin_IC2 = Pin / Energy.ic2ConvertRatio;	//Power injected in Eu unit
        double newR = resistance;
        double Pout =  powerRate * Energy.ic2ConvertRatio;
        
        double RSet = Pout > 10E-14 ? Vi * Vi / Pout : 10E5; //Avoid NaH
        double Rerr = RSet - newR;
        
	    newR += KP*Rerr + KI*lastErr;
	    lastErr = Rerr;
	        
	    if (newR <0.1)
	    	newR = 0.1;
	    if (newR > 10E5)
	    	newR = 10E5;
        
	    bufferedEnergy += Pin_IC2;
	    
        //Buffer is full
        if (bufferedEnergy > maxBufferedEnergy){
        	newR = Float.MAX_VALUE;
        }
        
        //Large enough resistance error
        if (Math.abs(newR - resistance) > 10E-3){
        	resistance = newR;
        	Energy.postTileChangeEvent(this);
        }
	}
        
	//TileStandardSEMachine------------------------------------------------------------------------
	@Override
	public double getResistance() {
		return resistance;
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

	//IC2------------------------------------------------------------------------------------------
	@Override
	public boolean emitsEnergyTo(TileEntity paramTileEntity, ForgeDirection paramForgeDirection) {
		return true;
	}

	/**
	 * Energy output provided by the source this tick.
	 * This is typically Math.min(stored energy, max output/tick).
	 * 
	 * @note Modifying the energy net from this method is disallowed.
	 * 
	 * @return Energy offered this tick
	 */
	@Override
	public double getOfferedEnergy() {
		return bufferedEnergy > 0 ? outputVoltage : 0;
	}

	/**
	 * Draw energy from this source's buffer.
	 * 
	 * If the source doesn't have a buffer, this is a no-op.
	 * 
	 * @param amount amount of EU to draw, may be negative
	 */
	@Override
	public void drawEnergy(double paramDouble) {
		bufferedEnergy -= paramDouble;
		powerRate = paramDouble;	
	}

	/**
	 * Determine the tier of this energy source.
	 * 1 = LV, 2 = MV, 3 = HV, 4 = EV etc.
	 * 
	 * @note Modifying the energy net from this method is disallowed.
	 *
	 * @return tier of this energy source
	 */
	@Override
	public int getSourceTier() {
		// TODO Auto-generated method stub
		return 0;
	}
}
