package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerNoInventory;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.essential.machines.tile.TileVoltageMeter;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerVoltageMeter extends ContainerNoInventory<TileVoltageMeter> implements ISEContainerUpdate, IContainerWithGui {
    public double voltage;

    public ContainerVoltageMeter(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        double voltage = tileEntity.voltage;

        //Look for any changes
        if (this.voltage == voltage)
            return;

        this.voltage = voltage;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, voltage);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataArrivedFromServer(Object[] data) {
        this.voltage = (Double) data[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiVoltageMeter(this);
    }
}
