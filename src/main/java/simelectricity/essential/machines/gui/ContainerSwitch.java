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
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerSwitch extends ContainerNoInventoryTwoPort<TileSwitch> implements ISEContainerUpdate, ISEButtonEventHandler{
	public double resistance;
	public boolean isOn;
	public double maxCurrent;
	public double current;
	public ForgeDirection inputSide, outputSide;
	
	public ContainerSwitch(TileEntity tileEntity) {
		super(tileEntity);
	}
	
	@Override
	public void detectAndSendChanges() {
		double resistance = tileEntity.resistance;
		boolean isOn = tileEntity.isOn;
		double maxCurrent = tileEntity.maxCurrent;
		double current = tileEntity.current;
		ForgeDirection inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		
		//Look for any changes
		if (this.resistance == resistance &&
			this.isOn == isOn &&
			this.maxCurrent == maxCurrent &&
			this.current == current &&
			this.inputSide == inputSide &&
			this.outputSide == outputSide)
			return;

		this.resistance = resistance;
		this.isOn = isOn;
		this.maxCurrent = maxCurrent;
		this.current = current;
		this.inputSide = inputSide;
		this.outputSide = outputSide;
		
		//Send change to all crafter
    	Iterator<ICrafting> iterator = this.crafters.iterator();
    	while (iterator.hasNext()) {
    		ICrafting crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, resistance, isOn, maxCurrent, current, inputSide, outputSide);
    		}
    	}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataArrivedFromServer(Object[] data) {
		this.resistance = (Double) data[0];
		this.isOn = (Boolean) data[1];
		this.maxCurrent = (Double) data[2];
		this.current = (Double) data[3];
		this.inputSide = (ForgeDirection) data[4];
		this.outputSide = (ForgeDirection) data[5];
	}

	@Override
	public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
		double resistance = this.tileEntity.resistance;
		double maxCurrent = this.tileEntity.maxCurrent;
		boolean isOn = this.isOn;
		
		boolean isOnChanged = false;
		
        switch (buttonID) {
        case 0:
            if (isCtrlPressed)
                resistance -= 1;
            else
                resistance -= 0.1;
            break;
        case 1:
            if (isCtrlPressed)
                resistance -= 0.001;
            else
                resistance -= 0.01;
            break;
        case 2:
            if (isCtrlPressed)
                resistance += 0.001;
            else
                resistance += 0.01;
            break;
        case 3:
            if (isCtrlPressed)
                resistance += 1;
            else
                resistance += 0.1;
            break;

        case 4:
            if (isCtrlPressed)
                maxCurrent -= 100;
            else
                maxCurrent -= 10;
            break;
        case 5:
            if (isCtrlPressed)
                maxCurrent -= 0.1;
            else
                maxCurrent -= 1;
            break;
        case 6:
            if (isCtrlPressed)
                maxCurrent += 0.1;
            else
                maxCurrent += 1;
            break;
        case 7:
            if (isCtrlPressed)
                maxCurrent += 100;
            else
                maxCurrent += 10;
            break;
        case 8:
        	isOnChanged = true;
        	isOn = !isOn;
        	break;
        default:
        }

	    if (resistance < 0.001)
	        resistance = 0.001F;
	    if (resistance > 100)
	        resistance = 100;
	    
	    if (maxCurrent < 0.1)
	        maxCurrent = 0.1F;
	    if (maxCurrent > 1000)
	        maxCurrent = 1000;
			
	    this.tileEntity.resistance = resistance;
	    this.tileEntity.maxCurrent = maxCurrent;
	    
	    if (isOnChanged){
	    	this.tileEntity.setSwitchStatus(isOn);
	    }else{
	    	SEAPI.energyNetAgent.updateTileParameter(this.tileEntity);
	    }
	}
}
