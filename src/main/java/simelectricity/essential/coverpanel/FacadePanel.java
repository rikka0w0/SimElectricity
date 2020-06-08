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
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.client.coverpanel.GenericFacadeRender;

public abstract class FacadePanel implements ISEFacadeCoverPanel {
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
	
	@Override
	public BlockState getBlockState() {return blockState;}
	
	@Override
	public boolean isHollow() {return isHollow;}

	@Override
	public void toNBT(CompoundNBT nbt) {
		nbt.putBoolean("isHollow", isHollow());
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
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public <T extends ISECoverPanel> ISECoverPanelRender<T> getCoverPanelRender() {
		return GenericFacadeRender.instance.cast();
	}
	
	public static class FacadeNormal extends FacadePanel {
		public FacadeNormal() {
			this(Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
		}
		
		public FacadeNormal(BlockState state, ItemStack stack) {
			super(false, state, stack);
		}
		
		public FacadeNormal(CompoundNBT nbt) {
			super(nbt);
		}
	}
	
	public static class FacadeHollow extends FacadePanel {
		public FacadeHollow() {
			this(Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
		}
		
		public FacadeHollow(BlockState state, ItemStack stack) {
			super(true, state, stack);
		}
		
		public FacadeHollow(CompoundNBT nbt) {
			super(nbt);
		}
	}
}
