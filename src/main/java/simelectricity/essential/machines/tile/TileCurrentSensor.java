package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerCurrentSensor;

public class TileCurrentSensor extends SETwoPortMachine implements ISESwitch, IEnergyNetUpdateHandler, ISESocketProvider, IGuiProviderTile {
    public double current;
    public boolean emitRedstoneSignal;

    public double resistance = 0.001;
    public double thresholdCurrent = 1;
    public boolean absMode, inverted;


    /////////////////////////////////////////////////////////
    ///TileEntity
    /////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.thresholdCurrent = tagCompound.getDouble("thresholdCurrent");
        this.absMode = tagCompound.getBoolean("absMode");
        this.inverted = tagCompound.getBoolean("inverted");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("resistance", this.resistance);
        tagCompound.setDouble("thresholdCurrent", this.thresholdCurrent);
        tagCompound.setBoolean("absMode", this.absMode);
        tagCompound.setBoolean("inverted", this.inverted);

        return super.writeToNBT(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///IEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.current = SEAPI.energyNetAgent.getCurrentMagnitude(input);

        WorldServer world = (WorldServer) this.world;
        world.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                TileCurrentSensor.this.checkRedstoneStatus();    //Update the world from the server thread
            }
        });
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
    private boolean setRedstone(boolean status) {
        if (this.emitRedstoneSignal != status) {
            this.emitRedstoneSignal = status;
            this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
            return true;
        }
        return false;
    }

    public void checkRedstoneStatus() {
        double current = this.absMode ? Math.abs(this.current) : this.current;
        this.setRedstone(current > this.thresholdCurrent ^ this.inverted);
    }
    
    ///////////////////////////////////
    /// IGuiProviderTile
    ///////////////////////////////////
	@Override
	public Container getContainer(EntityPlayer player, EnumFacing side) {
		return new ContainerCurrentSensor(this);
	}
}
