package simelectricity.essential.common;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.essential.utils.network.ISEDirectionSelectorEventHandler;

public abstract class ContainerNoInventoryTwoPort<TYPE extends SETwoPortMachine> extends ContainerNoInventory<TYPE> implements ISEDirectionSelectorEventHandler{
	public ContainerNoInventoryTwoPort(TileEntity tileEntity) {
		super(tileEntity);
	}

	@Override
	public void onDirectionSelected(ForgeDirection direction, int mouseButton) {
		ForgeDirection inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		
	    if (mouseButton == 0) {        //Left key
	        if (outputSide == direction)
	            outputSide = inputSide;
	        inputSide = direction;
	    } else if (mouseButton == 1) { //Right key
	        if (inputSide == direction)
	            inputSide = outputSide;
	        outputSide = direction;
        }
		
	    SEAPI.energyNetAgent.updateTileConnection(tileEntity);
	    tileEntity.setFunctionalSide(inputSide, outputSide);
	}
}
