package simelectricity.essential.grid;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;

public class BlockPowerPoleCollisionBox extends SEBlock implements ISESimpleTextureItem{
	public BlockPowerPoleCollisionBox() {
		super("essential_powerpole_collision_box", Material.ROCK, SEItemBlock.class);
		this.setBlockUnbreakable();
	}
    
	@Override
	@SideOnly(Side.CLIENT)
	public String getIconName(int damage) {
		return "essential_powerpole_0";	//There's no way to obtain this block, so just return a existing texture
	}
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	public final static IProperty<Integer> propertyPart = PropertyInteger.create("part", 0 , 10);
	@Override
	protected final BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {propertyPart});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){
        return super.getDefaultState().withProperty(propertyPart, meta);
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		return state.getValue(propertyPart);
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		int part = state.getValue(propertyPart);
		
		if (part == 0)
			return new AxisAlignedBB(0, 0, 0, 1, 0.05, 1);
		else if (part == 1)
			return new AxisAlignedBB(0, 0, 0.5, 1, 0.05, 1);
		else if (part == 2)
			return new AxisAlignedBB(0.5, 0, 0, 1, 0.05, 1);
		else if (part == 3)
			return new AxisAlignedBB(0, 0, 0, 1, 0.05, 0.5);
		else if (part == 4)
			return new AxisAlignedBB(0, 0, 0, 0.5, 0.05, 1);
		else if (part == 5)
			return new AxisAlignedBB(0, 0, 0.5, 0.5, 0.05, 1);
		else if (part == 6)
			return new AxisAlignedBB(0.5, 0, 0.5, 1, 0.05, 1);
		else if (part == 7)
			return new AxisAlignedBB(0.5, 0, 0, 1, 0.05, 0.5);
		else if (part == 8)
			return new AxisAlignedBB(0, 0, 0, 0.5, 0.05, 0.5);
		
		
		
		else if (part == 9)
			return new AxisAlignedBB(0, 0, 0.125F, 1, 0.25F, 0.875F);
		else if (part == 10)
			return new AxisAlignedBB(0.125F, 0, 0, 0.875F, 0.25F, 1);
		
		return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return ItemStack.EMPTY; //Player can not get this block anyway!
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY; //Player can not get this block anyway!
	}
	
	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
