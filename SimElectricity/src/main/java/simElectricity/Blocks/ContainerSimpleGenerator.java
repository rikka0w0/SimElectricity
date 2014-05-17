package simElectricity.Blocks;

import java.util.Iterator;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.API.Common.ContainerBase;

public class ContainerSimpleGenerator extends ContainerBase {
	protected TileSimpleGenerator tileEntity;
	protected int progress;
	
	public ContainerSimpleGenerator (InventoryPlayer inventoryPlayer, TileSimpleGenerator te){
        tileEntity = te;
        
        addSlotToContainer(new Slot(tileEntity, 0, 43, 33));
        
        bindPlayerInventory(inventoryPlayer);
	}
    
    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting){
        super.addCraftingToCrafters(par1iCrafting);
      	par1iCrafting.sendProgressBarUpdate(this, 0, tileEntity.progress);         	
    }
    
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2){
    	if (par1 == 0)	tileEntity.progress = par2;
   	}
    
    @Override
    public void detectAndSendChanges(){
    	super.detectAndSendChanges();
    	Iterator var1 = this.crafters.iterator();
    	while (var1.hasNext())
    	{
    		ICrafting var2 = (ICrafting)var1.next();
            var2.sendProgressBarUpdate(this, 0, progress);    
    	}
    	
    	progress=tileEntity.progress;
    }
	
	public int getPlayerInventoryStartIndex(){
    	return 1;
    }
    public int getPlayerInventoryEndIndex(){
    	return 37;
    }
    public int getTileInventoryStartIndex(){
    	return 0;
    }
    public int getTileInventoryEndIndex(){
    	return 1;
    }
}
