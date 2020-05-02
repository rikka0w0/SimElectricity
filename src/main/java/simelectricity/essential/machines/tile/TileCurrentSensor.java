package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerCurrentSensor;

public class TileCurrentSensor extends SETwoPortMachine<ISESwitch> implements ISESwitch, ISEEnergyNetUpdateHandler, ISESocketProvider, INamedContainerProvider {
    public double current;
    public boolean emitRedstoneSignal;

    public double resistance = 0.001;
    public double thresholdCurrent = 1;
    public boolean absMode, inverted;


    /////////////////////////////////////////////////////////
    ///TileEntity
    /////////////////////////////////////////////////////////
    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.thresholdCurrent = tagCompound.getDouble("thresholdCurrent");
        this.absMode = tagCompound.getBoolean("absMode");
        this.inverted = tagCompound.getBoolean("inverted");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("resistance", this.resistance);
        tagCompound.putDouble("thresholdCurrent", this.thresholdCurrent);
        tagCompound.putBoolean("absMode", this.absMode);
        tagCompound.putBoolean("inverted", this.inverted);

        return super.write(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///ISEEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.current = this.input.getCurrentMagnitude();

      //Update the world from the server thread
        Utils.enqueueServerWork(this::checkRedstoneStatus);
    }

    /////////////////////////////////////////////////////////
    ///ISESwitchData
    /////////////////////////////////////////////////////////
    @Override
    public boolean isOn() {
        return true;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        if (side == this.inputSide)
            return 2;
        else if (side == this.outputSide)
            return 4;
        else
            return -1;
    }

    ///////////////////////////////////
    /// Redstone
    ///////////////////////////////////
    private boolean setRedstone(boolean status) {
        if (this.emitRedstoneSignal != status) {
            this.emitRedstoneSignal = status;
            this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockState().getBlock());
            return true;
        }
        return false;
    }

    public void checkRedstoneStatus() {
        double current = this.absMode ? Math.abs(this.current) : this.current;
        this.setRedstone(current > this.thresholdCurrent ^ this.inverted);
    }
    
    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerCurrentSensor(this, windowId);
	}
}
