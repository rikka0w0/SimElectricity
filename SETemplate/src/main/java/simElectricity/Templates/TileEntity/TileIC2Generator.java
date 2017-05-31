package simElectricity.Templates.TileEntity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.SEAPI;

import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.ISEWrenchable;
import simElectricity.API.DataProvider.ISEConstantPowerLoadData;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISETile;
import simElectricity.Templates.Common.TileSidedFacingMachine;

public class TileIC2Generator extends TileSidedFacingMachine implements IEnergySource, ISETile, ISEConstantPowerLoadData, ISEWrenchable, IEnergyNetUpdateHandler{
	public ForgeDirection functionalSide = ForgeDirection.NORTH;
	ISESubComponent tile = SEAPI.energyNetAgent.newComponent(this, this);
	
	public double inputPower = 0;	//Input rate
	
	public double bufferedEnergy = 0;
	public double maxBufferedEnergy = 10000;
	public double powerRate = 0;	//Output rate

	public double outputVoltage = 32;	//IC2 voltage!
	
	public boolean enabled = true;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;

        
        
        bufferedEnergy += inputPower;
        
        if (bufferedEnergy > maxBufferedEnergy){
        	bufferedEnergy = maxBufferedEnergy;
        	if (enabled){
        		SEAPI.energyNetAgent.markTileForUpdate(this);
        		enabled = false;
        	}
        		
        	inputPower = 0;
        }
        
        if (bufferedEnergy < maxBufferedEnergy / 2){
        	if (!enabled){
        		SEAPI.energyNetAgent.markTileForUpdate(this);
        		enabled = true;
        	}
        		
        }
        	
	}
        
	@Override
	public void onEnergyNetUpdate() {
		double V = SEAPI.energyNetAgent.getVoltage(tile);
		double Rcal = V*V/getRatedPower();
		
		if (Rcal > getMaximumResistance())
			Rcal = getMaximumResistance();
		if (Rcal < getMinimumResistance())
			Rcal = getMinimumResistance();
        
		inputPower = V*V/Rcal/SEAPI.ratioSE2IC;
	}
	
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        outputVoltage = tagCompound.getDouble("outputVoltage");
        bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("bufferedEnergy", bufferedEnergy);
        tagCompound.setDouble("outputVoltage", outputVoltage);
    }
	
	
    @Override
	public void onLoad() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
    }

    @Override
	public void onUnload() {
    	MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
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
		return bufferedEnergy > 0 ? Math.min(bufferedEnergy , outputVoltage) : 0;
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
		return 1;
	}


	@Override
	public double getRatedPower() {
		return outputVoltage * SEAPI.ratioSE2IC;
	}


	@Override
	public double getMinimumResistance() {
		return 100;
	}


	@Override
	public double getMaximumResistance() {
		return 100000;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{functionalSide};
	}


	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		return side == functionalSide ? this.tile : null;
	}


	@Override
	public boolean attachToEnergyNet() {
		return true;
	}


	@Override
	public ForgeDirection getFunctionalSide() {
		return this.functionalSide;
	}


	@Override
	public void setFunctionalSide(ForgeDirection newFunctionalSide) {
		functionalSide = newFunctionalSide;
	}
	
	@Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return true;
    }
}
