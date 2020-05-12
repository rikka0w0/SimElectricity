package simelectricity.essential.coverpanel;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.extension.facades.BCFacadeRender;

public abstract class FacadePanel implements ISECoverPanel{
	private final boolean isHollow;
	public final BlockState blockState;
	private final ItemStack itemStack;
	
	protected FacadePanel(CompoundNBT nbt) {
		this(
				nbt.getBoolean("isHollow"),
				NBTUtil.readBlockState(nbt.getCompound("blockstate")),
				ItemStack.read(nbt.getCompound("itemstack"))
			);
	}
	
	protected FacadePanel(boolean isHollow, BlockState blockState, ItemStack itemStack){
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
		nbt.putBoolean("isHollow", isHollow);
		nbt.put("blockstate", NBTUtil.writeBlockState(blockState));
		
		CompoundNBT nbtItemStack = new CompoundNBT();
		if (itemStack != null) {
			itemStack.write(nbtItemStack);
		} else {
			ItemStack.EMPTY.write(nbtItemStack);
		}
		nbt.put("itemstack", nbtItemStack);
	}
	
	@Override
	public void setHost(TileEntity hostTileEntity, Direction side) {}

	@Override
	public ItemStack getDroppedItemStack() {
		return itemStack.copy();
	}
	
	public static class FacadeNormal extends FacadePanel {
		public FacadeNormal() {
			super(false, Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
		}
		
		public FacadeNormal(CompoundNBT nbt) {
			super(nbt);
		}
		
		@Override
		public boolean isHollow() {
			return false;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public ISECoverPanelRender getCoverPanelRender() {
			return BCFacadeRender.instance;
		}
	}
	
	public static class FacadeHollow extends FacadePanel {
		public FacadeHollow() {
			super(false, Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
		}
		
		public FacadeHollow(CompoundNBT nbt) {
			super(nbt);
		}

		@Override
		public boolean isHollow() {
			return true;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public ISECoverPanelRender getCoverPanelRender() {
			return BCFacadeRender.instance;
		}
	}
}
