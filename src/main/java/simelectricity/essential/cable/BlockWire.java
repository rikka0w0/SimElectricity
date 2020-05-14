package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.IMetaProvider;
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

public class BlockWire extends BlockBase implements ICustomBoundingBox, IMetaProvider<ISECableMeta>, IWaterLoggable  {
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
        		Block.Properties.create(Material.GLASS)
        		.hardnessAndResistance(0.2F, 10.0F)
        		.sound(SoundType.METAL),
        		ItemBlockWire.class,
        		(new Item.Properties()).group(SEAPI.SETab),
                TileWire.class);
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
        protected boolean canPlace(BlockItemUseContext context, BlockState stateForPlace) {
        	World world = context.getWorld();
        	BlockPos pos = context.getPos();
        	Direction side = context.getFace();
        	
            TileEntity teSelected = world.getTileEntity(pos);
            TileEntity teNew = world.getTileEntity(pos.offset(side));

            if (teSelected instanceof ISEGenericWire) {
                return true;
            } else if (teNew instanceof ISEGenericWire) {
                return true;
            }

            return super.canPlace(context, stateForPlace);
        }

        @Override
        public ActionResultType onItemUse(ItemUseContext context) {
        	PlayerEntity player = context.getPlayer();
            World world = context.getWorld();
            BlockPos pos = context.getPos();
            Hand hand = context.getHand();
            Direction facing = context.getFace();
            Vec3d vec3d = context.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ());
            float hitX = (float) vec3d.x;
            float hitY = (float) vec3d.y;
            float hitZ = (float) vec3d.z;
            
            
        	float x = hitX-facing.getXOffset()-0.5f;
            float y = hitY-facing.getYOffset()-0.5f;
            float z = hitZ-facing.getZOffset()-0.5f;


            Direction to = null;
            if (facing.getAxis() == Direction.Axis.Y) {
                if (MathHelper.abs(x) > MathHelper.abs(z)) {
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
                if (MathHelper.abs(y) > MathHelper.abs(z)) {
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
                if (MathHelper.abs(x) > MathHelper.abs(y)) {
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
            ItemStack itemStack = player.getHeldItem(hand);
            boolean shrinkItem = false;
            TileEntity teSelected = BlockUtils.getTileEntitySafely(world, pos);
            TileEntity teNew = BlockUtils.getTileEntitySafely(world, pos.offset(facing));

            if (teSelected instanceof ISEGenericWire) {
            	// TODO: fix this
            	BlockRayTraceResult trace = blockWire.rayTrace(world, pos, player);
//                RayTraceResult trace = this.rayTrace(world, player, RayTraceContext.FluidMode.NONE);
                if (trace.getPos().equals(pos) && subHit_isBranch(trace.subHit)) {
                    ISEGenericWire wireTile = (ISEGenericWire) teSelected;
                    Direction tr_side = subHit_side(trace.subHit);
                    Direction tr_branch = subHit_branch(trace.subHit);

                    if (tr_branch == null) {
                        // Center
                        if (facing != tr_side && facing != tr_side.getOpposite()) {
                            if (!wireTile.hasBranch(tr_side, facing) && BlockUtils.isSideSolid(world, pos.offset(tr_side), tr_side.getOpposite())) {
                                shrinkItem = blockWire.addBranch(wireTile, tr_side, facing, itemStack, world.isRemote);
                            }
                        }
                    } else {

                        if (facing == tr_side || facing == tr_side.getOpposite()) {

                            if (!wireTile.hasBranch(to, facing.getOpposite()) &&
                                    (BlockUtils.isSideSolid(world, pos.offset(to), to.getOpposite()) ||
                                            world.getTileEntity(pos.offset(to)) instanceof ISECableTile)) {
                                shrinkItem = blockWire.addBranch(wireTile, to, facing.getOpposite(), itemStack, world.isRemote);
                            }
                        } else {
                            if (wireTile.hasBranch(tr_side, facing)) {
                                if (teNew instanceof ISEGenericWire) {
                                    // Add branch in neighbor
                                    if (!((ISEGenericWire) teNew).hasBranch(tr_side, tr_branch.getOpposite()) &&
                                            (BlockUtils.isSideSolid(world, pos.offset(facing).offset(tr_side), tr_side.getOpposite()) ||
                                                    world.getTileEntity(pos.offset(tr_branch).offset(tr_side)) instanceof ISECableTile)) {
                                        shrinkItem = blockWire.addBranch((ISEGenericWire) teNew, tr_side, tr_branch.getOpposite(), itemStack, world.isRemote);
                                    }
                                } else {
                                    // Block edge, try to place a new neighbor wire
                                    if (BlockUtils.isSideSolid(world, pos.offset(tr_branch).offset(tr_side), tr_side.getOpposite()) ||
                                            world.getTileEntity(pos.offset(tr_branch).offset(tr_side)) instanceof ISECableTile) {
                                        nextPlacedSide.set(tr_side);
                                        nextPlacedto.set(tr_branch.getOpposite());
                                        nextPlacedItemStack.set(itemStack);

                                        return super.onItemUse(context);
                                    }
                                }
                            } else {
                                if (!wireTile.hasBranch(tr_side, facing) && BlockUtils.isSideSolid(world, pos.offset(tr_side), tr_side.getOpposite())) {
                                    shrinkItem = blockWire.addBranch(wireTile, tr_side, facing, itemStack, world.isRemote);
                                }
                            }
                        }
                    }
                }
            } else if (teNew instanceof ISEGenericWire) {
                Direction wire_side = facing.getOpposite();
                // Selecting the block after the ISEGenericWire block
                if (!((ISEGenericWire) teNew).hasBranch(wire_side, to) && BlockUtils.isSideSolid(world, pos, wire_side.getOpposite())) {
                    shrinkItem = blockWire.addBranch((ISEGenericWire) teNew, wire_side, to, itemStack, world.isRemote);
                }
            } else {
                // Attempt to place fresh wire

                nextPlacedSide.set(facing.getOpposite());
                nextPlacedto.set(to);
                nextPlacedItemStack.set(itemStack);

                return super.onItemUse(context);
            }

            if (shrinkItem) {
                if (!player.isCreative())
                    itemStack.shrink(1);

                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.FAIL;
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

    
    private final Class<? extends TileWire> tileEntityClass;
    protected BlockWire(String name, ISECableMeta meta, Block.Properties props, Class<? extends ItemBlockBase> itemBlockClass,
    		Item.Properties itemProps, Class<? extends TileWire> tileEntityClass) {
    	super(name+"_"+meta.name(), props, itemBlockClass, itemProps);
    	this.setDefaultState(this.getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
        this.meta = meta;
        this.tileEntityClass = tileEntityClass;

        //Calc. collision boxes and cache them
//        this.cableBoundingBoxes = new AxisAlignedBB[thicknessList.length][7];
//        for (int i=0; i<thicknessList.length; i++) {
//            float min = 0.5F - thicknessList[i] / 2F;
//            float max = 0.5F + thicknessList[i] / 2F;
//
//            for (Direction side: Direction.values()) {
//                cableBoundingBoxes[i][side.ordinal()] = RayTraceHelper.createAABB(side, min, 0, min, max, min, max);
//            }
//
//            cableBoundingBoxes[i][6] = new AxisAlignedBB(min, min, min, max, max, max);
//        }
    }

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public IFluidState getFluidState(BlockState state) {
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false)
				: super.getFluidState(state);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(BlockStateProperties.WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(BlockStateProperties.WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	tooltip.add(new StringTextComponent(
    			I18n.format("gui.simelectricity.resistivity") + ": " + 
    			SEUnitHelper.getStringWithoutUnit(2F*meta.resistivity()) + "\u03a9/m"
    			));;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileWire wire;
        try {
            wire = tileEntityClass.getConstructor().newInstance();
            //if (!world.isRemote)    //createTileEntity is only called by the server thread when the block is placed at the first
                //wire.setResistanceOnPlace(this.resistances[this.getMetaFromState(state)]);
            return wire;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    //////////////////////////////////
    ///CollisionBoxes
    //////////////////////////////////
    public Vec3d getBranchVecOffset(Direction side) {
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

        return new Vec3d(x, y, z);
    }

    public AxisAlignedBB getBranchBoundingBox(Direction side, Direction branch, boolean ignoreCorner, boolean onlyCorner) {
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
                new AxisAlignedBB(min, min, min, max, max, max).offset(getBranchVecOffset(side)) : // Center
                RayTraceHelper.createAABB(branch, min, yMin, min, max, yMax, max).offset(getBranchVecOffset(side));
    }

    public AxisAlignedBB getCenterBoundingBox(ISEGenericWire wireTile, Direction side) {
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

        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2).offset(getBranchVecOffset(side));
    }

    public AxisAlignedBB getCornerBoundingBox(ISEGenericWire wireTile, Direction side1, Direction side2) {
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

        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public static Direction subHit_side(int subHit) {
        return Direction.byIndex((subHit>>4) & 0x07);
    }

    @Nullable
    public static Direction subHit_branch(int subHit) {
        int to_int = subHit & 0x0F;
        return to_int > Direction.values().length ? null : Direction.byIndex(to_int);
    }

    public static boolean subHit_isBranch(int subHit) {
        return subHit > -1 && subHit < 256;
    }

    public static boolean subHit_isCorner(int subHit) {
        return (subHit & 0x80) > 0;
    }

    // TODO: Check collisionRayTrace (getRayTraceResult) and getRaytraceShape
    // Was RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
//    @Override
//    public RayTraceResult getRayTraceResult(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original) {
//    	return this.rayTrace(world, pos, start, end);
//    }

    @Nullable
    public BlockRayTraceResult rayTrace(IBlockReader world, BlockPos pos, PlayerEntity player) {
        Vec3d start = player.getPositionVector().add(0, player.getEyeHeight(), 0);
        double reachDistance = 5;
        
//        if (player instanceof EntityPlayerMP)
//            reachDistance = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();

        Vec3d end = start.add(player.getLookVec().normalize().scale(reachDistance));
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public BlockRayTraceResult rayTrace(IBlockReader world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof ISEGenericWire))
            return RayTraceHelper.computeTrace(null, pos, start, end, VoxelShapes.fullCube().getBoundingBox(), -1);

        ISEGenericWire wireTile = (ISEGenericWire) tile;

        BlockRayTraceResult best = null;
        for (Direction wire_side:  Direction.values()) {
            boolean hasConnection = false;

            // Branches
            for (Direction to : Direction.values()) {
                if (wireTile.hasBranch(wire_side, to)) {
                    hasConnection = true;

                    boolean hasCorner = wireTile.hasBranch(to, wire_side);
                    best = RayTraceHelper.computeTrace(best, pos, start, end,
                            getBranchBoundingBox(wire_side, to, hasCorner, false),
                            (wire_side.ordinal() << 4) | to.ordinal());

                    if (hasCorner)
                        best = RayTraceHelper.computeTrace(best, pos, start, end,
                                getBranchBoundingBox(wire_side, to, false, true),
                                0x80 | (wire_side.ordinal() << 4) | to.ordinal());
                }
            }

            // Center
            if (hasConnection)
                best = RayTraceHelper.computeTrace(best, pos, start, end, getBranchBoundingBox(wire_side,null, false, false), (wire_side.ordinal() << 4) | 7);
        }

        return best;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        TileEntity te = world.getTileEntity(pos);

        VoxelShape vs = VoxelShapes.empty();
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
                vs = VoxelShapes.combine(vs, VoxelShapes.create(min+x, y, min+z, max+x, max+y, max+z), IBooleanFunction.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.UP)) {
                hasConnection = true;
                vs = VoxelShapes.combine(vs, VoxelShapes.create(min+x, min+y, min+z, max+x, 1+y, max+z), IBooleanFunction.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.NORTH)) {
                hasConnection = true;
                vs = VoxelShapes.combine(vs, VoxelShapes.create(min+x, min+y, z, max+x, max+y, max+z), IBooleanFunction.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.SOUTH)) {
                hasConnection = true;
                vs = VoxelShapes.combine(vs, VoxelShapes.create(min+x, min+y, min+z, max+x, max+y, 1+z), IBooleanFunction.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.WEST)) {
                hasConnection = true;
                vs = VoxelShapes.combine(vs, VoxelShapes.create(x, min+y, min+z, max+x, max+y, max+z), IBooleanFunction.OR);
            }

            if (wireTile.hasBranch(wire_side, Direction.EAST)) {
                hasConnection = true;
                vs = VoxelShapes.combine(vs, VoxelShapes.create(min+x, min+y, min+z, 1+x, max+y, max+z), IBooleanFunction.OR);
            }

            if (hasConnection) {
                //Center
                vs = VoxelShapes.combine(vs, VoxelShapes.create(min+x, min+y, min+z, max+x, max+y, max+z), IBooleanFunction.OR);
            }
        }
        
		return vs;
    }

    // Was AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    @Override
    public VoxelShape getBoundingShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	VoxelShape ret = VoxelShapes.empty();
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEGenericWire))
            return ret;  //This is not supposed to happen

        ISEGenericWire wireTile = (ISEGenericWire) te;
		BlockRayTraceResult trace = rayTrace(world, pos, Essential.proxy.getClientPlayer());

        if (trace == null || trace.subHit < 0 || !pos.equals(trace.getPos())) {
            // Perhaps we aren't the object the mouse is over
            return ret;
        }

        AxisAlignedBB aabb = null;
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

            aabb = aabb.grow(0.025);
        }

        return aabb==null ? VoxelShapes.empty() : VoxelShapes.create(aabb);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGenericWire) {
            ISEGenericWire wireTile = (ISEGenericWire) te;
            wireTile.onRenderingUpdateRequested();

            int x = fromPos.getX() - pos.getX();
            int y = fromPos.getY() - pos.getY();
            int z = fromPos.getZ() - pos.getZ();
            if (x*x+y*y+z*z != 1)
                return; // the change is not send from neighbor block!!!

            TileEntity teFrom = world.getTileEntity(fromPos);
            if (teFrom instanceof ISEGenericCable)
            	return;
            
            Direction side = Direction.getFacingFromVector(x, y, z);
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
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    	if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return;

        ISEGenericWire wireTile = (ISEGenericWire) te;
        addBranch(wireTile, nextPlacedSide.get(), nextPlacedto.get(), nextPlacedItemStack.get(), world.isRemote);
        wireTile.onRenderingUpdateRequested();
    }

    ///////////////////////
    /// Item drops
    ///////////////////////
    ThreadLocal<List<ItemStack>> itemDrops = new ThreadLocal<>();
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        if (world.isRemote)
            return false;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

        ISEGenericWire wireTile = (ISEGenericWire) te;

        BlockRayTraceResult trace = this.rayTrace(world, pos, player);
        if (trace == null)
            return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

        if (subHit_isBranch(trace.subHit) && trace.getPos().equals(pos)) {
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
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return ItemStack.EMPTY;

        ISEGenericWire wireTile = (ISEGenericWire) te;

        BlockRayTraceResult trace = this.rayTrace(world, pos, player);

        if (subHit_isBranch(trace.subHit) && trace.getPos().equals(pos)) {    //Center, corner or branches
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
