package simElectricity.Blocks;

import java.util.Iterator;

import simElectricity.API.Common.ContainerBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerElectricFurnace extends ContainerBase{
	public ContainerElectricFurnace(InventoryPlayer inventoryPlayer,TileEntity te) {super(inventoryPlayer, te);}
	protected int progress;

    
    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting){
        super.addCraftingToCrafters(par1iCrafting);
      	par1iCrafting.sendProgressBarUpdate(this, 0, ((TileElectricFurnace)tileEntity).progress);     
    }
    
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2){
    	if (par1 == 0)	((TileElectricFurnace)tileEntity).progress = par2;
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
    	
    	progress=((TileElectricFurnace)tileEntity).progress;
    }
	
    @Override
    public int getPlayerInventoryStartIndex(){return 2;}
    @Override
    public int getPlayerInventoryEndIndex(){return 38;}
    @Override   
    public int getTileInventoryStartIndex(){return 0;}
    @Override
    public int getTileInventoryEndIndex(){return 1;}

	@Override
	public void init() {
        addSlotToContainer(new Slot((IInventory) tileEntity, 0, 43, 33));
        addSlotToContainer(new Slot((IInventory) tileEntity, 1, 103, 34){public boolean isItemValid(ItemStack par1ItemStack){return false;}});
	}
}