package simElectricity.Test;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import simElectricity.API.Common.ContainerBase;

public class ContainerBatteryBox extends ContainerBase{
	public ContainerBatteryBox(InventoryPlayer inventoryPlayer, TileEntity te) {super(inventoryPlayer, te);}
	protected int progress,energy;

    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting){
        super.addCraftingToCrafters(par1iCrafting);
      	par1iCrafting.sendProgressBarUpdate(this, 0, ((TileBatteryBox)tileEntity).progress);   
      	par1iCrafting.sendProgressBarUpdate(this, 1, ((TileBatteryBox)tileEntity).energy);        	
    }
    
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2){
    	if (par1 == 0)	((TileBatteryBox)tileEntity).progress = par2;
    	if (par1 == 1)	((TileBatteryBox)tileEntity).energy = par2;    	
   	}
    
    @Override
    public void detectAndSendChanges(){
    	super.detectAndSendChanges();
    	Iterator var1 = this.crafters.iterator();
    	while (var1.hasNext())
    	{
    		ICrafting var2 = (ICrafting)var1.next();
            var2.sendProgressBarUpdate(this, 0, progress);       
            var2.sendProgressBarUpdate(this, 1, energy); 
    	}
    	
    	progress=((TileBatteryBox)tileEntity).progress;
    	energy=((TileBatteryBox)tileEntity).energy;
    }
	
    @Override
    public int getPlayerInventoryStartIndex(){return 2;}
    @Override
    public int getPlayerInventoryEndIndex(){return 38;}
    @Override   
    public int getTileInventoryStartIndex(){return 0;}
    @Override
    public int getTileInventoryEndIndex(){return 2;}

	@Override
	public void init() {
        addSlotToContainer(new Slot((IInventory) tileEntity, 0, 43, 33));
        addSlotToContainer(new Slot((IInventory) tileEntity, 1, 103, 34));
	}

}
