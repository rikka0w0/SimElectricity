package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.essential.ConfigProvider;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerSE2RF;

public class TileSE2RF extends SESinglePortMachine implements ISEConstantPowerLoad, ISEEnergyNetUpdateHandler, ITickable, IGuiProviderTile, ISESocketProvider {
    public final static double ratedOutputPower = 100;	// W
    public final static double minInputVoltage = 85;	// V
    public final static double maxInputVoltage = 265;	// V
    public final static double bufferCapacity = 1000;	// J

    public double ouputPowerSetPoint = 1;
    public boolean enabled = true;
    
    public volatile double voltage;				// V
    public volatile double actualInputPower;    // J per sec = J per 20-tick
    public double bufferedEnergy;				// J
    
    public EnumFacing outputSide = EnumFacing.UP;

    public int calcRFPowerDemand(int offeredAmount, EnumFacing side) {
        int rfDemand = 0;

        TileEntity te = Utils.getTileEntitySafely(world, pos.offset(side));

		if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
			IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
			if (es != null) {
				rfDemand += es.receiveEnergy(offeredAmount, true);
			}
		}

        return rfDemand;
    }
    
    public int outpurRFPower(int offeredAmount, EnumFacing side) {
        int accpted = 0;

        TileEntity te = Utils.getTileEntitySafely(world, pos.offset(side));

		if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
			IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
			if (es != null) {
				accpted += es.receiveEnergy(offeredAmount, false);
			}
		}

        return accpted;
    }

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void update() {
        if (world.isRemote)
            return;
        
        
        int offeredAmount = (int)(ratedOutputPower / 20 * ConfigProvider.joule2rf);	// Energy per tick, RF
        int rfDemand = calcRFPowerDemand(offeredAmount, outputSide);	// Energy per tick, RF
        if (this.enabled) {
        	this.bufferedEnergy += actualInputPower / 20;	// Energy per tick, J
        	
            if (this.bufferedEnergy * ConfigProvider.joule2rf > rfDemand) {
    	        int rfAccepted = outpurRFPower(offeredAmount, outputSide);	// Energy per tick, RF
    	        this.bufferedEnergy -= rfAccepted / ConfigProvider.joule2rf;
    	        
    	        this.ouputPowerSetPoint = rfDemand * 20 / ConfigProvider.joule2rf;
    	        
    	        if (this.ouputPowerSetPoint < 1)
    	        	this.ouputPowerSetPoint = 1;
    	        
    	        if (this.bufferedEnergy > this.bufferCapacity)
    	        	this.enabled = false;
    	        
    	        SEAPI.energyNetAgent.updateTileParameter(this);
            }
        } else {
        	if (this.bufferedEnergy * ConfigProvider.joule2rf > rfDemand) {
        		int rfAccepted = outpurRFPower(offeredAmount, outputSide);
        		this.bufferedEnergy -= rfAccepted / ConfigProvider.joule2rf;
        	}
        	
            if (this.bufferedEnergy < this.bufferCapacity * 0.25) {
            	this.enabled = true;
            	SEAPI.energyNetAgent.updateTileParameter(this);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    public static class RFBuffer extends EnergyStorage{
        public RFBuffer(int capacity) {
            super(capacity);
        }

        @Override
        public boolean canReceive() {
            return false;
            //return this.maxReceive > 0;
        }

        public void setBuffer(int value) {
            this.energy = value;
        }
    }

    RFBuffer rfBuf = new RFBuffer(100000);

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) rfBuf;
        }
        return super.getCapability(capability, facing);
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = SEAPI.energyNetAgent.getVoltage(circuit);
        ISEConstantPowerLoad cachedParam = (ISEConstantPowerLoad) circuit;

        if (!cachedParam.isEnabled()) {
            this.actualInputPower = 0;
        } else {
            double Rcal = voltage * voltage / cachedParam.getRatedPower();

            if (Rcal > cachedParam.getMaximumResistance())
                Rcal = cachedParam.getMaximumResistance();
            if (Rcal < cachedParam.getMinimumResistance())
                Rcal = cachedParam.getMinimumResistance();

            this.actualInputPower = voltage * voltage / Rcal;
        }
    }

    ///////////////////////////////////
    /// ISEConstantPowerLoad
    ///////////////////////////////////
    public double getRatedPower() {
        return ouputPowerSetPoint;
    }

    public double getMinimumResistance() {
        return minInputVoltage * minInputVoltage / ratedOutputPower;
    }

    public double getMaximumResistance() {
        return maxInputVoltage * maxInputVoltage / ratedOutputPower;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    ///////////////////////////////////
    /// IGuiProviderTile
    ///////////////////////////////////
	@Override
	public Container getContainer(EntityPlayer player, EnumFacing side) {
		return new ContainerSE2RF(this);
	}

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
        return side == this.functionalSide ? 0 : -1;
    }
}
