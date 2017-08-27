package simelectricity.essential.grid;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.common.UnlistedNonNullProperty;

public class BlockPowerPole3 extends SEBlock implements ITileEntityProvider, ISESubBlock, ISEHVCableConnector{	
	public BlockPowerPole3() {
		super("essential_powerpole3", Material.ROCK, ItemBlock.class);
		
		this.setDefaultState(this.blockState.getBaseState());
	}
	
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return EnumBlockTypePole3.names;
	}
	
	public static class ItemBlock extends SEItemBlock {
		public ItemBlock(Block block) {super(block);}
	}
	
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos.down());
		
		return blockState.getBlock() == this || blockState.isSideSolid(world, pos.down(), EnumFacing.UP);
	}
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected final BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, 
				new IProperty[] {EnumBlockTypePole3.property, Properties.propertyFacing},
				new IUnlistedProperty[] {UnlistedNonNullProperty.propertyGridTile});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){
		meta &= 15;
        return super.getDefaultState().withProperty(EnumBlockTypePole3.property, EnumBlockTypePole3.fromInt(meta));
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		return state.getValue(EnumBlockTypePole3.property).index;
    }
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumBlockTypePole3 blockType = state.getValue(EnumBlockTypePole3.property);
		if (!blockType.ignoreFacing) {
			TileEntity te = world.getTileEntity(pos);
			if (te != null) {
				state = state.withProperty(Properties.propertyFacing, ((TilePowerPole3)te).facing);
			}
		}
		return state;
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState retval = (IExtendedBlockState)state;
			
			TileEntity te = world.getTileEntity(pos);
			
			if (te instanceof ISEGridTile) {
				retval = retval.withProperty(UnlistedNonNullProperty.propertyGridTile, new WeakReference<>((ISEGridTile) te));
			}
			
			return retval;
		}
		return state;
	}
	
	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////	
	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int damage, EntityLivingBase placer) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, damage, placer);
		return state;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return ItemStack.EMPTY;
	}
	
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);       
    	
    	EnumBlockTypePole3 blockType = state.getValue(EnumBlockTypePole3.property);
		if (!blockType.ignoreFacing) {
			TileEntity te = world.getTileEntity(pos);
			
			if (te instanceof TilePowerPole3) {
				TilePowerPole3 pole = (TilePowerPole3) te;
				pole.facing = 8 - MathHelper.floor((placer.rotationYaw) * 8.0F / 360.0F + 0.5D) & 7;
				

				
				if (!world.isRemote) {
					//TODO: CHECK!
					SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, 3));

					world.notifyBlockUpdate(pos, state, state, 2);
				}
			}
		}
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
    	TileEntity te = world.getTileEntity(pos);	//Do this before the tileEntity is removed!
    	if (te instanceof ISEGridTile)
    		SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());
    	
    	super.breakBlock(world, pos, state);
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) { //Server only!
            if (!this.canPlaceBlockAt(worldIn, pos)) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }
    
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		EnumBlockTypePole3 blockType = EnumBlockTypePole3.fromInt(meta);
		
		switch (blockType) {
		case Pole:
			return null;
		case Crossarm10KvT0:
			return new TilePowerPole3.Pole10KvType0();
		case Crossarm10KvT1:
			return new TilePowerPole3.Pole10KvType1();
		}
		return null;
	}
	
	///////////////////
	/// BoundingBox
	///////////////////	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean magicBool) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));
	}
	
	//////////////////////////////////////
	/// ISEHVCableConnector
	//////////////////////////////////////
	@Override
	public boolean canHVCableConnect(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		
		return te instanceof TilePowerPole3;
	}

	@Override
	public ISEGridNode getNode(World world, BlockPos pos){
		TileEntity te = world.getTileEntity(pos);
		
		return (te instanceof TilePowerPole3) ? ((TilePowerPole3)te).getGridNode() : null;
	}
	
	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    	return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
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
