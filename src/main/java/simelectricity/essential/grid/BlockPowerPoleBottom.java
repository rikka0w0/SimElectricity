package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.block.BlockBase;
import rikka.librikka.tileentity.TileEntityBase;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISEHVCableConnector;

import java.util.LinkedList;

import javax.annotation.Nonnull;

public class BlockPowerPoleBottom extends BlockBase implements ISEHVCableConnector {
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
        super("essential_powerpole_bottom", 
        		Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(3.0F, 10.0F)
        		.sound(SoundType.METAL), 
        		new Item.Properties());
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
    public LinkedList<BlockInfo> getBaseBlockCoordOffsets(BlockState state) {
        LinkedList<BlockInfo> list = new LinkedList();

        int facing = BlockPowerPoleTop.getFacingInt(state);
        if ((facing & 1) == 0) {    // 90 x n
            facing = facing >> 1;
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, -2, 0, -2), 
            			this.getDefaultState().with(DirHorizontal8.prop, 
            			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][0])))
            		);
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, -2, 0, 2), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][1])))
            		);
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 2, 0, 2), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][2])))
            		);
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 2, 0, -2), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix90[facing][3])))
            		);
        } else {
            facing = facing >> 1;
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 0, 0, 3), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][0])))
            		);
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 3, 0, 0), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][1])))
            		);
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, 0, 0, -3), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][2])))
            		);
            list.add(new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, -3, 0, 0), 
        			this.getDefaultState().with(DirHorizontal8.prop, 
        			BlockPowerPoleTop.toDir8(BlockPowerPoleBottom.baseCoordOffsetMatrix45[facing][3])))
            		);
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
    public static BlockPos getCenterBoxCoord(BlockPos basePos, BlockState state) {
        int facing = BlockPowerPoleTop.getFacingInt(state);
        Vec3i offset = BlockPowerPoleBottom.getCenterBoxOffset(facing);
        return basePos.add(offset);
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
	
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new Tile();
    }
    
    public static class Tile extends TileEntityBase {
        public Tile() {
			super(Essential.MODID);
		}

        @Override
        @OnlyIn(Dist.CLIENT)
        public double getMaxRenderDistanceSquared() {
            return 100000;
        }

        @Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        public AxisAlignedBB getRenderBoundingBox() {
            return TileEntity.INFINITE_EXTENT_AABB;
        }
        
        @Override
        public boolean hasFastRenderer() {
            return true;
        }
    	
    	@OnlyIn(Dist.CLIENT)
    	public int getFacing() {
    		return BlockPowerPoleTop.getFacingInt(getBlockState());
    	}
    }
    
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    	builder.add(DirHorizontal8.prop);
	}

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        BlockPos centerPos = BlockPowerPoleBottom.getCenterBoxCoord(pos, state);
        BlockState centerState = world.getBlockState(centerPos);
        Block block = centerState.getBlock();
        
        if (block instanceof BlockPowerPoleTop)
            return block.getPickBlock(centerState, null, world, centerPos, player);

        return ItemStack.EMPTY;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        BlockPos centerPos = BlockPowerPoleBottom.getCenterBoxCoord(pos, state);
        Block centerBlock = world.getBlockState(centerPos).getBlock();

        if (centerBlock instanceof BlockPowerPoleTop)
            world.removeBlock(centerPos, false);

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockPos centerPos = BlockPowerPoleBottom.getCenterBoxCoord(pos, state);
        TileEntity te = world.getTileEntity(centerPos);
        
        return te instanceof TilePowerPole ? (TilePowerPole) te : null;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
}
