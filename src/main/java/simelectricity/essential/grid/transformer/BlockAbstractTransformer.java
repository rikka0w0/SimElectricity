package simelectricity.essential.grid.transformer;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockStructure.Result;

public abstract class BlockAbstractTransformer extends BlockBase {
    public BlockAbstractTransformer(String unlocalizedName, Material material, ItemGroup group) {
		super(unlocalizedName, 
				Block.Properties.create(material).hardnessAndResistance(3.0F, 10.0F).sound(SoundType.METAL), 
				ItemBlockBase.class,
				(new Item.Properties()).group(group));
	}
    
    protected abstract MultiBlockStructure getBlueprint();
    
    protected abstract ItemStack getItemToDrop(BlockState state);

	///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
	
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
//    @Override
//    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
//    	builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.INVERTED);
//    }

    ///////////////////////////////
    /// Block activities
    ///////////////////////////////
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isRemote)
            return;

        Result ret = this.getBlueprint().attempToBuild(world, pos);
        if (ret != null) {
            ret.createStructure();
        }
        return;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
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
    public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
    	return getItemToDrop(state);
    }
}
