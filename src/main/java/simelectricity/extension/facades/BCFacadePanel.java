package simelectricity.extension.facades;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public class BCFacadePanel implements ISECoverPanel{
	private final boolean isHollow;
	private final BlockState blockState;
	private final ItemStack itemStack;
	
	public BCFacadePanel(CompoundNBT nbt) {
		this.isHollow = nbt.getBoolean("isHollow");
			
		this.blockState = NBTUtil.readBlockState(nbt.getCompound("blockstate"));
		
		CompoundNBT nbtItemStack = nbt.getCompound("itemstack");
		this.itemStack = ItemStack.read(nbtItemStack);
		if (itemStack != null)
			itemStack.setCount(1);
	}
	
	public BCFacadePanel(boolean isHollow, BlockState blockState, ItemStack itemStack){
		this.isHollow = isHollow;
		this.blockState = blockState;
		
		if (itemStack == null){
			this.itemStack = ItemStack.EMPTY;
		}else{
			this.itemStack = itemStack.copy();
			this.itemStack.setCount(1);
		}
			
	}
	
	public BlockState getBlockState() {return blockState;}
	
	@Override
	public boolean isHollow() {return isHollow;}

	@Override
	public void toNBT(CompoundNBT nbt) {
		nbt.putString("coverPanelType", "BCFacade");
		nbt.putBoolean("isHollow", isHollow);
		nbt.put("blockstate", NBTUtil.writeBlockState(blockState));
		
		CompoundNBT nbtItemStack = new CompoundNBT();
		if (itemStack != null) {
			itemStack.write(nbtItemStack);
			nbt.put("itemstack", nbtItemStack);
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return BCFacadeRender.instance;
	}

	@Override
	public void setHost(TileEntity hostTileEntity, Direction side) {}

	@Override
	public ItemStack getDroppedItemStack() {
		return itemStack.copy();
	}
}
