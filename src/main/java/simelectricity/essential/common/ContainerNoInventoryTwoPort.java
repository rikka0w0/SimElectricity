package simelectricity.essential.common;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.utils.network.ISEDirectionSelectorEventHandler;

public abstract class ContainerNoInventoryTwoPort<TYPE extends SETwoPortMachine> extends ContainerNoInvAutoSync<TYPE> implements ISEDirectionSelectorEventHandler {
    public ContainerNoInventoryTwoPort(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void onDirectionSelected(EnumFacing direction, int mouseButton) {
        EnumFacing inputSide = this.host.inputSide, outputSide = this.host.outputSide;

        if (mouseButton == 0) {        //Left key
            if (outputSide == direction)
                outputSide = inputSide;
            inputSide = direction;
        } else if (mouseButton == 1) { //Right key
            if (inputSide == direction)
                inputSide = outputSide;
            outputSide = direction;
        }

        SEAPI.energyNetAgent.updateTileConnection(this.host);
        this.host.setFunctionalSide(inputSide, outputSide);
    }
}
