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
import rikka.librikka.Utils;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerSE2RF;

public class TileSE2RF extends SESinglePortMachine implements ISEConstantPowerLoad, ITickable, IGuiProviderTile {
	public double bufferedEnergy;
	
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
        
        this.bufferedEnergy += SEAPI.energyNetAgent.getVoltage(circuit);

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
}
