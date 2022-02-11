package simelectricity.essential.cable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import rikka.librikka.IMetaProvider;
import rikka.librikka.RayTraceHelper;
import rikka.librikka.MarkedBlockHitResult;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.item.ItemBlockBase;
import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.common.CoverPanelUtils;
import simelectricity.essential.utils.SEUnitHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * RayTrace is inspired by BuildCraft
 *
 * @author Rikka0_0
 */
public class BlockCable extends BlockBase implements EntityBlock, ICustomBoundingBox, IMetaProvider<ISECableMeta>, SimpleWaterloggedBlock {
    ///////////////////////////////
    /// Cable Properties
    ///////////////////////////////
    public static enum CableTypes implements ISECableMeta {
    	copper_thin(0.22F, 0.05F),
    	copper_medium(0.32F, 0.005F),
    	copper_thick(0.42F, 0.0005F),
    	aluminum_thin(0.22F, 0.075F),
    	aluminum_medium(0.32F, 0.0075F),
    	aluminum_thick(0.42F, 0.00075F),
    	silver_thin(0.22F, 0.04F),
    	silver_medium(0.32F, 0.004F),
    	silver_thick(0.42F, 0.0004F),
    	gold_thin(0.22F, 0.02F),
    	gold_medium(0.32F, 0.002F),
    	gold_thick(0.42F, 0.0002F),
    	;

    	private final float thickness;
    	private final float resistivity;
    	CableTypes(float thickness, float resistivity) {
    		this.thickness = thickness;
    		this.resistivity = resistivity;
    	}

    	public float thickness() {
    		return this.thickness;
    	}

    	public float resistivity() {
    		return this.resistivity;
    	}
    }

	private BlockCable(ISECableMeta cableData) {
        this("cable",
        		cableData,
        		BlockBehaviour.Properties.of(Material.GLASS).strength(0.2F, 10.0F).sound(SoundType.METAL).noOcclusion()
        		.isRedstoneConductor((a,b,c)->false)
        		, ItemBlockBase::new,
        		(new Item.Properties()).tab(SEAPI.SETab),
        		Essential.beTypeOf(TileCable.class)::get);
    }

    public static BlockCable[] create() {
    	BlockCable[] ret = new BlockCable[CableTypes.values().length];
    	for (ISECableMeta cableData: CableTypes.values()) {
    		ret[cableData.ordinal()] = new BlockCable(cableData);
    	}
    	return ret;
    }


    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    private final ISECableMeta cableData;

	@Override
	public ISECableMeta meta() {
		return cableData;
	}

    private final Supplier<BlockEntityType<? extends TileCable>> beType;
    protected BlockCable(String name, ISECableMeta cableData, BlockBehaviour.Properties props, ItemBlockBase.Constructor itemBlockProvider,
    		Item.Properties itemProps, Supplier<BlockEntityType<? extends TileCable>> beType) {
    	// variableOpacity tells Minecraft not to cache any BlockStats
        super(name+"_"+cableData.name(), props.dynamicShape(), itemBlockProvider, itemProps);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        this.cableData = cableData;
        this.beType = beType;

        //Calc. collision boxes and cache them
        float min = 0.5F - cableData.thickness() / 2F;
        float max = 0.5F + cableData.thickness() / 2F;

        for (Direction side: Direction.values()) {
        	cableBoundingBoxes[side.ordinal()] = RayTraceHelper.createAABB(side, min, 0, min, max, min, max);
        }
        cableBoundingBoxes[6] = new AABB(min, min, min, max, max, max);

        brancheShapes[Direction.DOWN.ordinal()] = Shapes.box(min, 0, min, max, max, max);
        brancheShapes[Direction.UP.ordinal()] = Shapes.box(min, min, min, max, 1, max);
        brancheShapes[Direction.NORTH.ordinal()] = Shapes.box(min, min, 0, max, max, max);
        brancheShapes[Direction.SOUTH.ordinal()] = Shapes.box(min, min, min, max, max, 1);
        brancheShapes[Direction.WEST.ordinal()] = Shapes.box(0, min, min, max, max, max);
        brancheShapes[Direction.EAST.ordinal()] = Shapes.box(min, min, min, 1, max, max);
        brancheShapes[Direction.values().length] = Shapes.box(min, min, min, max, max, max);
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
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

    @Override
    // @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    	tooltip.add(new TextComponent(
    			I18n.get("gui.simelectricity.resistivity") + ": " +
    			SEUnitHelper.getStringWithoutUnit(2F*cableData.resistivity()) + "\u03a9/m"
    			));
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        TileCable cable;
        try {
            cable = beType.get().create(pos, state);
            // if (world instanceof ServerLevel && !((World)world).isClientSide)    //createTileEntity is only called by the server thread when the block is placed at the first
            cable.setResistanceOnPlace(this.cableData.resistivity());
            return cable;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

//    @Override
//    public boolean isSolidSide(BlockGetter world, BlockPos pos, Direction side) {
//        BlockEntity tile = world.getTileEntity(pos);
//
//        return tile instanceof ISEGenericCable && ((ISEGenericCable) tile).getCoverPanelOnSide(side) != null;
//    }

//    @Deprecated
//    public BlockFaceShape getBlockFaceShape(BlockGetter worldIn, IBlockState state, BlockPos pos, Direction face)
//    {
//        return isSideSolid(state, worldIn, pos, face) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
//    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof TileCable)
            return ((TileCable) te).lightLevel;

        return 0;
    }

    //////////////////////////////////
    ///CollisionBoxes
    //////////////////////////////////
    protected final static AABB[] coverPanelBoundingBoxes;

    static {
    	coverPanelBoundingBoxes = new AABB[6];
    	for (Direction side: Direction.values()){
    		coverPanelBoundingBoxes[side.ordinal()] = RayTraceHelper.createAABB(side, 0, 0, 0, 1, ISECoverPanel.thickness, 1);
    	}
    }

    /**
     * @param side null for center
     * @param meta
     * @return
     */
    private final AABB[] cableBoundingBoxes = new AABB[Direction.values().length+1];
    private final VoxelShape[] brancheShapes = new VoxelShape[Direction.values().length+1];

    public AABB getBoundingBox(ISEGenericCable cable, boolean ignoreCoverPanel) {
        double x1, y1, z1, x2, y2, z2;
        x1 = 0.5 - cableData.thickness() / 2;
        y1 = x1;
        z1 = x1;
        x2 = 0.5 + cableData.thickness() / 2;
        y2 = x2;
        z2 = x2;

        //Branches
        if (cable.connectedOnSide(Direction.DOWN))
            y1 = 0;

        if (cable.connectedOnSide(Direction.UP))
            y2 = 1;

        if (cable.connectedOnSide(Direction.NORTH))
            z1 = 0;

        if (cable.connectedOnSide(Direction.SOUTH))
            z2 = 1;

        if (cable.connectedOnSide(Direction.WEST))
            x1 = 0;

        if (cable.connectedOnSide(Direction.EAST))
            x2 = 1;

        if (!ignoreCoverPanel) {
        	//Cover panel
	        if (cable.getCoverPanelOnSide(Direction.DOWN) != null) {
	        	x1=0;
	        	y1=0;
	        	z1=0;
	        	x2=1;
	        	z2=1;
	        }

	        if (cable.getCoverPanelOnSide(Direction.UP) != null) {
	        	x1=0;
	        	z1=0;
	        	x2=1;
	        	y2=1;
	        	z2=1;
	        }

	        if (cable.getCoverPanelOnSide(Direction.NORTH) != null) {
	        	x1=0;
	        	y1=0;
	        	z1=0;
	        	x2=1;
	        	y2=1;
	        }

	        if (cable.getCoverPanelOnSide(Direction.SOUTH) != null) {
	        	x1=0;
	        	y1=0;
	        	x2=1;
	        	y2=1;
	        	z2=1;
	        }

	        if (cable.getCoverPanelOnSide(Direction.WEST) != null) {
	        	x1=0;
	        	y1=0;
	        	z1=0;
	        	y2=1;
	        	z2=1;
	        }

	        if (cable.getCoverPanelOnSide(Direction.EAST) != null) {
	        	y1=0;
	        	z1=0;
	        	x2=1;
	        	y2=1;
	        	z2=1;
	        }
        }

        return new AABB(x1, y1, z1, x2, y2, z2);
    }

    // Was addCollisionBoxToList
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // Center
      	VoxelShape vs = brancheShapes[6];

    	BlockEntity te = world.getBlockEntity(pos);
    	if (!(te instanceof ISEGenericCable))
    		return vs;	// First placement

    	ISEGenericCable cable = (ISEGenericCable) te;

    	for (Direction dir: Direction.values())
    		if (cable.connectedOnSide(dir))
    			vs = Shapes.joinUnoptimized(vs, brancheShapes[dir.ordinal()], BooleanOp.OR);

      //Cover panel
      if (cable.getCoverPanelOnSide(Direction.DOWN) != null)
    	  vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 0, 0, 1, ISECoverPanel.thickness, 1), BooleanOp.OR);

      if (cable.getCoverPanelOnSide(Direction.UP) != null)
    	  vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 1 - ISECoverPanel.thickness, 0, 1, 1, 1), BooleanOp.OR);

      if (cable.getCoverPanelOnSide(Direction.NORTH) != null)
    	  vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 0, 0, 1, 1, ISECoverPanel.thickness), BooleanOp.OR);

      if (cable.getCoverPanelOnSide(Direction.SOUTH) != null)
    	  vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 0, 1 - ISECoverPanel.thickness, 1, 1, 1), BooleanOp.OR);

      if (cable.getCoverPanelOnSide(Direction.WEST) != null)
    	  vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 0, 0, ISECoverPanel.thickness, 1, 1), BooleanOp.OR);

      if (cable.getCoverPanelOnSide(Direction.EAST) != null)
    	  vs = Shapes.joinUnoptimized(vs, Shapes.box(1 - ISECoverPanel.thickness, 0, 0, 1, 1, 1), BooleanOp.OR);

    	return vs;
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

        AttributeInstance attrib = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attrib != null)
        	reachDistance = attrib.getValue();

        Vec3 end = start.add(player.getLookAngle().normalize().scale(reachDistance));
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public MarkedBlockHitResult<Integer> rayTrace(BlockGetter world, BlockPos pos, Vec3 start, Vec3 end) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (!(tile instanceof ISEGenericCable))
            return MarkedBlockHitResult.rayTrace(pos, start, end, Shapes.block().bounds(), 400);

        ISEGenericCable cable = (ISEGenericCable) tile;

        //Cable center & branches
        //Start form center
        MarkedBlockHitResult<Integer> best = MarkedBlockHitResult.rayTrace(pos, start, end, cableBoundingBoxes[Direction.values().length], 0);
        for (Direction side : Direction.values()) {
            if (cable.connectedOnSide(side))
                best = MarkedBlockHitResult.iterate(best, pos, start, end, cableBoundingBoxes[side.ordinal()], side.ordinal() + 1);
        }

        //CoverPanel
        for (Direction side : Direction.values()) {
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                best = MarkedBlockHitResult.iterate(best, pos, start, end, coverPanelBoundingBoxes[side.ordinal()], side.ordinal() + 1 + 6);
            }
        }

        if (best == null)
            return MarkedBlockHitResult.rayTrace(pos, start, end, Shapes.block().bounds(), 400);

        //subhit: 0: center, 123456 branches, 789 10 11 12 coverpanel
        return best;
    }

    // Was AABB getSelectedBoundingBox(IBlockState state, Level world, BlockPos pos)
    @Override
    public VoxelShape getBoundingShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	VoxelShape ret = Shapes.empty();
//		for (AABB aabb: getShape(state, world, pos, CollisionContext.dummy()).toBoundingBoxList()) {
//			ret = Shapes.combine(ret, Shapes.create(aabb.grow(0.025)), BooleanOp.OR);
//		}
    	BlockEntity te = world.getBlockEntity(pos);

		if (!(te instanceof ISEGenericCable))
			return ret; // This is not supposed to happen

		ISEGenericCable cable = (ISEGenericCable) te;
		MarkedBlockHitResult<Integer> trace = this.rayTrace(world, pos, Essential.proxy.getClientPlayer());

		AABB aabb = null;
		if (trace == null || trace.subHit < 0 || !pos.equals(trace.getBlockPos())) {
			// Perhaps we aren't the object the mouse is over
			aabb = cableBoundingBoxes[6];
		} else {
			if (trace.subHit > 6 && trace.subHit < 13) { // CoverPanel
				aabb = coverPanelBoundingBoxes[trace.subHit - 7].expandTowards(0.01, 0.01, 0.01);
			} else if (trace.subHit > -1 && trace.subHit < 7) { // Center or branches
				aabb = getBoundingBox(cable, true).inflate(0.025);
			}
		}

		return aabb==null ? Shapes.empty() : Shapes.create(aabb);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult ray) {
        BlockEntity te = world.getBlockEntity(pos);

        if (!(te instanceof ISEGenericCable))
            return InteractionResult.FAIL;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;

        ItemStack itemStack = player.getMainHandItem();
        if (itemStack == null || itemStack.isEmpty())
            return CoverPanelUtils.openCoverPanelGui(cable, player);	// Empty hand

        // We have an item on the player's main hand
        Item item = itemStack.getItem();
		if (item instanceof DyeItem) {
			DyeColor color = ((DyeItem) item).getDyeColor();
			if (!player.isCreative())
				itemStack.shrink(1);

			if (!world.isClientSide)
				cable.setColor(color.ordinal());

			return InteractionResult.SUCCESS;
		}

        // Attempt to install cover panel, check panel type
        ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromItemStack(itemStack);
        if (coverPanel instanceof ISEFacadeCoverPanel
        		&&((ISEFacadeCoverPanel)coverPanel).getBlockState().isAir())
        	return InteractionResult.FAIL;

        if (CoverPanelUtils.installCoverPanel(state, world, pos, player, handIn, ray) == InteractionResult.SUCCESS)
            return InteractionResult.SUCCESS;

        return CoverPanelUtils.openCoverPanelGui(cable, player);	// Fail to install cover panel
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide)
            return;

        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onRenderingUpdateRequested();
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isClientSide)
            return;

        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onRenderingUpdateRequested();
    }

    ///////////////////////
    /// Item drops
    ///////////////////////
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof ISEGenericCable))
			return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);

		if (CoverPanelUtils.removeCoverPanel((ISECoverPanelHost)te, player))
			return false;

		return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return ItemStack.EMPTY;

        ISECoverPanelHost host = (ISECoverPanelHost) te;
        Direction side = host.getSelectedCoverPanel(player);

        return side==null ?
        		super.getCloneItemStack(state, target, world, pos, player) :
        		host.getCoverPanelOnSide(side).getDroppedItemStack();
    }

    ///////////////////////
    ///Redstone
    ///////////////////////
    // The more sensitive version of vanilla isSignalSource
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof ISEGenericCable) {
            ISEGenericCable cable = (ISEGenericCable) te;
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side.getOpposite());

            return coverPanel instanceof ISERedstoneEmitterCoverPanel;
        }

        return false;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof ISEGenericCable) {
            ISEGenericCable cable = (ISEGenericCable) te;
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side.getOpposite());

            return coverPanel instanceof ISERedstoneEmitterCoverPanel
                    ? ((ISERedstoneEmitterCoverPanel) coverPanel).isProvidingWeakPower() ? 15 : 0
                    : 0;
        }

        return 0;
    }
}
