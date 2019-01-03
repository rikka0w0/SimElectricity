package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.tileentity.IGuiProviderTile;
import scala.tools.nsc.settings.RC;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerSource;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.ConfigProvider;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerRF2SE;

public class TileRF2SE extends SESinglePortMachine implements ISEConstantPowerSource, ISEEnergyNetUpdateHandler, ITickable, IGuiProviderTile, ISESocketProvider {
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
    public void update() {
        if (world.isRemote)
            return;

        if (this.bufferedEnergy > bufferCapacity) {
            acceptRF = false;
        } else if (this.bufferedEnergy < bufferCapacity * 0.25) {
            acceptRF = true;
        }

        boolean paramChanged = false;

        if (((ISEConstantPowerSource) this.circuit).isOn()) {
            double ouputPowerSetPoint = this.actualInputPower * 20.0 / ConfigProvider.joule2rf;  // J per Sec
            if (ouputPowerSetPoint < 1) {
                ouputPowerSetPoint = 1;
            }

            if (Math.abs(ouputPowerSetPoint - this.ouputPowerSetPoint) > 1e-6) {
                this.ouputPowerSetPoint = ouputPowerSetPoint;
                paramChanged = true;
            }

            double RFConsumed = actualOutputPower / 20.0 * ConfigProvider.joule2rf;
            //if (RFConsumed < 1.0)
                //RFConsumed = 1.0;
            bufferedEnergy -= MathHelper.floor(RFConsumed);

            if (this.bufferedEnergy < 2.0*RFConsumed) {
                this.enabled = false;
                paramChanged = true;
            }
        } else {
            if (this.bufferedEnergy > this.bufferCapacity * 0.25) {
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
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.ratedOutputPower = tagCompound.getDouble("ratedOutputPower");
        this.ouputPowerSetPoint = tagCompound.getDouble("ouputPowerSetPoint");
        this.enabled = tagCompound.getBoolean("enabled");
        this.acceptRF = tagCompound.getBoolean("acceptRF");
        this.bufferedEnergy = tagCompound.getInteger("bufferedEnergy");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("ratedOutputPower", this.ratedOutputPower);
        tagCompound.setDouble("ouputPowerSetPoint", this.ouputPowerSetPoint);
        tagCompound.setBoolean("enabled", this.enabled);
        tagCompound.setBoolean("acceptRF", this.acceptRF);
        tagCompound.setInteger("bufferedEnergy", this.bufferedEnergy);

        return super.writeToNBT(tagCompound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == this.facing) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == this.facing) {
            return (T) rfBufferHandler;
        }
        return super.getCapability(capability, facing);
    }

    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(EnumFacing newFacing) {
        EnumFacing oldFacing = this.facing;
        super.setFacing(newFacing);

        world.neighborChanged(this.pos.offset(oldFacing), this.blockType, this.pos);
        world.neighborChanged(this.pos.offset(newFacing), this.blockType, this.pos);
    }

    ///////////////////////////////////
    /// IEnergyStorage
    ///////////////////////////////////
    RFBufferHandler rfBufferHandler = new RFBufferHandler(this);
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
            int energyReceived = Math.min((int)(owner.ratedOutputPower / 20.0 * ConfigProvider.joule2rf), maxReceive);
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
        this.voltage = SEAPI.energyNetAgent.getVoltage(circuit);
        ISEConstantPowerSource cachedParam = (ISEConstantPowerSource) circuit;

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
    /// IGuiProviderTile
    ///////////////////////////////////
    @Override
    public Container getContainer(EntityPlayer player, EnumFacing side) {
        return new ContainerRF2SE(this);
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
        return side == this.functionalSide ? 1 : -1;
    }
}
