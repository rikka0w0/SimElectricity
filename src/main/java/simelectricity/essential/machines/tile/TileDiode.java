package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEDiode;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerDiode;

public class TileDiode extends SETwoPortMachine implements ISEDiode, ISEEnergyNetUpdateHandler, ISESocketProvider, IGuiProviderTile {
    public volatile double inputVoltage, outputVoltage;

    /////////////////////////////////////////////////////////
    ///ISEDiodeData
    /////////////////////////////////////////////////////////
    @Override
    public double getForwardResistance() {
        return 0.1;
    }

    @Override
    public double getSaturationCurrent() {
        return 1e-6;
    }

    @Override
    public double getThermalVoltage() {
        return 26e-6;
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.inputVoltage = SEAPI.energyNetAgent.getVoltage(input);
        this.outputVoltage = SEAPI.energyNetAgent.getVoltage(input.getComplement());
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
    /// IGuiProviderTile
    ///////////////////////////////////
	@Override
	public Container getContainer(EntityPlayer player, EnumFacing side) {
		return new ContainerDiode(this);
	}
}
