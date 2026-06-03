package simelectricity.essential.machines.blockentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import rikka.librikka.block.BlockUtils;
import rikka.librikka.blockentity.INamedMenuProvider;
import rikka.librikka.blockentity.ITickableBlockEntity;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerSE2RF;

public class BlockEntitySE2RF extends SESinglePortMachine<ISEConstantPowerLoad> implements
		ISEConstantPowerLoad, ISEEnergyNetUpdateHandler, ITickableBlockEntity, INamedMenuProvider {
    public BlockEntitySE2RF(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

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

        IEnergyStorage es = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(side), side.getOpposite());
        if (es != null) {
            rfDemand += es.receiveEnergy(offeredAmount, true);
        }

        return rfDemand;
    }

    public int outpurRFPower(int offeredAmount, Direction side) {
        int accpted = 0;

        IEnergyStorage es = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(side), side.getOpposite());
        if (es != null) {
            accpted += es.receiveEnergy(offeredAmount, false);
        }

        return accpted;
    }

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (level.isClientSide)
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

    	        if (this.bufferedEnergy > BlockEntitySE2RF.bufferCapacity) {
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

            if (this.bufferedEnergy < BlockEntitySE2RF.bufferCapacity * 0.25) {
            	this.enabled = true;
	        	paramChanged = true;
            }
        }

        if (paramChanged)
        	SEAPI.energyNetAgent.updateTileParameter(this);
    }

    @Override
    public void loadAdditional(CompoundTag tagCompound, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tagCompound, registries);

        this.ratedOutputPower = tagCompound.getDouble("ratedOutputPower");
        this.ouputPowerSetPoint = tagCompound.getDouble("ouputPowerSetPoint");
        this.enabled = tagCompound.getBoolean("enabled");
        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
    	nbt.putDouble("ratedOutputPower", this.ratedOutputPower);
        nbt.putDouble("ouputPowerSetPoint", this.ouputPowerSetPoint);
        nbt.putBoolean("enabled", this.enabled);
        nbt.putDouble("bufferedEnergy", this.bufferedEnergy);

        super.saveAdditional(nbt, registries);
    }



    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(Direction newFacing) {
        Direction oldFacing = getFacing();
        super.setFacing(newFacing);

        level.neighborChanged(this.worldPosition.relative(oldFacing), this.getBlockState().getBlock(), this.worldPosition);
        level.neighborChanged(this.worldPosition.relative(newFacing), this.getBlockState().getBlock(), this.worldPosition);
    }

    ///////////////////////////////////
    /// IEnergyStorage
    ///////////////////////////////////
    public final RFBufferHandler rfBufferHandler = new RFBufferHandler(this);
    protected static class RFBufferHandler implements IEnergyStorage {
    	protected BlockEntitySE2RF owner;
    	public RFBufferHandler(BlockEntitySE2RF owner) {
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
			return (int) (BlockEntitySE2RF.bufferCapacity * SEAPI.energyNetAgent.joule2rf());
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
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerSE2RF(this, windowId, player);
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
