package simElectricity.Blocks;

import java.util.Iterator;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.API.Common.ContainerBase;

public class ContainerSimpleGenerator extends ContainerBase {
	public ContainerSimpleGenerator(InventoryPlayer inventoryPlayer,TileEntity te) {super(inventoryPlayer, te);}
	protected int progress,outputVoltage,outputResistance;
    
    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting){
        super.addCraftingToCrafters(par1iCrafting);
      	par1iCrafting.sendProgressBarUpdate(this, 0, ((TileSimpleGenerator)tileEntity).progress);   
      	par1iCrafting.sendProgressBarUpdate(this, 1, (int) ((TileSimpleGenerator)tileEntity).outputVoltage);
      	par1iCrafting.sendProgressBarUpdate(this, 2, (int) ((TileSimpleGenerator)tileEntity).outputResistance);
    }
    
    
    @Override
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2){
    	if (par1 == 0)	((TileSimpleGenerator)tileEntity).progress = par2;
    	if (par1 == 1)	((TileSimpleGenerator)tileEntity).outputVoltage = par2;    	
    	if (par1 == 2)	((TileSimpleGenerator)tileEntity).outputResistance = par2;    	
   	}
    
    @Override
    public void detectAndSendChanges(){
    	super.detectAndSendChanges();
    	Iterator var1 = this.crafters.iterator();
    	while (var1.hasNext())
    	{
    		ICrafting var2 = (ICrafting)var1.next();
            var2.sendProgressBarUpdate(this, 0, progress);  
            var2.sendProgressBarUpdate(this, 1, outputVoltage);  
            var2.sendProgressBarUpdate(this, 2, outputResistance);  
    	}
    	
    	progress=((TileSimpleGenerator)tileEntity).progress;
    	outputVoltage=(int) ((TileSimpleGenerator)tileEntity).outputVoltage;
    	outputResistance=(int) ((TileSimpleGenerator)tileEntity).outputResistance;    	
    }
	
	@Override
	public int getPlayerInventoryStartIndex(){
    	return 1;
    }
    @Override
	public int getPlayerInventoryEndIndex(){
    	return 37;
    }
    @Override
	public int getTileInventoryStartIndex(){
    	return 0;
    }
    @Override
	public int getTileInventoryEndIndex(){
    	return 1;
    }

	@Override
	public void init() {
		addSlotToContainer(new Slot((IInventory) tileEntity, 0, 51, 33));
	}
}
