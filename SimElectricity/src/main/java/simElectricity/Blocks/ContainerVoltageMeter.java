package simElectricity.Blocks;

import simElectricity.API.Util;
import simElectricity.API.Common.ContainerBase;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerVoltageMeter extends ContainerBase{
	protected TileVoltageMeter tileEntity;
	public ContainerVoltageMeter (InventoryPlayer inventoryPlayer, TileVoltageMeter te){
        tileEntity = te;
                
        bindPlayerInventory(inventoryPlayer);
	}
    
    @Override
    public void detectAndSendChanges()
    {
    	super.detectAndSendChanges();
    	tileEntity.voltage=Util.getVoltage(tileEntity);
    	Util.updateTileEntityField(tileEntity, "voltage");
    }
	
    public int getPlayerInventoryStartIndex(){
    	return 27;
    }
    public int getPlayerInventoryEndIndex(){
    	return 36;
    }
    public int getTileInventoryStartIndex(){
    	return 0;
    }
    public int getTileInventoryEndIndex(){
    	return 27;
    } 
}
