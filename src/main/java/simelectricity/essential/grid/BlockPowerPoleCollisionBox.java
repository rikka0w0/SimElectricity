package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import rikka.librikka.block.BlockBase;

public class BlockPowerPoleCollisionBox extends BlockBase {
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    public static final IntegerProperty propertyPart = IntegerProperty.create("part", 0, 10);

    public BlockPowerPoleCollisionBox() {
        super("essential_powerpole_collision_box", Block.Properties.create(Material.ROCK), new Item.Properties());
//        setBlockUnbreakable();
    }

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(propertyPart);
	}
	
	public BlockState forPart(int part) {
		return this.getDefaultState().with(propertyPart, part);
	}

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    	VoxelShape ret = VoxelShapes.empty();
		int part = state.get(BlockPowerPoleCollisionBox.propertyPart);

		if (part == 0)
			return VoxelShapes.create(0, 0, 0, 1, 0.05, 1);
		else if (part == 1)
			return VoxelShapes.create(0, 0, 0.5, 1, 0.05, 1);
		else if (part == 2)
			return VoxelShapes.create(0.5, 0, 0, 1, 0.05, 1);
		else if (part == 3)
			return VoxelShapes.create(0, 0, 0, 1, 0.05, 0.5);
		else if (part == 4)
			return VoxelShapes.create(0, 0, 0, 0.5, 0.05, 1);
		else if (part == 5)
			return VoxelShapes.create(0, 0, 0.5, 0.5, 0.05, 1);
		else if (part == 6)
			return VoxelShapes.create(0.5, 0, 0.5, 1, 0.05, 1);
		else if (part == 7)
			return VoxelShapes.create(0.5, 0, 0, 1, 0.05, 0.5);
		else if (part == 8)
			return VoxelShapes.create(0, 0, 0, 0.5, 0.05, 0.5);

		else if (part == 9)
			return VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F);
		else if (part == 10)
			return VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1);

		return VoxelShapes.create(0, 0, 0, 1, 1, 1);
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

	}

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }
}
