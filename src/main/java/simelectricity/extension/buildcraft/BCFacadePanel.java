package simelectricity.extension.buildcraft;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public class BCFacadePanel implements ISECoverPanel{
	private final boolean isHollow;
	private final IBlockState blockState;
	private final ItemStack itemStack;
	
	public BCFacadePanel(NBTTagCompound nbt) {
		this.isHollow = nbt.getBoolean("isHollow");
		
		Block block = Block.getBlockById(nbt.getInteger("blockID"));
		int meta = nbt.getByte("meta");
		
		this.blockState = block.getStateFromMeta(meta);
		
		this.itemStack = new ItemStack(nbt);
		if (itemStack != null)
			itemStack.setCount(1);
	}
	
	public BCFacadePanel(boolean isHollow, IBlockState blockState, ItemStack itemStack){
		this.isHollow = isHollow;
		this.blockState = blockState;
		
		if (itemStack == null){
			this.itemStack = ItemStack.EMPTY;
		}else{
			this.itemStack = itemStack.copy();
			this.itemStack.setCount(1);
		}
			
	}
	
	public IBlockState getBlockState() {return blockState;}
	
	public ItemStack getItemStack() {return itemStack.copy();}
	
	@Override
	public boolean isHollow() {return isHollow;}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "BCFacade");
		
		nbt.setBoolean("isHollow", isHollow);

		Block block = blockState.getBlock();
		int meta = block.getMetaFromState(blockState);
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
	public void setHost(TileEntity hostTileEntity, EnumFacing side) {}
}
