package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import rikka.librikka.block.BlockUtils;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerSE2RF;

public class TileSE2RF extends SESinglePortMachine<ISEConstantPowerLoad> implements 
		ISEConstantPowerLoad, ISEEnergyNetUpdateHandler, ITickableTileEntity, INamedContainerProvider2 {
    public final static double bufferCapacity = 1000;	// J
    public double ratedOutputPower = 100;	            // W

    public double ouputPowerSetPoint = 1;
    public boolean enabled = true;
    
    public double voltage;				// V
    public double actualInputPower;    // J per sec = J per 20-tick
    public double bufferedEnergy;				// J
    public int rfDemandRateDisplay;
    public int rfOutputRateDisplay;

    public int calcRFPowerDemand(int offeredAmount, Direction side) {
        int rfDemand = 0;

        TileEntity te = BlockUtils.getTileEntitySafely(world, pos.offset(side));

		if (te != null) {
			IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).orElse(null);
			if (es != null) {
				rfDemand += es.receiveEnergy(offeredAmount, true);
			}
		}

        return rfDemand;
    }
    
    public int outpurRFPower(int offeredAmount, Direction side) {
        int accpted = 0;

        TileEntity te = BlockUtils.getTileEntitySafely(world, pos.offset(side));

		if (te != null) {
			IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).orElse(null);
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
    public void tick() {
        if (world.isRemote)
            return;
        
        boolean paramChanged = false;
        
        int offeredAmount = (int)(ratedOutputPower / 20 * SEAPI.energyNetAgent.joule2rf());	// Energy per tick, RF
        int rfDemand = calcRFPowerDemand(offeredAmount, getFacing());	// Energy per tick, RF
        this.rfDemandRateDisplay = rfDemand;
        if (((ISEConstantPowerLoad) circuit).isOn()) {
        	this.bufferedEnergy += actualInputPower / 20;	// Energy per tick, J
        	
        	double ouputPowerSetPoint = rfDemand * 20 / SEAPI.energyNetAgent.joule2rf();
	        if (ouputPowerSetPoint < 1) {
	        	ouputPowerSetPoint = 1;
	        }
	        
	        if (Math.abs(ouputPowerSetPoint - this.ouputPowerSetPoint) > 1e-6) {
	        	this.ouputPowerSetPoint = ouputPowerSetPoint;
	        	paramChanged = true;
	        }
        	
            if (this.bufferedEnergy * SEAPI.energyNetAgent.joule2rf() > rfDemand) {
    	        int rfAccepted = outpurRFPower(offeredAmount, getFacing());	// Energy per tick, RF
        		this.rfOutputRateDisplay = rfAccepted;
    	        this.bufferedEnergy -= rfAccepted / SEAPI.energyNetAgent.joule2rf();
    	        
    	        if (this.bufferedEnergy > this.bufferCapacity) {
    	        	this.enabled = false;
    	        	paramChanged = true;
    	        }
            } else {
            	this.rfOutputRateDisplay = 0;
            }
        } else {
        	if (this.bufferedEnergy * SEAPI.energyNetAgent.joule2rf() > rfDemand) {
        		int rfAccepted = outpurRFPower(offeredAmount, getFacing());
        		this.rfOutputRateDisplay = rfAccepted;
        		this.bufferedEnergy -= rfAccepted / SEAPI.energyNetAgent.joule2rf();
        	} else {
            	this.rfOutputRateDisplay = 0;
            }
        	
            if (this.bufferedEnergy < this.bufferCapacity * 0.25) {
            	this.enabled = true;
	        	paramChanged = true;
            }
        }
        
        if (paramChanged)
        	SEAPI.energyNetAgent.updateTileParameter(this);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        this.ratedOutputPower = tagCompound.getDouble("ratedOutputPower");
        this.ouputPowerSetPoint = tagCompound.getDouble("ouputPowerSetPoint");
        this.enabled = tagCompound.getBoolean("enabled");
        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("ratedOutputPower", this.ratedOutputPower);
        tagCompound.putDouble("ouputPowerSetPoint", this.ouputPowerSetPoint);
        tagCompound.putBoolean("enabled", this.enabled);
        tagCompound.putDouble("bufferedEnergy", this.bufferedEnergy);

        return super.write(tagCompound);
    }

    // TODO: getCapability
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == this.getFacing()) {
            return (LazyOptional<T>) LazyOptional.of(()->rfBufferHandler);
        }
        return super.getCapability(capability, facing);
    }

    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(Direction newFacing) {
        Direction oldFacing = getFacing();
        super.setFacing(newFacing);

        world.neighborChanged(this.pos.offset(oldFacing), this.getBlockState().getBlock(), this.pos);
        world.neighborChanged(this.pos.offset(newFacing), this.getBlockState().getBlock(), this.pos);
    }

    ///////////////////////////////////
    /// IEnergyStorage
    ///////////////////////////////////
    RFBufferHandler rfBufferHandler = new RFBufferHandler(this);
    protected static class RFBufferHandler implements IEnergyStorage {
    	protected TileSE2RF owner;
    	public RFBufferHandler(TileSE2RF owner) {
    		this.owner = owner;
    	}
    	
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
        	return 0;	// Can not receive
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtract())
                return 0;

            int energyExtracted = Math.min(getEnergyStored(), Math.min((int)(owner.ratedOutputPower / 20 * SEAPI.energyNetAgent.joule2rf()), maxExtract));
            if (!simulate)
            	owner.bufferedEnergy -= energyExtracted / SEAPI.energyNetAgent.joule2rf();
            return energyExtracted;
        }
        
		@Override
		public int getMaxEnergyStored() {
			return (int) (bufferCapacity * SEAPI.energyNetAgent.joule2rf());
		}

        @Override
        public int getEnergyStored() {
            return (int) (owner.bufferedEnergy * SEAPI.energyNetAgent.joule2rf());
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
    
    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = this.circuit.getVoltage();

        if (!cachedParam.isOn()) {
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
        return 1;
    }

    public double getMaximumResistance() {
        return 1e6;
    }

    public boolean isOn() {
        return enabled;
    }
    
    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerSE2RF(this, windowId);
	}

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == this.functionalSide ? 0 : -1;
    }
}
