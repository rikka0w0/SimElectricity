package simelectricity.essential.machines.gui;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileCurrentSensor;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerCurrentSensor extends ContainerNoInventoryTwoPort<TileCurrentSensor> implements ISEContainerUpdate, ISEButtonEventHandler{
	public double thresholdCurrent, resistance;
	public ForgeDirection inputSide, outputSide;
	public boolean absoluteMode, inverted;
	
	public double current;
	public boolean emitRedstoneSignal;
	
	public ContainerCurrentSensor(TileEntity tileEntity) {
		super(tileEntity);
	}

	@Override
	public void detectAndSendChanges() {
		double thresholdCurrent = tileEntity.thresholdCurrent, resistance = tileEntity.resistance;
		ForgeDirection inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		boolean absoluteMode = tileEntity.absoluteMode, inverted = tileEntity.inverted;
		double current = tileEntity.current;
		boolean emitRedstoneSignal = tileEntity.emitRedstoneSignal;
		
		//Look for any changes
		if (this.thresholdCurrent == thresholdCurrent &&
			this.resistance == resistance &&
			this.inputSide == inputSide &&
			this.outputSide == outputSide &&
			this.absoluteMode == absoluteMode &&
			this.inverted == inverted &&
			this.current == current && 
			this.emitRedstoneSignal == emitRedstoneSignal)
			return;

		this.thresholdCurrent = thresholdCurrent;
		this.resistance = resistance;
		this.inputSide = inputSide;
		this.outputSide = outputSide;
		this.absoluteMode = absoluteMode;
		this.inverted = inverted;
		this.current = current;
		this.emitRedstoneSignal = emitRedstoneSignal;
		
		//Send change to all crafter
    	Iterator<ICrafting> iterator = this.crafters.iterator();
    	while (iterator.hasNext()) {
    		ICrafting crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, thresholdCurrent, resistance, inputSide, outputSide, absoluteMode, inverted, current, emitRedstoneSignal);
    		}
    	}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataArrivedFromServer(Object[] data) {
		this.thresholdCurrent = (Double) data[0];
		this.resistance = (Double) data[1];
		this.inputSide = (ForgeDirection) data[2];
		this.outputSide = (ForgeDirection) data[3];
		this.absoluteMode = (Boolean) data[4];
		this.inverted = (Boolean) data[5];
		this.current = (Double) data[6];
		this.emitRedstoneSignal = (Boolean) data[7];
	}
	
	@Override
	public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
		double resistance = this.tileEntity.resistance;
		double thresholdCurrent = this.tileEntity.thresholdCurrent;
				
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
		
	    if (tileEntity.resistance != resistance){
	    	tileEntity.resistance = resistance;
	    	SEAPI.energyNetAgent.updateTileParameter(this.tileEntity);
	    }
	    
	    if (this.tileEntity.thresholdCurrent != thresholdCurrent){
	    	this.tileEntity.thresholdCurrent = thresholdCurrent;
	    	tileEntity.checkRedstoneStatus();
	    }
	}
}
