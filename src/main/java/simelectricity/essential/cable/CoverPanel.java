package simelectricity.essential.cable;

import simelectricity.essential.api.ISECoverPanel;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CoverPanel implements ISECoverPanel{
	public static final double thickness = 0.05;	//Constant
	
	private boolean isBCFacade;
	private boolean isHollow;
	private int meta;
	private Block block;
	private ItemStack itemStack;
	
	public CoverPanel(boolean isBCFacade, boolean isHollow, int meta, Block block, ItemStack itemStack){
		this.isBCFacade = isBCFacade;
		this.isHollow = isHollow;
		this.meta = meta;
		this.block = block;
		
		if (itemStack == null){
			this.itemStack = null;
		}else{
			itemStack.stackSize = 1;
			this.itemStack = itemStack;
		}
			
	}
	
	@Override
	public boolean isBuildCraftFacade() {return isBCFacade;}

	@Override
	public boolean isHollow() {return isHollow;}

	@Override
	public int getBlockMeta() {return meta;}

	@Override
	public Block getBlock() {return block;}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setBoolean("isBCFacade", isBCFacade);
		nbt.setBoolean("isHollow", isHollow);
		nbt.setInteger("meta", meta);
		nbt.setInteger("blockID", Block.getIdFromBlock(block));
		
		if (itemStack != null)
			itemStack.writeToNBT(nbt);
	}

	@Override
	public ItemStack getCoverPanelItem() {
		return itemStack;
	}
}
