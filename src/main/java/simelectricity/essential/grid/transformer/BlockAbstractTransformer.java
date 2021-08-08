package simelectricity.essential.grid.transformer;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockStructure.Result;

import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class BlockAbstractTransformer extends BlockBase {
    public BlockAbstractTransformer(String unlocalizedName, Material material, CreativeModeTab group) {
		super(unlocalizedName,
				BlockBehaviour.Properties.of(material).strength(3.0F, 10.0F).sound(SoundType.METAL).isRedstoneConductor((a,b,c)->false),
				ItemBlockBase.class,
				(new Item.Properties()).tab(group));
	}

    protected abstract MultiBlockStructure getBlueprint();

    protected abstract ItemStack getItemToDrop(BlockState state);

    ///////////////////////////////
    /// Block activities
    ///////////////////////////////
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isClientSide)
            return;

        Result ret = this.getBlueprint().attempToBuild(world, pos);
        if (ret != null) {
            ret.createStructure();
        }
        return;
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && !world.isClientSide) {
            this.getBlueprint().restoreStructure(te, state, true);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    	List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

    	ItemStack itemStack = getItemToDrop(state);
    	if (itemStack.getItem() != Items.AIR)
    		ret.add(itemStack);

        return ret;
    }

    /**
     * Creative-mode middle mouse button clicks
     */
    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
    	return getItemToDrop(state);
    }
}
