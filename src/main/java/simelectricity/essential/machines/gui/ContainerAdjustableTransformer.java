package simelectricity.essential.machines.gui;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventory;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.ISEDirectionSelectorEventHandler;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerAdjustableTransformer extends ContainerNoInventory<TileAdjustableTransformer> implements ISEContainerUpdate, ISEButtonEventHandler, ISEDirectionSelectorEventHandler{
	public double ratio, outputResistance;
	public ForgeDirection inputSide, outputSide;
	
	public ContainerAdjustableTransformer(TileEntity tileEntity) {
		super(tileEntity);
	}

	@Override
	public void detectAndSendChanges() {
		double ratio = tileEntity.ratio, outputResistance = tileEntity.outputResistance;
		ForgeDirection inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		
		//Look for any changes
		if (this.ratio == ratio &&
			this.outputResistance == outputResistance &&
			this.inputSide == inputSide &&
			this.outputSide == outputSide)
			return;

		this.ratio = ratio;
		this.outputResistance = outputResistance;
		this.inputSide = inputSide;
		this.outputSide = outputSide;
		
		//Send change to all crafter
    	Iterator<ICrafting> iterator = this.crafters.iterator();
    	while (iterator.hasNext()) {
    		ICrafting crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, ratio, outputResistance, inputSide, outputSide);
    		}
    	}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataArrivedFromServer(Object[] data) {
		this.ratio = (Double) data[0];
		this.outputResistance = (Double) data[1];
		this.inputSide = (ForgeDirection) data[2];
		this.outputSide = (ForgeDirection) data[3];
	}
	
	@Override
	public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
		double ratio = tileEntity.ratio, outputResistance = tileEntity.outputResistance;
		
		switch(buttonID){
		case 6:
			if (isCtrlPressed)
				outputResistance -= 10;
			else
				outputResistance -= 1;
			break;
		case 7:
			outputResistance -= 0.1;
			break;
		case 8:
			if (isCtrlPressed)
				outputResistance -= 0.001;
			else
				outputResistance -= 0.01;
			break;
		case 9:
			if (isCtrlPressed)
				outputResistance += 0.001;
			else
				outputResistance += 0.01;
			break;
		case 10:
			outputResistance += 0.1;
			break;
		case 11:
			if (isCtrlPressed)
				outputResistance += 10;
			else
				outputResistance += 1;
			break;
			
			
			
		case 0:
			if (isCtrlPressed)
				ratio -= 10;
			else
				ratio -= 1;
			break;
		case 1:
			ratio -= 0.1;
			break;
		case 2:
			if (isCtrlPressed)
				ratio -= 0.001;
			else
				ratio -= 0.01;
			break;
		case 3:
			if (isCtrlPressed)
				ratio += 0.001;
			else
				ratio += 0.01;
			break;
		case 4:
			outputResistance += 0.1;
			break;
		case 5:
			if (isCtrlPressed)
				ratio += 10;
			else
				ratio += 1;
			break;
		}
		
        if (outputResistance < 0.001)
        	outputResistance = 0.001;
        if (outputResistance > 100)
        	outputResistance = 100;


        if (ratio < 0.001)
            ratio = 0.001;
        if (ratio > 100)
            ratio = 100;
		
		tileEntity.ratio = ratio;
		tileEntity.outputResistance = outputResistance;
		
		SEAPI.energyNetAgent.updateTileParameter(tileEntity);
	}

	@Override
	public void onDirectionSelected(ForgeDirection direction, int mouseButton) {
		ForgeDirection inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		
		System.out.println(direction);
		
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
