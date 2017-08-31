package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerNoInventory;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerQuantumGenerator extends ContainerNoInventory<TileQuantumGenerator> implements ISEContainerUpdate, ISEButtonEventHandler, IContainerWithGui {
    public double internalVoltage;
    public double resistance;
    public double voltage;
    public double current;

    @SideOnly(Side.CLIENT)
    public double outputPower;

    public ContainerQuantumGenerator(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataArrivedFromServer(Object[] data) {
        this.internalVoltage = (Double) data[0];
        this.resistance = (Double) data[1];
        this.voltage = (Double) data[2];
        this.current = (Double) data[3];

        this.outputPower = this.current * this.voltage;
    }

    @Override
    public void detectAndSendChanges() {
        double internalVoltage = tileEntity.internalVoltage;
        double resistance = tileEntity.resistance;

        double voltage = tileEntity.voltage;
        double current = tileEntity.current;

        //Look for any changes
        if (this.internalVoltage == internalVoltage &&
                this.resistance == resistance &&
                this.voltage == voltage &&
                this.current == current)
            return;

        this.voltage = voltage;
        this.internalVoltage = voltage;
        this.resistance = resistance;
        this.current = current;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, internalVoltage, resistance, voltage, current);
            }
        }
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = tileEntity.resistance;
        double internalVoltage = tileEntity.internalVoltage;

        switch (buttonID) {
            case 0:
                resistance -= 1;
                break;
            case 1:
                resistance -= 0.1;
                break;
            case 2:
                if (isCtrlPressed)
                    resistance -= 0.001;
                else
                    resistance -= 0.01;
                break;
            case 3:
                if (isCtrlPressed)
                    resistance += 0.001;
                else
                    resistance += 0.01;
                break;
            case 4:
                resistance += 0.1;
                break;
            case 5:
                resistance += 1;
                break;


            case 6:
                if (isCtrlPressed)
                    internalVoltage -= 1000;
                else
                    internalVoltage -= 100;
                break;
            case 7:
                internalVoltage -= 10;
                break;
            case 8:
                internalVoltage -= 1;
                break;
            case 9:
                internalVoltage += 1;
                break;
            case 10:
                internalVoltage += 10;
                break;
            case 11:
                if (isCtrlPressed)
                    internalVoltage += 1000;
                else
                    internalVoltage += 100;
                break;
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        if (internalVoltage < 0.1)
            internalVoltage = 0.1;
        if (internalVoltage > 10000)
            internalVoltage = 10000;

        tileEntity.resistance = resistance;
        tileEntity.internalVoltage = internalVoltage;

        SEAPI.energyNetAgent.updateTileParameter(this.tileEntity);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiQuantumGenerator(this);
    }
}
