package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.Properties;
import rikka.librikka.properties.UnlistedPropertyRef;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.common.ISEFacing8;
import simelectricity.essential.grid.TilePowerPole3;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;

public class BlockPowerPole3 extends BlockBase implements ISubBlock, ISEHVCableConnector {
    public BlockPowerPole3() {
        super("essential_powerpole3", Material.ROCK, ItemBlockBase.class);

		setDefaultState(blockState.getBaseState());
		
		setCreativeTab(SEAPI.SETab);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public String[] getSubBlockUnlocalizedNames() {
        return EnumBlockTypePole3.names;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos.down());

        //return flg = blockState.getBlock() == this || blockState.isSideSolid(world, pos.down(), EnumFacing.UP);
        return true;
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        EnumBlockTypePole3 blockType = state.getValue(EnumBlockTypePole3.property);

        switch (blockType) {
            case Pole:
                return null;
            case Crossarm10kVT0:
                return new TilePowerPole3.Pole10Kv.Type0();
            case Crossarm10kVT1:
                return new TilePowerPole3.Pole10Kv.Type1();
            case Crossarm415VT0:
                return new TilePowerPole3.Pole415vType0();
            case Branching10kV:
            	return new TilePoleBranch.Type10kV();
            case Branching415V:
            	return new TilePoleBranch.Type415V();
            default:
            	break;
        }
        return null;
    }
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected final BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{EnumBlockTypePole3.property, Properties.facing3bit},
                new IUnlistedProperty[]{UnlistedPropertyRef.propertyTile});
    }

    @Override
    public final IBlockState getStateFromMeta(int meta) {
        meta &= 15;
        return getDefaultState().withProperty(EnumBlockTypePole3.property, EnumBlockTypePole3.fromInt(meta));
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        return state.getValue(EnumBlockTypePole3.property).index;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumBlockTypePole3 blockType = state.getValue(EnumBlockTypePole3.property);
        if (!blockType.ignoreFacing) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof ISEFacing8) {
                state = state.withProperty(Properties.facing3bit, ((ISEFacing8) te).getRotation());
            }
        }
        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState retval = (IExtendedBlockState) state;

            TileEntity te = world.getTileEntity(pos);

            if (te instanceof ISEPowerPole)
                retval = retval.withProperty(UnlistedPropertyRef.propertyTile, new WeakReference<>(te));

            return retval;
        }
        return state;
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(EnumBlockTypePole3.property).index;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        EnumBlockTypePole3 blockType = state.getValue(EnumBlockTypePole3.property);
        if (!blockType.ignoreFacing) {
            TileEntity te = world.getTileEntity(pos);

            if (te instanceof ISEFacing8) {
            	((ISEFacing8)te).setFacingOnPlacement(8 - MathHelper.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D) & 7);

                if (!world.isRemote) {
                    //TODO: CHECK!
                    SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, blockType.numOfConductor));

                    world.notifyBlockUpdate(pos, state, state, 2);
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
        if (te instanceof ISEGridTile) {
        	ISEGridNode node = ((ISEGridTile) te).getGridNode();
        	if (node != null)
                SEAPI.energyNetAgent.detachGridNode(world, node);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) { //Server only!
            if (!canPlaceBlockAt(worldIn, pos)) {
				dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
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
		Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));
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

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
}
