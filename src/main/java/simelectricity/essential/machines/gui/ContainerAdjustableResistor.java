package simelectricity.essential.machines.gui;

import java.util.Iterator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventory;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.utils.network.ISEButtonEventHandler;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerAdjustableResistor extends ContainerNoInventory<TileAdjustableResistor> implements ISEContainerUpdate, ISEButtonEventHandler{
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
		resistance = (Double) data[0];
		voltage = (Double) data[1];
		current = (Double) data[2];
		powerLevel = (Double) data[3];
		bufferedEnergy = (Double) data[4];
	}
	
	@Override
	public void detectAndSendChanges() {
		double voltage = this.tileEntity.voltage;
		double resistance = this.tileEntity.resistance;
		double current = this.tileEntity.current;
		double powerLevel = this.tileEntity.powerLevel;
		double bufferedEnergy = this.tileEntity.bufferedEnergy;
		
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
    	Iterator<IContainerListener> iterator = this.listeners.iterator();
    	while (iterator.hasNext()) {
    		IContainerListener crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, resistance, voltage, current, powerLevel, bufferedEnergy);
    		}
    	}
	}
	
	@Override
	public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
		double resistance = this.tileEntity.resistance;
		
		switch (buttonID){
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
			this.tileEntity.bufferedEnergy = 0;
			return;
		}
		
        if (resistance < 0.1)
            resistance = 0.1;
        if (resistance > 10000)
            resistance = 10000;
		
	    this.tileEntity.resistance = resistance;
	    
	    SEAPI.energyNetAgent.updateTileParameter(tileEntity);
	}
}
