package simElectricity.Blocks;

import simElectricity.API.Util;
import simElectricity.API.Common.ContainerBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerQuantumGenerator extends ContainerBase{
    public ContainerQuantumGenerator(InventoryPlayer inventoryPlayer,TileEntity te) {super(inventoryPlayer, te);}
    
	@Override
	public int getPlayerInventoryStartIndex(){
    	return 27;
    }
    @Override
	public int getPlayerInventoryEndIndex(){
    	return 36;
    }
    @Override
	public int getTileInventoryStartIndex(){
    	return 0;
    }
    @Override
	public int getTileInventoryEndIndex(){
    	return 27;
    }

	@Override
	public void init() {
        if(!tileEntity.getWorldObj().isRemote){
        	Util.updateTileEntityField(tileEntity, "outputVoltage");
        	Util.updateTileEntityField(tileEntity, "outputResistance");
        }
	} 
}
