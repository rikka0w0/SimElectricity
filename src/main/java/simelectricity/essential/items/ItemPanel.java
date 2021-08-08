package simelectricity.essential.items;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.IMetaProvider;
import rikka.librikka.IMetaBase;
import rikka.librikka.item.ItemBase;
import simelectricity.api.SEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.coverpanel.LedPanel;
import simelectricity.essential.coverpanel.VoltageSensorPanel;

public final class ItemPanel extends ItemBase implements IMetaProvider<IMetaBase> {
    public static enum ItemType implements IMetaBase {
    	ledpanel(LedPanel::new),
    	voltagesensor(VoltageSensorPanel::new),
    	facade(null),
    	facade_hollow(null);

    	public final Supplier<ISECoverPanel> constructor;
    	ItemType(Supplier<ISECoverPanel> constructor) {
    		this.constructor = constructor;
    	}
    }

    public final ItemType itemType;
    private ItemPanel(ItemType itemType) {
        super("item_" + itemType.name(), (new Item.Properties())
        		.tab(SEAPI.SETab));
        this.itemType = itemType;
    }

    @Override
	public IMetaBase meta() {
		return itemType;
	}

    public static ItemPanel[] create() {
    	ItemPanel[] ret = new ItemPanel[ItemType.values().length];
    	for (ItemType meta: ItemType.values()) {
    		ret[meta.ordinal()] = new ItemPanel(meta);
    	}
    	return ret;
    }

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (this.itemType != ItemType.facade && this.itemType != ItemType.facade_hollow)
			return super.useOn(context);

		if (!context.getPlayer().isCrouching())
			return InteractionResult.FAIL;

		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState blockstate = world.getBlockState(pos);
		VoxelShape shape = blockstate.getShape(world, pos);

		for (Direction side: Direction.values()) {
			if (!Block.isFaceFull(shape, side))
				return super.useOn(context);
		}

		CompoundTag bsNBT = NbtUtils.writeBlockState(blockstate);
		context.getItemInHand().getOrCreateTag().put("facade_blockstate", bsNBT);

		return InteractionResult.SUCCESS;
	}

	@SuppressWarnings("deprecation")
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
		if (this.itemType != ItemType.facade && this.itemType != ItemType.facade_hollow || !stack.hasTag())
			return;

		BlockState blockstate = NbtUtils.readBlockState(stack.getTag().getCompound("facade_blockstate"));
		if (!blockstate.isAir())
			tooltip.add(blockstate.getBlock().getName());
		if (flagIn.isAdvanced())
			tooltip.add(new TextComponent(blockstate.toString()));
	}
}
