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
import simelectricity.essential.machines.tile.TileCurrentSensor;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerCurrentSensor extends ContainerNoInventoryTwoPort<TileCurrentSensor> implements ISEContainerUpdate, ISEButtonEventHandler, IContainerWithGui {
    public double thresholdCurrent, resistance;
    public EnumFacing inputSide, outputSide;
    public boolean absMode, inverted;

    public double current;
    public boolean emitRedstoneSignal;

    @SideOnly(Side.CLIENT)
    public String conditionString;

    public ContainerCurrentSensor(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        double thresholdCurrent = this.tileEntity.thresholdCurrent, resistance = this.tileEntity.resistance;
        EnumFacing inputSide = this.tileEntity.inputSide, outputSide = this.tileEntity.outputSide;
        boolean absMode = this.tileEntity.absMode, inverted = this.tileEntity.inverted;
        double current = this.tileEntity.current;
        boolean emitRedstoneSignal = this.tileEntity.emitRedstoneSignal;

        //Look for any changes
        if (this.thresholdCurrent == thresholdCurrent &&
                this.resistance == resistance &&
                this.inputSide == inputSide &&
                this.outputSide == outputSide &&
                this.absMode == absMode &&
                this.inverted == inverted &&
                this.current == current &&
                this.emitRedstoneSignal == emitRedstoneSignal)
            return;

        this.thresholdCurrent = thresholdCurrent;
        this.resistance = resistance;
        this.inputSide = inputSide;
        this.outputSide = outputSide;
        this.absMode = absMode;
        this.inverted = inverted;
        this.current = current;
        this.emitRedstoneSignal = emitRedstoneSignal;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, thresholdCurrent, resistance, inputSide, outputSide, absMode, inverted, current, emitRedstoneSignal);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataArrivedFromServer(Object[] data) {
        thresholdCurrent = (Double) data[0];
        resistance = (Double) data[1];
        inputSide = (EnumFacing) data[2];
        outputSide = (EnumFacing) data[3];
        absMode = (Boolean) data[4];
        inverted = (Boolean) data[5];
        current = (Double) data[6];
        emitRedstoneSignal = (Boolean) data[7];

        conditionString = absMode ? "|I|" : "I";
        conditionString += inverted ? "<" : ">";
        conditionString += SEUnitHelper.getCurrentStringWithUnit(thresholdCurrent);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = tileEntity.resistance;
        double thresholdCurrent = tileEntity.thresholdCurrent;
        boolean absMode = tileEntity.absMode;
        boolean inverted = tileEntity.inverted;

        switch (buttonID) {
            case 0:
                thresholdCurrent -= 100;
                break;
            case 1:
                thresholdCurrent -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    thresholdCurrent -= 0.1;
                else
                    thresholdCurrent -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    thresholdCurrent += 0.1;
                else
                    thresholdCurrent += 1;
                break;
            case 4:
                thresholdCurrent += 10;
                break;
            case 5:
                thresholdCurrent += 100;
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
                inverted = !inverted;
                break;

            case 13:
                absMode = !absMode;
                break;
            default:
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        if (thresholdCurrent < 0.1)
            thresholdCurrent = 0.1;
        if (thresholdCurrent > 1000)
            thresholdCurrent = 1000;

        if (this.tileEntity.resistance != resistance) {
            this.tileEntity.resistance = resistance;
            SEAPI.energyNetAgent.updateTileParameter(tileEntity);
        }

        if (tileEntity.thresholdCurrent != thresholdCurrent) {
            tileEntity.thresholdCurrent = thresholdCurrent;
            this.tileEntity.checkRedstoneStatus();
        }

        if (this.tileEntity.inverted != inverted) {
            this.tileEntity.inverted = inverted;
            this.tileEntity.checkRedstoneStatus();
        }

        if (this.tileEntity.absMode != absMode) {
            this.tileEntity.absMode = absMode;
            this.tileEntity.checkRedstoneStatus();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiCurrentSensor(this);
    }
}
