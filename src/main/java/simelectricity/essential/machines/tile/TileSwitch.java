package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerSwitch;

public class TileSwitch extends SETwoPortMachine implements ISESwitch, ISEEnergyNetUpdateHandler, ISESocketProvider, IGuiProviderTile {
    public volatile double current;

    public volatile double resistance = 0.001;
    public volatile double maxCurrent = 1;
    public volatile boolean isOn;

    /////////////////////////////////////////////////////////
    ///TileEntity
    /////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.maxCurrent = tagCompound.getDouble("maxCurrent");
        this.isOn = tagCompound.getBoolean("isOn");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("resistance", this.resistance);
        tagCompound.setDouble("maxCurrent", this.maxCurrent);
        tagCompound.setBoolean("isOn", this.isOn);

        return super.writeToNBT(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///ISEEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        if (this.isOn) {
            this.current = SEAPI.energyNetAgent.getCurrentMagnitude(input);
        } else {
            this.current = 0;
        }

        if (this.current > this.maxCurrent) {
            ((WorldServer) world).addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	setSwitchStatus(false);
                }
            });
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
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);

        nbt.setBoolean("isOn", this.isOn);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        this.isOn = nbt.getBoolean("isOn");

        markForRenderUpdate();

        super.onSyncDataFromServerArrived(nbt);
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
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
        this.isOn = isOn;
        SEAPI.energyNetAgent.updateTileParameter(this);

        markTileEntityForS2CSync();
    }
    
    ///////////////////////////////////
    /// IGuiProviderTile
    ///////////////////////////////////
	@Override
	public Container getContainer(EntityPlayer player, EnumFacing side) {
		return new ContainerSwitch(this);
	}
}
