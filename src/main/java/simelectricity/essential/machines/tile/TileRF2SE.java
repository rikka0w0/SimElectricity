package simelectricity.essential.machines.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerSource;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerRF2SE;

public class TileRF2SE extends SESinglePortMachine<ISEConstantPowerSource> implements 
		ISEConstantPowerSource, ISEEnergyNetUpdateHandler, ITickableTileEntity, INamedContainerProvider2 {
    public final static int bufferCapacity = 1000;	// RF
    public double ratedOutputPower = 100;	            // W

    public double ouputPowerSetPoint = 1;
    public boolean enabled = false;
    public boolean acceptRF = false;

    public double voltage;				// V
    public double actualOutputPower;    // J per sec = J per 20-tick
    public int bufferedEnergy;		    // RF
    public int actualInputPower;        // RF per tick
    public int rfInputRateDisplay;

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (world.isRemote)
            return;

        if (this.bufferedEnergy > bufferCapacity) {
            acceptRF = false;
        } else if (this.bufferedEnergy < bufferCapacity * 0.25) {
            acceptRF = true;
        }

        boolean paramChanged = false;

        if (((ISEConstantPowerSource) this.circuit).isOn()) {
            double ouputPowerSetPoint = this.actualInputPower * 20.0 / SEAPI.energyNetAgent.joule2rf();  // J per Sec
            if (ouputPowerSetPoint < 1) {
                ouputPowerSetPoint = 1;
            }

            if (Math.abs(ouputPowerSetPoint - this.ouputPowerSetPoint) > 1e-6) {
                this.ouputPowerSetPoint = ouputPowerSetPoint;
                paramChanged = true;
            }

            double RFConsumed = actualOutputPower / 20.0 * SEAPI.energyNetAgent.joule2rf();
            if (RFConsumed < 1.0)
                RFConsumed = 1.0;
            bufferedEnergy -= MathHelper.floor(RFConsumed);

            if (this.bufferedEnergy < 2.0*RFConsumed) {
                this.enabled = false;
                paramChanged = true;
            }
        } else {
            if (this.bufferedEnergy > TileRF2SE.bufferCapacity * 0.25) {
                this.enabled = true;
                paramChanged = true;
            }
        }

        if (paramChanged)
            SEAPI.energyNetAgent.updateTileParameter(this);

        rfInputRateDisplay = actualInputPower;
        if (acceptRF)
            actualInputPower = 0;
    }

    @Override
    public void read(BlockState blockState, CompoundNBT tagCompound) {
        super.read(blockState, tagCompound);

        this.ratedOutputPower = tagCompound.getDouble("ratedOutputPower");
        this.ouputPowerSetPoint = tagCompound.getDouble("ouputPowerSetPoint");
        this.enabled = tagCompound.getBoolean("enabled");
        this.acceptRF = tagCompound.getBoolean("acceptRF");
        this.bufferedEnergy = tagCompound.getInt("bufferedEnergy");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("ratedOutputPower", this.ratedOutputPower);
        tagCompound.putDouble("ouputPowerSetPoint", this.ouputPowerSetPoint);
        tagCompound.putBoolean("enabled", this.enabled);
        tagCompound.putBoolean("acceptRF", this.acceptRF);
        tagCompound.putInt("bufferedEnergy", this.bufferedEnergy);

        return super.write(tagCompound);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == this.getFacing()) {
            return energyHdlerCap.cast();
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
    private final LazyOptional<?> energyHdlerCap = LazyOptional.of(()->rfBufferHandler);
    protected static class RFBufferHandler implements IEnergyStorage {
        protected TileRF2SE owner;
        public RFBufferHandler(TileRF2SE owner) {
            this.owner = owner;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive())
                return 0;

            if (!owner.acceptRF)
                return 0;   // Buffer is full

            // Min (Available space in the buffer, maximum input RF per tick, maximum available RF in this tick
            int energyReceived = Math.min((int)(owner.ratedOutputPower / 20.0 * SEAPI.energyNetAgent.joule2rf()), maxReceive);
            if (!simulate) {
                owner.bufferedEnergy += energyReceived;
                owner.actualInputPower += energyReceived;
            }
            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;   // Can not extract
        }

        @Override
        public int getMaxEnergyStored() {
            return bufferCapacity;
        }

        @Override
        public int getEnergyStored() {
            return owner.bufferedEnergy;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = this.circuit.getVoltage();

        if (!cachedParam.isOn()) {
            this.actualOutputPower = 0;
        } else {
            if (voltage < cachedParam.getMinimumOutputVoltage()) {
                double Rcal = cachedParam.getMinimumOutputVoltage() * cachedParam.getMinimumOutputVoltage() / cachedParam.getRatedPower();
                double Vint = 2 * cachedParam.getMinimumOutputVoltage();
                this.actualOutputPower = (Vint - voltage) / Rcal * voltage;
            } else if (voltage > cachedParam.getMaximumOutputVoltage()) {
                double Rcal = cachedParam.getMaximumOutputVoltage() * cachedParam.getMaximumOutputVoltage() / cachedParam.getRatedPower();
                double Vint = 2 * cachedParam.getMaximumOutputVoltage();
                this.actualOutputPower = (Vint - voltage) / Rcal * voltage;
            } else {
                this.actualOutputPower = cachedParam.getRatedPower();
            }
        }
    }

    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
    @Override
    public double getRatedPower() {
        return ouputPowerSetPoint;
    }

    @Override
    public double getMinimumOutputVoltage() {
        return 90;
    }

    @Override
    public double getMaximumOutputVoltage() {
        return 265/2.0F;
    }

    @Override
    public boolean isOn() {
        return enabled;
    }

    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerRF2SE(this, windowId);
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == this.functionalSide ? 1 : -1;
    }
}
