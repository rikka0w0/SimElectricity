package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerSwitch extends ContainerNoInventoryTwoPort<TileSwitch> implements ISEContainerUpdate, ISEButtonEventHandler, IContainerWithGui {
    public volatile double resistance;
    public volatile boolean isOn;
    public volatile double maxCurrent;
    public volatile double current;
    public volatile EnumFacing inputSide, outputSide;

    public ContainerSwitch(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        double resistance = this.tileEntity.resistance;
        boolean isOn = this.tileEntity.isOn;
        double maxCurrent = this.tileEntity.maxCurrent;
        double current = this.tileEntity.current;
        EnumFacing inputSide = this.tileEntity.inputSide, outputSide = this.tileEntity.outputSide;

        //Look for any changes
        if (this.resistance == resistance &&
                this.isOn == isOn &&
                this.maxCurrent == maxCurrent &&
                this.current == current &&
                this.inputSide == inputSide &&
                this.outputSide == outputSide)
            return;

        this.resistance = resistance;
        this.isOn = isOn;
        this.maxCurrent = maxCurrent;
        this.current = current;
        this.inputSide = inputSide;
        this.outputSide = outputSide;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, resistance, isOn, maxCurrent, current, inputSide, outputSide);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataArrivedFromServer(Object[] data) {
        resistance = (Double) data[0];
        isOn = (Boolean) data[1];
        maxCurrent = (Double) data[2];
        current = (Double) data[3];
        inputSide = (EnumFacing) data[4];
        outputSide = (EnumFacing) data[5];
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = tileEntity.resistance;
        double maxCurrent = tileEntity.maxCurrent;
        boolean isOn = this.isOn;

        boolean isOnChanged = false;

        switch (buttonID) {
            case 0:
                maxCurrent -= 100;
                break;
            case 1:
                maxCurrent -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    maxCurrent -= 0.1;
                else
                    maxCurrent -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    maxCurrent += 0.1;
                else
                    maxCurrent += 1;
                break;
            case 4:
                maxCurrent += 10;
                break;
            case 5:
                maxCurrent += 100;
                break;


            case 6:
                resistance -= 1;
                break;
            case 7:
                resistance -= 0.1;
                break;
            case 8:
                if (isCtrlPressed)
                    resistance -= 0.001;
                else
                    resistance -= 0.01;
                break;
            case 9:
                if (isCtrlPressed)
                    resistance += 0.001;
                else
                    resistance += 0.01;
                break;
            case 10:
                resistance += 0.1;
                break;
            case 11:
                resistance += 1;
                break;


            case 12:
                isOnChanged = true;
                isOn = !isOn;
                break;
            default:
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        if (maxCurrent < 0.1)
            maxCurrent = 0.1;
        if (maxCurrent > 1000)
            maxCurrent = 1000;

        tileEntity.resistance = resistance;
        tileEntity.maxCurrent = maxCurrent;

        if (isOnChanged) {
            tileEntity.setSwitchStatus(isOn);
        } else {
            SEAPI.energyNetAgent.updateTileParameter(tileEntity);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiSwitch(this);
    }
}
