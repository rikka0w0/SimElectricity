package simelectricity.energybridge.ic2.cable;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.essential.cable.TileCable;

public class TileIc2Cable extends TileCable implements IEnergyStorage, IEnergySink, IEnergySource {

	public boolean addedToEnergyNet;
	
    public boolean isAddedToEnergyNet()
    {
        return this.addedToEnergyNet;
    }
    
    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote&!this.addedToEnergyNet)
        {
            //EnergyNet.getForWorld(this.worldObj).addTileEntity(this);
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }
	
    @Override
    public void invalidate()
    {
        if (!worldObj.isRemote&this.addedToEnergyNet)
        {
            //EnergyNet.getForWorld(this.worldObj).removeTileEntity(this);
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }

        super.invalidate();
    }
    
	public TileIc2Cable() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getStored() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStored(int energy) {
		// TODO Auto-generated method stub

	}

	@Override
	public int addEnergy(int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutput() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		// TODO Auto-generated method stub
		return 128;
	}

	@Override
	public void drawEnergy(double amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSourceTier() {
		return 1;
	}

	@Override
	public double getDemandedEnergy() {
		return 1;
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		// TODO Auto-generated method stub
		return 0;
	}

}
