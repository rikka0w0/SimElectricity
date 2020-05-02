package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.item.ItemBlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class BlockPowerPole2 extends BlockBase implements ICustomBoundingBox, ISEHVCableConnector {
    ///////////////////
    /// Utils
    ///////////////////
    private static final int[][] rodBlockOffsetMatrix = {
            {0, 3},    //{x,z}
            {3, 0},
            {0, -3},
            {-3, 0}
    };
    private static final int[][] cbOffsetMatrix = {
            {0, 1},    //{x,z}
            {1, 0},
            {0, -1},
            {-1, 0}
    };
    
    public final int type;
    private BlockPowerPole2(int type) {
        super("essential_powerpole2_" + String.valueOf(type), 
        		Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(0.2F, 10.0F)
        		.sound(SoundType.METAL), 
        		BlockPowerPole2.ItemBlock.class, 
        		(new Item.Properties()).group(SEAPI.SETab));
        this.type = type;
    }
    
    public static BlockPowerPole2[] create() {
    	return new BlockPowerPole2[] {new BlockPowerPole2(0), new BlockPowerPole2(1)};
    }

    private LinkedList<BlockInfo> getRodBlockOffsets(BlockState state) {
        LinkedList<BlockInfo> list = new LinkedList();

        int facing = getFacingInt(state);
        int facing1 = facing - 1 & 3;
        int facing2 = facing + 1 & 3;

        // TODO: Check 12???
        list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing1][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing1][1], 
        		this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, facingIntReverseMapping[facing2]).with(BlockStateProperties.EXTENDED, true)));
        list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing2][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing2][1], 
        		this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, facingIntReverseMapping[facing1]).with(BlockStateProperties.EXTENDED, true)));

        // Pole
        for (int i = 1; i < 15; i++) {
        	
            list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing1][0], i, BlockPowerPole2.rodBlockOffsetMatrix[facing1][1], 
            		this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, facingIntReverseMapping[facing2]).with(BlockStateProperties.EXTENDED, true)));
            list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing2][0], i, BlockPowerPole2.rodBlockOffsetMatrix[facing2][1], 
            		this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, facingIntReverseMapping[facing1]).with(BlockStateProperties.EXTENDED, true)));
        }

        return list;
    }

    private static LinkedList<Vec3i> getCollisionBoxBlockXZOffsets(BlockState state) {
        LinkedList<Vec3i> list = new LinkedList();

        int facing = getFacingInt(state);

        int facing1 = facing - 1 & 3;
        int xoffset = BlockPowerPole2.cbOffsetMatrix[facing1][0];
        int zoffset = BlockPowerPole2.cbOffsetMatrix[facing1][1];
        list.add(new Vec3i(xoffset, 0, zoffset));
        list.add(new Vec3i(xoffset * 2, 0, zoffset * 2));
        list.add(new Vec3i(xoffset * 4, 0, zoffset * 4));
        list.add(new Vec3i(xoffset * 5, 0, zoffset * 5));

        list.add(new Vec3i(xoffset * -1, 0, zoffset * -1));
        list.add(new Vec3i(xoffset * -2, 0, zoffset * -2));
        list.add(new Vec3i(xoffset * -4, 0, zoffset * -4));
        list.add(new Vec3i(xoffset * -5, 0, zoffset * -5));

        return list;
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    	builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.EXTENDED);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	PlayerEntity placer = context.getPlayer();
    	return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, Utils.getPlayerSightHorizontal(placer));
    }
    
    public int typeId() {
    	return this.type;
    }
    
    public static boolean isRod(BlockState blockstate) {
    	return blockstate.get(BlockStateProperties.EXTENDED);
    }
    
    private static int[] facingIntMapping = new int[] {-1, -1, 2, 0, 3, 1};
    private static Direction[] facingIntReverseMapping = new Direction[] {null, null, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    public static int getFacingInt(BlockState blockstate) {
    	// DUNSWE
    	//   2013
    	//   2413
    	return facingIntMapping[blockstate.get(BlockStateProperties.HORIZONTAL_FACING).ordinal()];
//    	return blockstate.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalIndex();
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
//    @Override
//    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
//        TileEntity te = this.getCenterTileFromRodPos(world, pos);
//        if (te instanceof TilePowerPole2)
//            return new ItemStack(this.itemBlock, 1, this.damageDropped(world.getBlockState(te.getPos())));
//
//        return ItemStack.EMPTY;
//    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isRemote)
            return;

        SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, 3));
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        int type = typeId();
        boolean isRod = isRod(state);
        int facing = this.getFacingInt(state);

        if (isRod) {
            int yc;
            if (type > 0) {
                yc = pos.getY() + 11;
            } else {
                yc = this.getBottomRodY(world, pos) + 11;
            }

            BlockPos centerPos = new BlockPos(pos.getX() + BlockPowerPole2.rodBlockOffsetMatrix[facing][0], yc, pos.getZ() + BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            BlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this && !isRod(centerState))
                world.setBlockState(centerPos, Blocks.AIR.getDefaultState());    //Sequentially everthing else will be removed
        } else {
            //Center Block
            TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
            if (te instanceof ISEGridTile)
                SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());

            for (BlockInfo blockInfo : getRodBlockOffsets(state)) {
                BlockPos rodPos = blockInfo.getRealPos(pos).down(11);
                BlockState rodState = world.getBlockState(rodPos);

                if (rodState.getBlock() == this && isRod(rodState))
                	world.setBlockState(rodPos, Blocks.AIR.getDefaultState());
            }

            for (Vec3i posXZOffset : BlockPowerPole2.getCollisionBoxBlockXZOffsets(state)) {
                BlockPos cbPos = pos.add(posXZOffset);
                BlockState cbState = world.getBlockState(cbPos);
                if (cbState.getBlock() == BlockRegistry.powerPoleCollisionBox)
                    world.setBlockState(cbPos, Blocks.AIR.getDefaultState());
            }
        }


        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return !isRod(state);}
	
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    	return isRod(state) ? null : new TilePowerPole2();
    }

    ///////////////////
    /// BoundingBox
    ///////////////////
    @Override
    public VoxelShape getBoundingShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        boolean isRod = isRod(state);
        int facing = getFacingInt(state);

        if (isRod) {
            BlockPos centerPos = pos.add(BlockPowerPole2.rodBlockOffsetMatrix[facing][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            BlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this) {
                if (!isRod(centerState))
                    if (facing == 0 || facing == 2) {
                        return VoxelShapes.create(0.125F, 0, 0, 0.875F, 1, 1);
                    } else {
                        return VoxelShapes.create(0, 0, 0.125F, 1, 1, 0.875F);
                    }
                else
                    return VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
            } else {
                return VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
            }
        } else {
            if (facing == 0 || facing == 2) {
                return VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F);
            } else {
                return VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        boolean isRod = isRod(state);
        int facing = getFacingInt(state);
        VoxelShape vs = VoxelShapes.empty();

        if (isRod) {
            vs = VoxelShapes.combine(vs, VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F), IBooleanFunction.OR);

            BlockPos centerPos = pos.add(BlockPowerPole2.rodBlockOffsetMatrix[facing][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            BlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this) {
                if (!isRod(centerState)) {
                    if (facing == 0 || facing == 2)
                        vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F), IBooleanFunction.OR);
                    else
                        vs = VoxelShapes.combine(vs, VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1), IBooleanFunction.OR);
                }
            }
        } else {
            if (facing == 0 || facing == 2) {
                vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F), IBooleanFunction.OR);
            } else {
                vs = VoxelShapes.combine(vs, VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1), IBooleanFunction.OR);
            }
        }
        
        return vs;
    }

    private int getBottomRodY(IBlockReader world, BlockPos pos) {
        for (int count = 0; count < 20; count++) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block == this && isRod(state) && typeId() > 0)
                return pos.getY();

            pos = pos.down();
        }

        return -1;
    }

    private TileEntity getCenterTileFromRodPos(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean isRod = isRod(state);
        int facing = this.getFacingInt(state);

        if (isRod) {
            int yc;
            if (typeId() > 0) {
                yc = pos.getY() + 11;
            } else {
                yc = this.getBottomRodY(world, pos) + 11;
            }

            BlockPos centerPos = new BlockPos(pos.getX() + BlockPowerPole2.rodBlockOffsetMatrix[facing][0], yc, pos.getZ() + BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            BlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this && !isRod(centerState))
                return world.getTileEntity(centerPos);

            return null;
        }

        return world.getTileEntity(pos);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = this.getCenterTileFromRodPos(world, pos);

        if (te instanceof TilePowerPole2)
            return (TilePowerPole2) te;
        else
            return null;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public static class ItemBlock extends ItemBlockBase {
        public ItemBlock(Block block, Item.Properties prop) {
            super(block, prop);
        }

        @Override
        public boolean placeBlock(BlockItemUseContext context, BlockState newState) {
        	World world = context.getWorld();
        	BlockPos pos = context.getPos();
            int facing = getFacingInt(newState);
            BlockPowerPole2 block = (BlockPowerPole2) this.getBlock();
            
            for (BlockInfo blockInfo : block.getRodBlockOffsets(newState))
                world.setBlockState(blockInfo.getRealPos(pos), blockInfo.state);

            int cbtype = facing == 0 || facing == 2 ? 9 : 10;
            for (Vec3i posXZOffset : BlockPowerPole2.getCollisionBoxBlockXZOffsets(newState))
                world.setBlockState(pos.up(11).add(posXZOffset), BlockRegistry.powerPoleCollisionBox.forPart(cbtype));

            //metedata [type][isRod][rotation]
            return super.placeBlock(context, newState);
        }
    }
}
