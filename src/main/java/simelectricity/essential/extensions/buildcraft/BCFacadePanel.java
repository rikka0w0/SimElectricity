package simelectricity.essential.extensions.buildcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.extensions.buildcraft.client.BCFacadeRender;

public class BCFacadePanel implements ISECoverPanel{
	private final boolean isHollow;
	private final int meta;
	private final Block block;
	private final ItemStack itemStack;
	
	public BCFacadePanel(NBTTagCompound nbt) {
		this.isHollow = nbt.getBoolean("isHollow");
		this.meta = nbt.getByte("meta");
		
		int blockID = nbt.getInteger("blockID");
		this.block = Block.getBlockById(blockID);
		
		this.itemStack = ItemStack.loadItemStackFromNBT(nbt);
		if (itemStack != null)
			itemStack.stackSize = 1;
	}
	
	public BCFacadePanel(boolean isHollow, int meta, Block block, ItemStack itemStack){
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
	
	public int getBlockMeta() {return meta;}
	
	public Block getBlock() {return block;}
	
	public ItemStack getItem() {return itemStack.copy();}
	
	@Override
	public boolean isHollow() {return isHollow;}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "BCFacade");
		
		nbt.setBoolean("isHollow", isHollow);
		nbt.setInteger("meta", meta);
		nbt.setInteger("blockID", Block.getIdFromBlock(block));
				
		if (itemStack != null)
			itemStack.writeToNBT(nbt);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return BCFacadeRender.instance;
	}

	@Override
	public void setHost(TileEntity hostTileEntity, ForgeDirection side) {}
}
