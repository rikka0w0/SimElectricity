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
    // In units of RF
    public double bufferedEnergy;

    public volatile double voltage;
    public volatile double actualInputPower;    //In units of J

    public int calcRFPowerDemand() {
        int rfDemand = 0;
        for (EnumFacing side: EnumFacing.values()) {
            TileEntity te = Utils.getTileEntitySafely(world, pos.offset(side));

            if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
                IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                if (es != null) {
                    rfDemand += es.receiveEnergy(10000, true);
                }
            }
        }

        return rfDemand;
    }

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void update() {
        if (world.isRemote)
            return;


        this.bufferedEnergy += actualInputPower * ConfigProvider.joule2rf;

        int rfDemand = calcRFPowerDemand();

        if (rfDemand != 0)
            System.out.println("Power demand: " + rfDemand);

        rfDemand = 0;
        for (EnumFacing side: EnumFacing.values()) {
            TileEntity te = Utils.getTileEntitySafely(world, pos.offset(side));

            if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
                IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                if (es != null) {
                    rfDemand += es.receiveEnergy(1000, false);
                }
            }
        }

        System.out.println("Accepted: " + rfDemand);
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
        return 100;
    }

    public double getMinimumResistance() {
        return 180*180/ getRatedPower();
    }

    public double getMaximumResistance() {
        return 265*265/ getRatedPower();
    }

    public boolean isEnabled() {
        return true;
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
