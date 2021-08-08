package simelectricity.essential.cable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.IMetaProvider;
import rikka.librikka.MarkedBlockHitResult;
import rikka.librikka.RayTraceHelper;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.BlockUtils;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.item.ItemBlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.ISEGenericWire;
import simelectricity.essential.utils.SEUnitHelper;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockWire extends BlockBase implements EntityBlock, ICustomBoundingBox, IMetaProvider<ISECableMeta>, SimpleWaterloggedBlock  {
    ///////////////////////////////
    /// Wire Properties
    ///////////////////////////////
	public static enum Type implements ISECableMeta {
		copper(0.05F),
		aluminum(0.075F);

		public final float resistivity;
		Type(float resistivity) {
			this.resistivity = resistivity;
		}

		@Override
		public float thickness() {
			return 0.1F;
		}

		@Override
		public float resistivity() {
			return resistivity;
		}

	}


    private BlockWire(ISECableMeta meta) {
        this("wire", meta,
        		BlockBehaviour.Properties.of(Material.GLASS)
        		.strength(0.2F, 10.0F)
        		.sound(SoundType.METAL)
        		.isRedstoneConductor((a,b,c)->false),
        		ItemBlockWire.class,
        		(new Item.Properties()).tab(SEAPI.SETab),
                TileWire::new);
    }

    public static BlockWire[] create() {
    	BlockWire[] ret = new BlockWire[Type.values().length];
    	for (ISECableMeta cableData: Type.values()) {
    		ret[cableData.ordinal()] = new BlockWire(cableData);
    	}
    	return ret;
    }


    public final ISECableMeta meta;
    protected static ThreadLocal<Direction> nextPlacedSide = new ThreadLocal<>();
    protected static ThreadLocal<Direction> nextPlacedto = new ThreadLocal<>();
    protected static ThreadLocal<ItemStack> nextPlacedItemStack = new ThreadLocal<>();

    @Override
    public ISECableMeta meta() {
    	return meta;
    }

    public boolean addBranch(ISEGenericWire wireTile, Direction side, Direction to, ItemStack itemStack, boolean isRemote) {
        if (!wireTile.canAddBranch(side, to, itemStack))
            return false;

        if (!isRemote)
            wireTile.addBranch(side, to, itemStack, meta.resistivity());

        return true;
    }

    protected static class ItemBlockWire extends ItemBlockBase {
        public ItemBlockWire(Block block, Item.Properties props) {
            super(block, props);
        }

        @Override	// Was canPlaceBlockOnSide
        protected boolean canPlace(BlockPlaceContext context, BlockState stateForPlace) {
        	Level world = context.getLevel();
        	BlockPos pos = context.getClickedPos();
        	Direction side = context.getClickedFace();

            BlockEntity teSelected = world.getBlockEntity(pos);
            BlockEntity teNew = world.getBlockEntity(pos.relative(side));

            if (teSelected instanceof ISEGenericWire) {
                return true;
            } else if (teNew instanceof ISEGenericWire) {
                return true;
            }

            return super.canPlace(context, stateForPlace);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
        	Player player = context.getPlayer();
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            InteractionHand hand = context.getHand();
            Direction facing = context.getClickedFace();
            Vec3 Vec3 = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            float hitX = (float) Vec3.x;
            float hitY = (float) Vec3.y;
            float hitZ = (float) Vec3.z;


        	float x = hitX-facing.getStepX()-0.5f;
            float y = hitY-facing.getStepY()-0.5f;
            float z = hitZ-facing.getStepZ()-0.5f;


            Direction to = null;
            if (facing.getAxis() == Direction.Axis.Y) {
                if (Mth.abs(x) > Mth.abs(z)) {
                    if (x>0)
                        to = Direction.EAST;
                    else
                        to = Direction.WEST;
                } else {
                    if (z>0)
                        to = Direction.SOUTH;
                    else
                        to = Direction.NORTH;
                }
            } else if (facing.getAxis() == Direction.Axis.X) {
                if (Mth.abs(y) > Mth.abs(z)) {
                    if (y>0)
                        to = Direction.UP;
                    else
                        to = Direction.DOWN;
                } else {
                    if (z>0)
                        to = Direction.SOUTH;
                    else
                        to = Direction.NORTH;
                }
            } else if (facing.getAxis() == Direction.Axis.Z) {
                if (Mth.abs(x) > Mth.abs(y)) {
                    if (x>0)
                        to = Direction.EAST;
                    else
                        to = Direction.WEST;
                } else {
                    if (y>0)
                        to = Direction.UP;
                    else
                        to = Direction.DOWN;
                }
            }

            BlockWire blockWire = (BlockWire) getBlock();
            ItemStack itemStack = player.getItemInHand(hand);
            boolean shrinkItem = false;
            BlockEntity teSelected = BlockUtils.getTileEntitySafely(world, pos);
            BlockEntity teNew = BlockUtils.getTileEntitySafely(world, pos.relative(facing));

            if (teSelected instanceof ISEGenericWire) {
            	// TODO: fix this
            	MarkedBlockHitResult<Integer> trace = blockWire.rayTrace(world, pos, player);
//                RayTraceResult trace = this.rayTrace(world, player, RayTraceContext.FluidMode.NONE);
                if (trace.getBlockPos().equals(pos) && subHit_isBranch(trace.subHit)) {
                    ISEGenericWire wireTile = (ISEGenericWire) teSelected;
                    Direction tr_side = subHit_side(trace.subHit);
                    Direction tr_branch = subHit_branch(trace.subHit);

                    if (tr_branch == null) {
                        // Center
                        if (facing != tr_side && facing != tr_side.getOpposite()) {
                            if (!wireTile.hasBranch(tr_side, facing) && BlockUtils.isSideSolid(world, pos.relative(tr_side), tr_side.getOpposite())) {
                                shrinkItem = blockWire.addBranch(wireTile, tr_side, facing, itemStack, world.isClientSide);
                            }
                        }
                    } else {

                        if (facing == tr_side || facing == tr_side.getOpposite()) {

                            if (!wireTile.hasBranch(to, facing.getOpposite()) &&
                                    (BlockUtils.isSideSolid(world, pos.relative(to), to.getOpposite()) ||
                                            world.getBlockEntity(pos.relative(to)) instanceof ISECableTile)) {
                                shrinkItem = blockWire.addBranch(wireTile, to, facing.getOpposite(), itemStack, world.isClientSide);
                            }
                        } else {
                            if (wireTile.hasBranch(tr_side, facing)) {
                                if (teNew instanceof ISEGenericWire) {
                                    // Add branch in neighbor
                                    if (!((ISEGenericWire) teNew).hasBranch(tr_side, tr_branch.getOpposite()) &&
                                            (BlockUtils.isSideSolid(world, pos.relative(facing).relative(tr_side), tr_side.getOpposite()) ||
                                                    world.getBlockEntity(pos.relative(tr_branch).relative(tr_side)) instanceof ISECableTile)) {
                                        shrinkItem = blockWire.addBranch((ISEGenericWire) teNew, tr_side, tr_branch.getOpposite(), itemStack, world.isClientSide);
                                    }
                                } else {
                                    // Block edge, try to place a new neighbor wire
                                    if (BlockUtils.isSideSolid(world, pos.relative(tr_branch).relative(tr_side), tr_side.getOpposite()) ||
                                            world.getBlockEntity(pos.relative(tr_branch).relative(tr_side)) instanceof ISECableTile) {
                                        nextPlacedSide.set(tr_side);
                                        nextPlacedto.set(tr_branch.getOpposite());
                                        nextPlacedItemStack.set(itemStack);

                                        return super.useOn(context);
                                    }
                                }
                            } else {
                                if (!wireTile.hasBranch(tr_side, facing) && BlockUtils.isSideSolid(world, pos.relative(tr_side), tr_side.getOpposite())) {
                                    shrinkItem = blockWire.addBranch(wireTile, tr_side, facing, itemStack, world.isClientSide);
                                }
                            }
                        }
                    }
                }
            } else if (teNew instanceof ISEGenericWire) {
                Direction wire_side = facing.getOpposite();
                // Selecting the block after the ISEGenericWire block
                if (!((ISEGenericWire) teNew).hasBranch(wire_side, to) && BlockUtils.isSideSolid(world, pos, wire_side.getOpposite())) {
                    shrinkItem = blockWire.addBranch((ISEGenericWire) teNew, wire_side, to, itemStack, world.isClientSide);
                }
            } else {
                // Attempt to place fresh wire

                nextPlacedSide.set(facing.getOpposite());
                nextPlacedto.set(to);
                nextPlacedItemStack.set(itemStack);

                return super.useOn(context);
            }

            if (shrinkItem) {
                if (!player.isCreative())
                    itemStack.shrink(1);

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    public static Direction[][] corners = new Direction[][] {
            {Direction.DOWN, Direction.NORTH},
            {Direction.DOWN, Direction.SOUTH},
            {Direction.DOWN, Direction.WEST},
            {Direction.DOWN, Direction.EAST},
            {Direction.UP, Direction.NORTH},
            {Direction.UP, Direction.SOUTH},
            {Direction.UP, Direction.WEST},
            {Direction.UP, Direction.EAST},
            {Direction.NORTH, Direction.WEST},
            {Direction.NORTH, Direction.EAST},
            {Direction.SOUTH, Direction.WEST},
            {Direction.SOUTH, Direction.EAST},
    };

    public static boolean isCornerIdNormal(Direction f1, Direction f2) {
        if (f1.getAxis() == f2.getAxis())
            return false;

        if (f1.getAxis() == Direction.Axis.Y) {
            return true;
        } else if (f1.getAxis() == Direction.Axis.Z) {
            return f2.getAxis() != Direction.Axis.Y;
        } else {
            return false;
        }
    }

    public static int cornerIdOf(Direction f1, Direction f2) {
        int index = -1;

        if (f1.getAxis() == f2.getAxis())
            return -1;

        Direction wire_side = null, branch = null;
        if (f1.getAxis() == Direction.Axis.Y) {
            wire_side = f1;
            branch = f2;
        } else if (f1.getAxis() == Direction.Axis.Z) {
            if (f2.getAxis() == Direction.Axis.Y) {
                wire_side = f2;
                branch = f1;
            } else {
                wire_side = f1;
                branch = f2;
            }
        } else {
            // f1.getAxis() == Direction.Axis.X
            wire_side = f2;
            branch = f1;
        }

        if (wire_side == Direction.DOWN) {
            if (branch == Direction.NORTH) {
                index = 0;
            } else if (branch == Direction.SOUTH) {
                index = 1;
            } else if (branch == Direction.WEST) {
                index = 2;
            } else if (branch == Direction.EAST) {
                index = 3;
            }
        } else if (wire_side == Direction.UP) {
            if (branch == Direction.NORTH) {
                index = 4;
            } else if (branch == Direction.SOUTH) {
                index = 5;
            } else if (branch == Direction.WEST) {
                index = 6;
            } else if (branch == Direction.EAST) {
                index = 7;
            }
        } else if (wire_side == Direction.NORTH) {
            if (branch == Direction.WEST) {
                index = 8;
            } else if (branch == Direction.EAST) {
                index = 9;
            }
        } else if (wire_side == Direction.SOUTH) {
            if (branch == Direction.WEST) {
                index = 10;
            } else if (branch == Direction.EAST) {
                index = 11;
            }
        }

        return index;
    }


    private final BlockEntitySupplier<? extends TileWire> blockEntitySupplier;
    protected BlockWire(String name, ISECableMeta meta, BlockBehaviour.Properties props, Class<? extends ItemBlockBase> itemBlockClass,
    		Item.Properties itemProps, BlockEntitySupplier<? extends TileWire> blockEntitySupplier) {
    	super(name+"_"+meta.name(), props, itemBlockClass, itemProps);
    	this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        this.meta = meta;
        this.blockEntitySupplier = blockEntitySupplier;

        //Calc. collision boxes and cache them
//        this.cableBoundingBoxes = new AABB[thicknessList.length][7];
//        for (int i=0; i<thicknessList.length; i++) {
//            float min = 0.5F - thicknessList[i] / 2F;
//            float max = 0.5F + thicknessList[i] / 2F;
//
//            for (Direction side: Direction.values()) {
//                cableBoundingBoxes[i][side.ordinal()] = RayTraceHelper.createAABB(side, min, 0, min, max, min, max);
//            }
//
//            cableBoundingBoxes[i][6] = new AABB(min, min, min, max, max, max);
//        }
    }

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false)
				: super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState FluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, FluidState.getType() == Fluids.WATER);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(BlockStateProperties.WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    	tooltip.add(new TextComponent(
    			I18n.get("gui.simelectricity.resistivity") + ": " +
    			SEUnitHelper.getStringWithoutUnit(2F*meta.resistivity()) + "\u03a9/m"
    			));;
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        TileWire wire;
        try {
            wire = blockEntitySupplier.create(pos, state);
            //if (!world.isRemote)    //createTileEntity is only called by the server thread when the block is placed at the first
                //wire.setResistanceOnPlace(this.resistances[this.getMetaFromState(state)]);
            return wire;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //////////////////////////////////
    ///CollisionBoxes
    //////////////////////////////////
    public Vec3 getBranchVecOffset(Direction side) {
        float thickness = meta.thickness();
        double x = 0, y = 0, z = 0;
        switch (side) {
            case DOWN:
                y = thickness / 2 - 0.5F;
                break;
            case UP:
                y = 0.5F-thickness / 2;
                break;
            case NORTH:
                z = thickness / 2 - 0.5F;
                break;
            case SOUTH:
                z = 0.5F - thickness / 2;
                break;
            case WEST:
                x = thickness / 2 - 0.5F;
                break;
            case EAST:
                x = 0.5F - thickness / 2;
                break;
        }

        return new Vec3(x, y, z);
    }

    public AABB getBranchBoundingBox(Direction side, Direction branch, boolean ignoreCorner, boolean onlyCorner) {
        float thickness = meta.thickness();
    	float min = 0.5F - thickness / 2.0F;
        float max = 0.5F + thickness / 2.0F;

        float yMin = 0;
        float yMax = min;

        if (ignoreCorner)
            yMin = thickness;
        if (onlyCorner)
            yMax = thickness;

        return (branch == null) ?
                new AABB(min, min, min, max, max, max).move(getBranchVecOffset(side)) : // Center
                RayTraceHelper.createAABB(branch, min, yMin, min, max, yMax, max).move(getBranchVecOffset(side));
    }

    public AABB getCenterBoundingBox(ISEGenericWire wireTile, Direction side) {
        float thickness = meta.thickness();
        double x1, y1, z1, x2, y2, z2;
        x1 = 0.5 - thickness / 2;
        y1 = x1;
        z1 = x1;
        x2 = 0.5 + thickness / 2;
        y2 = x2;
        z2 = x2;

        //Branches
        if (wireTile.hasBranch(side, Direction.DOWN))
            y1 = 0;

        if (wireTile.hasBranch(side, Direction.UP))
            y2 = 1;

        if (wireTile.hasBranch(side, Direction.NORTH))
            z1 = 0;

        if (wireTile.hasBranch(side, Direction.SOUTH))
            z2 = 1;

        if (wireTile.hasBranch(side, Direction.WEST))
            x1 = 0;

        if (wireTile.hasBranch(side, Direction.EAST))
            x2 = 1;

        return new AABB(x1, y1, z1, x2, y2, z2).move(getBranchVecOffset(side));
    }

    public AABB getCornerBoundingBox(ISEGenericWire wireTile, Direction side1, Direction side2) {
        Direction e1 = null;
        Direction e2 = null;
        if (side1.getAxis() == Direction.Axis.Y) {
            e1 = side1;
            e2 = side2;
        } else if (side2.getAxis() == Direction.Axis.Y) {
            e1 = side2;
            e2 = side1;
        } else  if (side1.getAxis() == Direction.Axis.Z) {
            e1 = side1;
            e2 = side2;
        } else if (side2.getAxis() == Direction.Axis.Z) {
            e1 = side2;
            e2 = side1;
        }

        double thickness = meta.thickness();
        double x1, y1, z1, x2, y2, z2;
        x1 = 0.5 + thickness / 2;
        y1 = x1;
        z1 = x1;
        x2 = 0.5 - thickness / 2;
        y2 = x2;
        z2 = x2;


        if (e1 == Direction.DOWN) {
            y1 = 0;

            if (e2 == Direction.NORTH) {
                z1 = 0;
            } else if (e2 == Direction.SOUTH) {
                z2 = 1;
            } else if (e2 == Direction.WEST) {
                x1 = 0;
            } else if (e2 == Direction.EAST) {
                x2 = 1;
            }
        } else if (e1 == Direction.UP) {
            y2 = 1;

            if (e2 == Direction.NORTH) {
                z1 = 0;
            } else if (e2 == Direction.SOUTH) {
                z2 = 1;
            } else if (e2 == Direction.WEST) {
                x1 = 0;
            } else if (e2 == Direction.EAST) {
                x2 = 1;
            }
        } else if (e1 == Direction.NORTH) {
            z1 = 0;

            if (e2 == Direction.WEST) {
                x1 = 0;
            } else if (e2 == Direction.EAST) {
                x2 = 1;
            }
        } else if (e1 == Direction.SOUTH) {
            z2 = 1;

            if (e2 == Direction.WEST) {
                x1 = 0;
            } else if (e2 == Direction.EAST) {
                x2 = 1;
            }
        }

        return new AABB(x1, y1, z1, x2, y2, z2);
    }

    public static Direction subHit_side(int subHit) {
        return Direction.from3DDataValue((subHit>>4) & 0x07);
    }

    @Nullable
    public static Direction subHit_branch(int subHit) {
        int to_int = subHit & 0x0F;
        return to_int > Direction.values().length ? null : Direction.from3DDataValue(to_int);
    }

    public static boolean subHit_isBranch(int subHit) {
        return subHit > -1 && subHit < 256;
    }

    public static boolean subHit_isCorner(int subHit) {
        return (subHit & 0x80) > 0;
    }

    // TODO: Check collisionRayTrace (getRayTraceResult) and getRaytraceShape
    // Was RayTraceResult collisionRayTrace(IBlockState state, Level world, BlockPos pos, Vec3 start, Vec3 end)
//    @Override
//    public RayTraceResult getRayTraceResult(BlockState state, Level world, BlockPos pos, Vec3 start, Vec3 end, RayTraceResult original) {
//    	return this.rayTrace(world, pos, start, end);
//    }

    @Nullable
    public MarkedBlockHitResult<Integer> rayTrace(BlockGetter world, BlockPos pos, Player player) {
        Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
        double reachDistance = 5;

//        if (player instanceof EntityPlayerMP)
//            reachDistance = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();

        Vec3 end = start.add(player.getLookAngle().normalize().scale(reachDistance));
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public MarkedBlockHitResult<Integer> rayTrace(BlockGetter world, BlockPos pos, Vec3 start, Vec3 end) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof ISEGenericWire))
            return MarkedBlockHitResult.rayTrace(pos, start, end, Shapes.block().bounds(), -1);

        ISEGenericWire wireTile = (ISEGenericWire) tile;

        MarkedBlockHitResult<Integer> best = null;
        for (Direction wire_side:  Direction.values()) {
            boolean hasConnection = false;

            // Branches
            for (Direction to : Direction.values()) {
                if (wireTile.hasBranch(wire_side, to)) {
                    hasConnection = true;

                    boolean hasCorner = wireTile.hasBranch(to, wire_side);
                    best = MarkedBlockHitResult.iterate(best, pos, start, end,
                            getBranchBoundingBox(wire_side, to, hasCorner, false),
                            (wire_side.ordinal() << 4) | to.ordinal());

                    if (hasCorner)
                        best = MarkedBlockHitResult.iterate(best, pos, start, end,
                                getBranchBoundingBox(wire_side, to, false, true),
                                0x80 | (wire_side.ordinal() << 4) | to.ordinal());
                }
            }

            // Center
            if (hasConnection)
                best = MarkedBlockHitResult.iterate(best, pos, start, end, getBranchBoundingBox(wire_side,null, false, false), (wire_side.ordinal() << 4) | 7);
        }

        return best;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockEntity te = world.getBlockEntity(pos);

        VoxelShape vs = Shapes.empty();
        if (!(te instanceof ISEWireTile))
            return vs;

        ISEGenericWire wireTile = (ISEGenericWire) te;
        float thickness = meta.thickness();

        for (Direction wire_side: Direction.values()) {
            double min = 0.5 - thickness / 2;
            double max = 0.5 + thickness / 2;

            double x = 0, y = 0 , z = 0;
            switch (wire_side) {
                case DOWN:
                    y = thickness / 2 - 0.5F;
                    break;
                case UP:
                    y = 0.5F-thickness / 2;
                    break;
                case NORTH:
                    z = thickness / 2 - 0.5F;
                    break;
                case SOUTH:
                    z = 0.5F - thickness / 2;
                    break;
                case WEST:
                    x = thickness / 2 - 0.5F;
                    break;
                case EAST:
                    x = 0.5F - thickness / 2;
                    break;
            }

            boolean hasConnection = false;

            //Branches
            if (wireTile.hasBranch(wire_side, Direction.DOWN)) {
                hasConnection = true;
                vs = Shapes.joinUnoptimized(vs, Shapes.box(min+x, y, min+z, max+x, max+y, max+z), BooleanOp.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.UP)) {
                hasConnection = true;
                vs = Shapes.joinUnoptimized(vs, Shapes.box(min+x, min+y, min+z, max+x, 1+y, max+z), BooleanOp.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.NORTH)) {
                hasConnection = true;
                vs = Shapes.joinUnoptimized(vs, Shapes.box(min+x, min+y, z, max+x, max+y, max+z), BooleanOp.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.SOUTH)) {
                hasConnection = true;
                vs = Shapes.joinUnoptimized(vs, Shapes.box(min+x, min+y, min+z, max+x, max+y, 1+z), BooleanOp.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.WEST)) {
                hasConnection = true;
                vs = Shapes.joinUnoptimized(vs, Shapes.box(x, min+y, min+z, max+x, max+y, max+z), BooleanOp.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.EAST)) {
                hasConnection = true;
                vs = Shapes.joinUnoptimized(vs, Shapes.box(min+x, min+y, min+z, 1+x, max+y, max+z), BooleanOp.OR);
            }

            if (hasConnection) {
                //Center
                vs = Shapes.joinUnoptimized(vs, Shapes.box(min+x, min+y, min+z, max+x, max+y, max+z), BooleanOp.OR);
            }
        }

		return vs;
    }

    // Was AABB getSelectedBoundingBox(IBlockState state, Level world, BlockPos pos)
    @Override
    public VoxelShape getBoundingShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	VoxelShape ret = Shapes.empty();
        BlockEntity te = world.getBlockEntity(pos);

        if (!(te instanceof ISEGenericWire))
            return ret;  //This is not supposed to happen

        ISEGenericWire wireTile = (ISEGenericWire) te;
        MarkedBlockHitResult<Integer> trace = rayTrace(world, pos, Essential.proxy.getClientPlayer());

        if (trace == null || trace.subHit < 0 || !pos.equals(trace.getBlockPos())) {
            // Perhaps we aren't the object the mouse is over
            return ret;
        }

        AABB aabb = null;
        if (subHit_isBranch(trace.subHit)) {    //Center, corner or branches
            boolean isCorner = subHit_isCorner(trace.subHit);
            Direction wire_side = subHit_side(trace.subHit);
            Direction to = subHit_branch(trace.subHit);

            if (isCorner) {
                // Corner
                aabb = getCornerBoundingBox(wireTile, wire_side, to);
            } else {
                if (to == null) {
                    // Center
                    aabb = getCenterBoundingBox(wireTile, wire_side);
                } else {
                    aabb = getBranchBoundingBox(wire_side, to, wireTile.hasBranch(to, wire_side), false);
                }
            }

            aabb = aabb.inflate(0.025);
        }

        return aabb==null ? Shapes.empty() : Shapes.create(aabb);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide)
            return;

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ISEGenericWire) {
            ISEGenericWire wireTile = (ISEGenericWire) te;
            wireTile.onRenderingUpdateRequested();

            int x = fromPos.getX() - pos.getX();
            int y = fromPos.getY() - pos.getY();
            int z = fromPos.getZ() - pos.getZ();
            if (x*x+y*y+z*z != 1)
                return; // the change is not send from neighbor block!!!

            BlockEntity teFrom = world.getBlockEntity(fromPos);
            if (teFrom instanceof ISEGenericCable)
            	return;

            Direction side = Direction.getNearest(x, y, z);
            if (wireTile.hasBranch(side, null) && !BlockUtils.isSideSolid(world, fromPos, side.getOpposite())) {
                // The opposite side is no longer solid

                // Drop wires as items
                LinkedList<ItemStack> drops = new LinkedList<>();
                wireTile.removeBranch(side, null, drops);
                for (ItemStack stack: drops)
                    Utils.dropItemIntoWorld(world, pos, stack);

                for (Direction wire_side: Direction.values()) {
                    if (wireTile.hasBranch(wire_side, null))
                        return;
                }

                // All branches have been removed, set the wire block to air.
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
    	if (world.isClientSide)
            return;

        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return;

        ISEGenericWire wireTile = (ISEGenericWire) te;
        addBranch(wireTile, nextPlacedSide.get(), nextPlacedto.get(), nextPlacedItemStack.get(), world.isClientSide);
        wireTile.onRenderingUpdateRequested();
    }

    ///////////////////////
    /// Item drops
    ///////////////////////
    ThreadLocal<List<ItemStack>> itemDrops = new ThreadLocal<>();
    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (world.isClientSide)
            return false;

        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

        ISEGenericWire wireTile = (ISEGenericWire) te;

        MarkedBlockHitResult<Integer> trace = this.rayTrace(world, pos, player);
        if (trace == null)
            return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

        if (subHit_isBranch(trace.subHit) && trace.getBlockPos().equals(pos)) {
            // Remove cable branch
            LinkedList<ItemStack> drops = new LinkedList<>();
            boolean isCorner = subHit_isCorner(trace.subHit);
            Direction wire_side = subHit_side(trace.subHit);
            Direction to = subHit_branch(trace.subHit);
            wireTile.removeBranch(wire_side, to, drops);

            if (isCorner && wireTile.hasBranch(to, wire_side)) {
                // Corner removed, so remove neighbor branches
                wireTile.removeBranch(to, wire_side, drops);
            }

            if (!player.isCreative())
                for (ItemStack stack: drops)
                    Utils.dropItemIntoWorld(world, pos, stack);

            for (Direction side: Direction.values()) {
                if (wireTile.hasBranch(side, null))
                        return false;
            }

            return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return ItemStack.EMPTY;

        ISEGenericWire wireTile = (ISEGenericWire) te;

        MarkedBlockHitResult<Integer> trace = this.rayTrace(world, pos, player);

        if (subHit_isBranch(trace.subHit) && trace.getBlockPos().equals(pos)) {    //Center, corner or branches
            Direction wire_side = subHit_side(trace.subHit);

            if (wireTile.getWireParam(wire_side).hasBranchOnSide(null)) {
                ItemStack stack = wireTile.getItemDrop(wire_side);
                stack.setCount(1);
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
