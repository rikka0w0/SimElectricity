package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventory;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerAdjustableResistor extends ContainerNoInventory<TileAdjustableResistor> implements ISEContainerUpdate, ISEButtonEventHandler {
    public double resistance;
    public double voltage;
    public double current;
    public double powerLevel;
    public double bufferedEnergy;

    public ContainerAdjustableResistor(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataArrivedFromServer(Object[] data) {
        this.resistance = (Double) data[0];
        this.voltage = (Double) data[1];
        this.current = (Double) data[2];
        this.powerLevel = (Double) data[3];
        this.bufferedEnergy = (Double) data[4];
    }

    @Override
    public void detectAndSendChanges() {
        double voltage = tileEntity.voltage;
        double resistance = tileEntity.resistance;
        double current = tileEntity.current;
        double powerLevel = tileEntity.powerLevel;
        double bufferedEnergy = tileEntity.bufferedEnergy;

        //Look for any changes
        if (this.resistance == resistance &&
                this.voltage == voltage &&
                this.current == current &&
                this.powerLevel == powerLevel &&
                this.bufferedEnergy == bufferedEnergy)
            return;

        this.resistance = resistance;
        this.voltage = voltage;
        this.current = current;
        this.powerLevel = powerLevel;
        this.bufferedEnergy = bufferedEnergy;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, resistance, voltage, current, powerLevel, bufferedEnergy);
            }
        }
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = tileEntity.resistance;

        switch (buttonID) {
            case 0:
                resistance -= 100;
                break;
            case 1:
                resistance -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    resistance -= 0.1;
                else
                    resistance -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    resistance += 0.1;
                else
                    resistance += 1;
                break;
            case 4:
                resistance += 10;
                break;
            case 5:
                resistance += 100;
                break;

            case 6:
                tileEntity.bufferedEnergy = 0;
                return;
        }

        if (resistance < 0.1)
            resistance = 0.1;
        if (resistance > 10000)
            resistance = 10000;

        tileEntity.resistance = resistance;

        SEAPI.energyNetAgent.updateTileParameter(this.tileEntity);
    }
}
