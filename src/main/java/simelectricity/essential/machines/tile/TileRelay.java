package simelectricity.essential.machines.tile;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerRelay;

/**
 * Created by manageryzy on 12/18/2017.
 */
public class TileRelay extends SETwoPortMachine<ISESwitch> implements ISESwitch, ISEEnergyNetUpdateHandler, INamedContainerProvider2 {
    public TileRelay(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	public double current;

    public double resistance = 0.001;
    public boolean isOn;

    /////////////////////////////////////////////////////////
    ///BlockEntity
    /////////////////////////////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.isOn = tagCompound.getBoolean("isOn");
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        tagCompound.putDouble("resistance", this.resistance);
        tagCompound.putBoolean("isOn", this.isOn);

        return super.save(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///ISEEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        if (this.isOn) {
            this.current = this.input.getCurrentMagnitude();
        } else {
            this.current = 0;
        }
    }

    /////////////////////////////////////////////////////////
    ///ISESwitchData
    /////////////////////////////////////////////////////////
    @Override
    public boolean isOn() {
        return this.isOn;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);

        nbt.putBoolean("isOn", this.isOn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        this.isOn = nbt.getBoolean("isOn");

        markForRenderUpdate();

        super.onSyncDataFromServerArrived(nbt);
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
    /// Utils
    ///////////////////////////////////
    public void setSwitchStatus(boolean isOn) {
        if (this.isOn == isOn)
            return;

        this.isOn = isOn;
        SEAPI.energyNetAgent.updateTileParameter(this);

        markTileEntityForS2CSync();
    }

    ///////////////////////////////////
    /// MenuProvider
    ///////////////////////////////////
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
    	return new ContainerRelay(this, windowId, player);
    }
}
