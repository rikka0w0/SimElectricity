package simElectricity.Blocks;

import simElectricity.API.Energy;
import simElectricity.API.Util;
import simElectricity.API.Common.ContainerBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerVoltageMeter extends ContainerBase{
    public ContainerVoltageMeter(InventoryPlayer inventoryPlayer, TileEntity te) {super(inventoryPlayer, te);}

	@Override
    public void detectAndSendChanges()
    {
    	super.detectAndSendChanges();
    	((TileVoltageMeter)tileEntity).voltage=Energy.getVoltage(((TileVoltageMeter)tileEntity));
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
