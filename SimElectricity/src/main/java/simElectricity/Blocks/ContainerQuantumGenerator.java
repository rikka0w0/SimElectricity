package simElectricity.Blocks;

import simElectricity.API.Util;
import simElectricity.API.Common.ContainerBase;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerQuantumGenerator extends ContainerBase{
	protected TileQuantumGenerator tileEntity;
	public ContainerQuantumGenerator (InventoryPlayer inventoryPlayer, TileQuantumGenerator te){
        tileEntity = te;
        
        if(!te.getWorldObj().isRemote){
        	Util.updateTileEntityField(te, "outputVoltage");
        	Util.updateTileEntityField(te, "outputResistance");
        }
        
        bindPlayerInventory(inventoryPlayer);
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
