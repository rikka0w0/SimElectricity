package simelectricity.essential.coverpanel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerVoltageSensor extends Container implements ISEContainerUpdate, ISEButtonEventHandler {
    private final VoltageSensorPanel panel;
    public boolean emitRedstoneSignal;
    public boolean inverted;
    public double thresholdVoltage;

    public ContainerVoltageSensor(VoltageSensorPanel panel, TileEntity te) {
        this.panel = panel;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void detectAndSendChanges() {
        boolean emitRedstoneSignal = this.panel.emitRedStoneSignal;
        boolean inverted = this.panel.inverted;
        double thresholdVoltage = this.panel.thresholdVoltage;

        //Look for any changes
        if (this.emitRedstoneSignal == emitRedstoneSignal &&
                this.inverted == inverted &&
                this.thresholdVoltage == thresholdVoltage)
            return;

        this.emitRedstoneSignal = emitRedstoneSignal;
        this.inverted = inverted;
        this.thresholdVoltage = thresholdVoltage;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, emitRedstoneSignal, inverted, thresholdVoltage);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataArrivedFromServer(Object[] data) {
        this.emitRedstoneSignal = (Boolean) data[0];
        this.inverted = (Boolean) data[1];
        this.thresholdVoltage = (Double) data[2];
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double thresholdVoltage = this.thresholdVoltage;
        boolean inverted = this.inverted;

        switch (buttonID) {
            case 0:
                thresholdVoltage -= 100;
                break;
            case 1:
                thresholdVoltage -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    thresholdVoltage -= 0.1;
                else
                    thresholdVoltage -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    thresholdVoltage += 0.1;
                else
                    thresholdVoltage += 1;
                break;
            case 4:
                thresholdVoltage += 10;
                break;
            case 5:
                thresholdVoltage += 100;
                break;

            case 6:
                inverted = !inverted;
                break;
        }

        if (thresholdVoltage < 0.1)
            thresholdVoltage = 0.1;
        if (thresholdVoltage > 500)
            thresholdVoltage = 500;

        panel.thresholdVoltage = thresholdVoltage;
        panel.inverted = inverted;

        panel.checkRedStoneSignal();
    }
}
