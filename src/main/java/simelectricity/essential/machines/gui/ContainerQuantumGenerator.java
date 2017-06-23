package simelectricity.essential.machines.gui;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventory;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerQuantumGenerator extends ContainerNoInventory<TileQuantumGenerator> implements ISEContainerUpdate, ISEButtonEventHandler{    
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
		internalVoltage = (Double) data[0];
		resistance = (Double) data[1];
		voltage = (Double) data[2];
		current = (Double) data[3];
		
		outputPower = current * voltage;
	}

	@Override
	public void detectAndSendChanges() {
		double internalVoltage = this.tileEntity.internalVoltage;
		double resistance = this.tileEntity.resistance;
		
		double voltage = this.tileEntity.voltage;
		double current = this.tileEntity.current;
		
		//Look for any changes
		if (this.internalVoltage == internalVoltage	&&
			this.resistance == resistance &&
			this.voltage == voltage &&
			this.current == current)
			return;
		
		this.voltage = voltage;
		this.internalVoltage = voltage;
		this.resistance = resistance;
		this.current = current;
		
		//Send change to all crafter
    	Iterator<ICrafting> iterator = this.crafters.iterator();
    	while (iterator.hasNext()) {
    		ICrafting crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, internalVoltage, resistance, voltage, current);
    		}
    	}
	}

	@Override
	public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
		double resistance = this.tileEntity.resistance;
		double internalVoltage = this.tileEntity.internalVoltage;
		
		switch (buttonID){
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
	    
	    this.tileEntity.resistance = resistance;
	    this.tileEntity.internalVoltage = internalVoltage;
	    
	    SEAPI.energyNetAgent.updateTileParameter(tileEntity);
	}
}
