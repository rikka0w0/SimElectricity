package simelectricity.essential.coverpanel;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
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

	protected FacadePanel(CompoundTag nbt) {
		this(
				nbt.getBoolean("isHollow"),
				NbtUtils.readBlockState(nbt.getCompound("blockstate")),
				ItemStack.of(nbt.getCompound("itemstack"))
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
	public void toNBT(CompoundTag nbt) {
		nbt.putBoolean("isHollow", isHollow());
		nbt.put("blockstate", NbtUtils.writeBlockState(blockState));

		CompoundTag nbtItemStack = new CompoundTag();
		if (itemStack != null) {
			itemStack.save(nbtItemStack);
		} else {
			ItemStack.EMPTY.save(nbtItemStack);
		}
		nbt.put("itemstack", nbtItemStack);
	}

	@Override
	public void setHost(BlockEntity hostTileEntity, Direction side) {}

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
			this(Blocks.AIR.defaultBlockState(), ItemStack.EMPTY);
		}

		public FacadeNormal(BlockState state, ItemStack stack) {
			super(false, state, stack);
		}

		public FacadeNormal(CompoundTag nbt) {
			super(nbt);
		}
	}

	public static class FacadeHollow extends FacadePanel {
		public FacadeHollow() {
			this(Blocks.AIR.defaultBlockState(), ItemStack.EMPTY);
		}

		public FacadeHollow(BlockState state, ItemStack stack) {
			super(true, state, stack);
		}

		public FacadeHollow(CompoundTag nbt) {
			super(nbt);
		}
	}
}
