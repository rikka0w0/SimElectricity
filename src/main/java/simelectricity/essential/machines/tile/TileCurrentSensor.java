package simelectricity.essential.machines.tile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import rikka.librikka.blockentity.INamedMenuProvider;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerCurrentSensor;

public class TileCurrentSensor extends SETwoPortMachine<ISESwitch> implements
		ISESwitch, ISEEnergyNetUpdateHandler, INamedMenuProvider {
    public TileCurrentSensor(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	public double current;
    public boolean emitRedstoneSignal;

    public double resistance = 0.001;
    public double thresholdCurrent = 1;
    public boolean absMode, inverted;


    /////////////////////////////////////////////////////////
    ///BlockEntity
    /////////////////////////////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.thresholdCurrent = tagCompound.getDouble("thresholdCurrent");
        this.absMode = tagCompound.getBoolean("absMode");
        this.inverted = tagCompound.getBoolean("inverted");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
    	nbt.putDouble("resistance", this.resistance);
    	nbt.putDouble("thresholdCurrent", this.thresholdCurrent);
    	nbt.putBoolean("absMode", this.absMode);
    	nbt.putBoolean("inverted", this.inverted);

        super.saveAdditional(nbt);
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
            this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
            return true;
        }
        return false;
    }

    public void checkRedstoneStatus() {
        double current = this.absMode ? Math.abs(this.current) : this.current;
        this.setRedstone(current > this.thresholdCurrent ^ this.inverted);
    }

    ///////////////////////////////////
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerCurrentSensor(this, windowId, player);
	}
}
