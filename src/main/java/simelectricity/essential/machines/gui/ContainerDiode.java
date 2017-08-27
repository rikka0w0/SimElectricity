package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerDiode extends ContainerNoInventoryTwoPort<TileDiode> implements ISEContainerUpdate {
    public double inputVoltage, outputVoltage;
    public EnumFacing inputSide, outputSide;

    @SideOnly(Side.CLIENT)
    public boolean forwardBiased;

    public ContainerDiode(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        double inputVoltage = this.tileEntity.inputVoltage, outputVoltage = this.tileEntity.outputVoltage;
        EnumFacing inputSide = this.tileEntity.inputSide, outputSide = this.tileEntity.outputSide;

        //Look for any changes
        if (this.inputVoltage == inputVoltage &&
                this.outputVoltage == outputVoltage &&
                this.inputSide == inputSide &&
                this.outputSide == outputSide)
            return;

        this.inputVoltage = inputVoltage;
        this.outputVoltage = outputVoltage;
        this.inputSide = inputSide;
        this.outputSide = outputSide;

        //Send change to all crafter
        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.sendToClient((EntityPlayerMP) crafter, inputVoltage, outputVoltage, inputSide, outputSide);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataArrivedFromServer(Object[] data) {
        inputVoltage = (Double) data[0];
        outputVoltage = (Double) data[1];
        inputSide = (EnumFacing) data[2];
        outputSide = (EnumFacing) data[3];

        forwardBiased = inputVoltage > outputVoltage;
    }
}
