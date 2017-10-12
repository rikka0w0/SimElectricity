package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.Properties;
import rikka.librikka.tileentity.TileEntityBase;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;

import java.util.LinkedList;

import javax.annotation.Nonnull;

public class BlockPowerPoleBottom extends BlockBase implements ISEHVCableConnector, ISimpleTexture {
    ///////////////////
    /// Utils
    ///////////////////
    private static final int[][] baseCoordOffsetMatrix90 = {
            {1, 3, 5, 7},    //facing first
            {3, 5, 7, 1},
            {5, 7, 1, 3},
            {7, 1, 3, 5}
    };
    private static final int[][] baseCoordOffsetMatrix45 = {
            {4, 6, 0, 2},
            {6, 0, 2, 4},
            {0, 2, 4, 6},
            {2, 4, 6, 0}
    };

    ///////////////////
    /// Initialize
    ///////////////////
    public BlockPowerPoleBottom() {
        super("essential_powerpole_bottom", Material.ROCK, ItemBlockBase.class);
        
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    /**
     * Internal, Do not use, Rotate around Y
     *
     * @param facing minecraft facing, S0, E3
     * @return
     */
    public static Vec3i rotateCoord(int facing, int x, int y, int z) {
        //facing coord
        int[][] rotationMatrixCoord = {
                {x, y, z},        //facing=0 S
                {z, y, -x},        //facing=1 W
                {-x, y, -z},        //facing=2 N
                {-z, y, x}        //facing=3 E
        };

        int[] coord = rotationMatrixCoord[facing];
        return new Vec3i(coord[0], coord[1], coord[2]);
    }

    /**
     * @param state blockstate which contains facing of the center block
     * @return offsets from center
     */
    public static LinkedList<BlockInfo> getBaseBlockCoordOffsets(IBlockState state) {
        LinkedList<BlockInfo> list = new LinkedList();

        int facing = state.getValue(Properties.facing3bit);
        if ((facing & 1) == 0) {    // 90 x n
            facing = facing >> 1;
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, -2, 0, -2), BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][0]));
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, -2, 0, 2), BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][1]));
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 2, 0, 2), BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][2]));
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 2, 0, -2), BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][3]));
        } else {
            facing = facing >> 1;
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 0, 0, 3), BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][0]));
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 3, 0, 0), BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][1]));
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 0, 0, -3), BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][2]));
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, -3, 0, 0), BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][3]));
        }
        return list;
    }

    public static Vec3i getCenterBoxOffset(int facing) {
        switch (facing) {
            case 1:
                return new Vec3i(+2, +18, +2);
            case 3:
                return new Vec3i(+2, +18, -2);
            case 7:
                return new Vec3i(-2, +18, +2);
            case 5:
                return new Vec3i(-2, +18, -2);


            case 0:
                return new Vec3i(0, +18, +3);
            case 2:
                return new Vec3i(+3, +18, 0);
            case 4:
                return new Vec3i(0, +18, -3);
            case 6:
                return new Vec3i(-3, +18, 0);
        }

        return new Vec3i(0, +18, 0);
    }

    /**
     * @param basePos    coord of the base block
     * @param state
     * @return coord of the center block
     */
    public static BlockPos getCenterBoxCoord(BlockPos basePos, IBlockState state) {
        int facing = state.getValue(Properties.facing3bit);
        Vec3i offset = BlockPowerPoleBottom.getCenterBoxOffset(facing);
        return basePos.add(offset);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getIconName(int damage) {
        return "essential_powerpole_0";    //There's no way to obtain this block, so just return a existing texture
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}
	
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new Tile();
    }
    
    public static class Tile extends TileEntityBase {
        @SideOnly(Side.CLIENT)
        @Override
        public double getMaxRenderDistanceSquared() {
            return 100000;
        }

        @SideOnly(Side.CLIENT)
        @Override
        @Nonnull
        public AxisAlignedBB getRenderBoundingBox() {
            return TileEntity.INFINITE_EXTENT_AABB;
        }
        
        @Override
        public boolean hasFastRenderer() {
            return true;
        }
    	
    	@SideOnly(Side.CLIENT)
    	public int getFacing() {
    		return this.getBlockMetadata() & 7;
    	}
    }
    
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected final BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, Properties.facing3bit);
    }

    @Override
    public final IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(Properties.facing3bit, meta & 7);
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        return state.getValue(Properties.facing3bit);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        BlockPos centerPos = BlockPowerPoleBottom.getCenterBoxCoord(pos, state);
        IBlockState centerState = world.getBlockState(centerPos);

        if (centerState.getBlock() == BlockRegistry.powerPoleTop)
            return BlockRegistry.powerPoleTop.getPickBlock(centerState, null, world, centerPos, player);

        return null;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;    //Prevent crash QAQ!!!
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        BlockPos centerPos = BlockPowerPoleBottom.getCenterBoxCoord(pos, state);
        Block centerBlock = world.getBlockState(centerPos).getBlock();

        if (centerBlock == BlockRegistry.powerPoleTop)
            world.setBlockToAir(centerPos);

        super.breakBlock(world, pos, state);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        BlockPos centerPos = BlockPowerPoleBottom.getCenterBoxCoord(pos, state);
        TileEntity te = world.getTileEntity(centerPos);
        
        return te instanceof TilePowerPole ? (TilePowerPole) te : null;
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
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
