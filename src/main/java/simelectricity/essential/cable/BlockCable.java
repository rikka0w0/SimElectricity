package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.IMetaProvider;
import rikka.librikka.RayTraceHelper;
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
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.utils.SEUnitHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * RayTrace is inspired by BuildCraft
 *
 * @author Rikka0_0
 */
public class BlockCable extends BlockBase implements ICustomBoundingBox, IMetaProvider<ISECableMeta>, IWaterLoggable {
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
        		Block.Properties.create(Material.GLASS).hardnessAndResistance(0.2F, 10.0F).sound(SoundType.METAL).notSolid()
        		, ItemBlockBase.class,
        		(new Item.Properties()).group(SEAPI.SETab),
                TileCable.class);
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

    private final Class<? extends TileCable> tileEntityClass;
    protected BlockCable(String name, ISECableMeta cableData, Block.Properties props, Class<? extends ItemBlockBase> itemBlockClass,
    		Item.Properties itemProps, Class<? extends TileCable> tileEntityClass) {
    	// variableOpacity tells Minecraft not to cache any BlockStats
        super(name+"_"+cableData.name(), props.variableOpacity(), itemBlockClass, itemProps);
        this.setDefaultState(this.getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
        this.cableData = cableData;
        this.tileEntityClass = tileEntityClass;

        //Calc. collision boxes and cache them
        float min = 0.5F - cableData.thickness() / 2F;
        float max = 0.5F + cableData.thickness() / 2F;

        for (Direction side: Direction.values()) {
        	cableBoundingBoxes[side.ordinal()] = RayTraceHelper.createAABB(side, min, 0, min, max, min, max);
        }
        cableBoundingBoxes[6] = new AxisAlignedBB(min, min, min, max, max, max);
        
        brancheShapes[Direction.DOWN.ordinal()] = VoxelShapes.create(min, 0, min, max, max, max);
        brancheShapes[Direction.UP.ordinal()] = VoxelShapes.create(min, min, min, max, 1, max);
        brancheShapes[Direction.NORTH.ordinal()] = VoxelShapes.create(min, min, 0, max, max, max);
        brancheShapes[Direction.SOUTH.ordinal()] = VoxelShapes.create(min, min, min, max, max, 1);
        brancheShapes[Direction.WEST.ordinal()] = VoxelShapes.create(0, min, min, max, max, max);
        brancheShapes[Direction.EAST.ordinal()] = VoxelShapes.create(min, min, min, 1, max, max);
        brancheShapes[Direction.values().length] = VoxelShapes.create(min, min, min, max, max, max);
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
    			SEUnitHelper.getStringWithoutUnit(2F*cableData.resistivity()) + "\u03a9/m"
    			));
    }

	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
    
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileCable cable;
        try {
            cable = tileEntityClass.getConstructor().newInstance();
            if (world instanceof ServerWorld && !((World)world).isRemote)    //createTileEntity is only called by the server thread when the block is placed at the first
                cable.setResistanceOnPlace(this.cableData.resistivity());
            return cable;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
    
//    @Override
//    public boolean isSolidSide(IBlockReader world, BlockPos pos, Direction side) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        return tile instanceof ISEGenericCable && ((ISEGenericCable) tile).getCoverPanelOnSide(side) != null;
//    }

//    @Deprecated
//    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, Direction face)
//    {
//        return isSideSolid(state, worldIn, pos, face) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
//    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileCable)
            return ((TileCable) te).lightLevel;

        return 0;
    }
    
    //////////////////////////////////
    ///CollisionBoxes
    //////////////////////////////////
    protected final static AxisAlignedBB[] coverPanelBoundingBoxes;
    
    static {
    	coverPanelBoundingBoxes = new AxisAlignedBB[6];
    	for (Direction side: Direction.values()){
    		coverPanelBoundingBoxes[side.ordinal()] = RayTraceHelper.createAABB(side, 0, 0, 0, 1, ISECoverPanel.thickness, 1);
    	}
    }
    
    /**
     * @param side null for center
     * @param meta
     * @return
     */
    private final AxisAlignedBB[] cableBoundingBoxes = new AxisAlignedBB[Direction.values().length+1];
    private final VoxelShape[] brancheShapes = new VoxelShape[Direction.values().length+1];
    
    public AxisAlignedBB getBoundingBox(ISEGenericCable cable, boolean ignoreCoverPanel) {
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
        
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }
    
    // Was addCollisionBoxToList
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        double min = 0.5 - cableData.thickness() / 2;
        double max = 0.5 + cableData.thickness() / 2;
      	
        // Center
      	VoxelShape vs = brancheShapes[6];
    	
    	TileEntity te = world.getTileEntity(pos);
    	if (!(te instanceof ISEGenericCable))
    		return vs;	// First placement
    	
    	ISEGenericCable cable = (ISEGenericCable) te;
    	
    	for (Direction dir: Direction.values())
    		if (cable.connectedOnSide(dir))
    			vs = VoxelShapes.combine(vs, brancheShapes[dir.ordinal()], IBooleanFunction.OR);
    	    	
      //Cover panel
      if (cable.getCoverPanelOnSide(Direction.DOWN) != null)
    	  vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0, 1, ISECoverPanel.thickness, 1), IBooleanFunction.OR);

      if (cable.getCoverPanelOnSide(Direction.UP) != null)
    	  vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 1 - ISECoverPanel.thickness, 0, 1, 1, 1), IBooleanFunction.OR);

      if (cable.getCoverPanelOnSide(Direction.NORTH) != null)
    	  vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0, 1, 1, ISECoverPanel.thickness), IBooleanFunction.OR);

      if (cable.getCoverPanelOnSide(Direction.SOUTH) != null)
    	  vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 1 - ISECoverPanel.thickness, 1, 1, 1), IBooleanFunction.OR);

      if (cable.getCoverPanelOnSide(Direction.WEST) != null)
    	  vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0, ISECoverPanel.thickness, 1, 1), IBooleanFunction.OR);

      if (cable.getCoverPanelOnSide(Direction.EAST) != null)
    	  vs = VoxelShapes.combine(vs, VoxelShapes.create(1 - ISECoverPanel.thickness, 0, 0, 1, 1, 1), IBooleanFunction.OR);
      
    	return vs;
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

//        if (player instanceof ServerPlayerEntity)
//            reachDistance = ((ServerPlayerEntity) player).interactionManager.getBlockReachDistance();

        Vec3d end = start.add(player.getLookVec().normalize().scale(reachDistance));
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public BlockRayTraceResult rayTrace(IBlockReader world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity tile = world.getTileEntity(pos);

        if (!(tile instanceof ISEGenericCable))
            return RayTraceHelper.computeTrace(null, pos, start, end, VoxelShapes.fullCube().getBoundingBox(), 400);

        ISEGenericCable cable = (ISEGenericCable) tile;

        BlockRayTraceResult best = null;
        //Cable center & branches
        //Start form center
        best = RayTraceHelper.computeTrace(best, pos, start, end, cableBoundingBoxes[Direction.values().length], 0);
        for (Direction side : Direction.values()) {
            if (cable.connectedOnSide(side))
                best = RayTraceHelper.computeTrace(best, pos, start, end, cableBoundingBoxes[side.ordinal()], side.ordinal() + 1);
        }

        //CoverPanel
        for (Direction side : Direction.values()) {
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                best = RayTraceHelper.computeTrace(best, pos, start, end, coverPanelBoundingBoxes[side.ordinal()], side.ordinal() + 1 + 6);
            }
        }

        if (best == null)
            return RayTraceHelper.computeTrace(null, pos, start, end, VoxelShapes.fullCube().getBoundingBox(), 400);

        //subhit: 0: center, 123456 branches, 789 10 11 12 coverpanel
        return best;
    }

    // Was AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    @Override
    public VoxelShape getBoundingShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	VoxelShape ret = VoxelShapes.empty();
//		for (AxisAlignedBB aabb: getShape(state, world, pos, ISelectionContext.dummy()).toBoundingBoxList()) {
//			ret = VoxelShapes.combine(ret, VoxelShapes.create(aabb.grow(0.025)), IBooleanFunction.OR);
//		}
    	TileEntity te = world.getTileEntity(pos);

		if (!(te instanceof ISEGenericCable))
			return ret; // This is not supposed to happen

		ISEGenericCable cable = (ISEGenericCable) te;
		BlockRayTraceResult trace = rayTrace(world, pos, Essential.proxy.getClientPlayer());

		AxisAlignedBB aabb = null;
		if (trace == null || trace.subHit < 0 || !pos.equals(trace.getPos())) {
			// Perhaps we aren't the object the mouse is over
			aabb = cableBoundingBoxes[6];
		} else {
			if (trace.subHit > 6 && trace.subHit < 13) { // CoverPanel
				aabb = coverPanelBoundingBoxes[trace.subHit - 7].expand(0.01, 0.01, 0.01);
			} else if (trace.subHit > -1 && trace.subHit < 7) { // Center or branches
				aabb = getBoundingBox(cable, true).grow(0.025);
			}
		}
		
		return aabb==null ? VoxelShapes.empty() : VoxelShapes.create(aabb);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    private ActionResultType attemptOpenCoverPanelGui(TileEntity te, PlayerEntity player) {
        if (player.isCrouching())
            return ActionResultType.FAIL;

        BlockRayTraceResult trace = this.rayTrace(te.getWorld(), te.getPos(), player);

        if (trace == null)
        	return ActionResultType.FAIL;    //This is not suppose to happen, but just in case!

        if (!trace.getPos().equals(te.getPos()))
        	return ActionResultType.FAIL;

        if (trace.subHit < 7)
        	return ActionResultType.FAIL;    //The player is looking at the cable

        if (trace.subHit > 12)
        	return ActionResultType.FAIL;    //The player is looking at somewhere else

        if (te instanceof ISECoverPanelHost) {
            ISECoverPanelHost host = (ISECoverPanelHost) te;
            Direction panelSide = Direction.byIndex(trace.subHit - 7);
            ISECoverPanel coverPanel = host.getCoverPanelOnSide(panelSide);

            // TODO: check this
            if (coverPanel instanceof ISEGuiCoverPanel) {
                player.openContainer((ISEGuiCoverPanel) coverPanel);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult ray) {
		Direction side = ray.getFace();
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEGenericCable))
            return ActionResultType.FAIL;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        
        ItemStack itemStack = player.getHeldItemMainhand();
        if (itemStack == null || itemStack.isEmpty())
            return this.attemptOpenCoverPanelGui(te, player);	// Empty hand

        // We have an item on the player's main hand
        Item item = itemStack.getItem();
		if (item instanceof DyeItem) {
			DyeColor color = ((DyeItem) item).getDyeColor();
			if (!player.isCreative())
				itemStack.shrink(1);

			if (!world.isRemote)
				cable.setColor(color.ordinal());

			return ActionResultType.SUCCESS;
		}
        
        // Check if it is an cover panel item
        ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromItemStack(itemStack);
        if (coverPanel == null)
            return this.attemptOpenCoverPanelGui(te, player);	// Other items

        // Attempt to install cover panel
        if (cable.canInstallCoverPanelOnSide(side, coverPanel)) {
            if (!player.isCreative())
                itemStack.shrink(1);

            if (coverPanel instanceof ISEFacadeCoverPanel
            		&&((ISEFacadeCoverPanel)coverPanel).getBlockState().isAir())
            	return ActionResultType.FAIL;
            
            if (!world.isRemote)    //Handle on server side
                cable.installCoverPanel(side, coverPanel);
            return ActionResultType.SUCCESS;
        }

        return this.attemptOpenCoverPanelGui(te, player);	// Fail to install cover panel
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onRenderingUpdateRequested();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onRenderingUpdateRequested();
    }

    ///////////////////////
    /// Item drops
    ///////////////////////
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof ISEGenericCable))
			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

		ISEGenericCable cable = (ISEGenericCable) te;

		BlockRayTraceResult trace = this.rayTrace(world, pos, player);
		if (trace == null)
			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
		if (!trace.getPos().equals(pos))
			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

		if (trace.subHit > 6 && trace.subHit < 13) {
			// Remove the selected cover panel
			Direction side = Direction.byIndex(trace.subHit - 7);
			ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
			cable.removeCoverPanel(coverPanel, !player.isCreative());
			return false;
		} else {
			for (Direction side : Direction.values()) {
				ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
				cable.removeCoverPanel(coverPanel, !player.isCreative());
			}

			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
		}
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return ItemStack.EMPTY;

        ISEGenericCable cable = (ISEGenericCable) te;

        BlockRayTraceResult trace = this.rayTrace(world, pos, player);
        if (!trace.getPos().equals(pos))
        	super.getPickBlock(state, target, world, pos, player);

        if (trace.subHit > 6 && trace.subHit < 13) {
            Direction side = Direction.byIndex(trace.subHit - 7);
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            return coverPanel.getDroppedItemStack();
        } else {
            return super.getPickBlock(state, target, world, pos, player);
        }
    }

    ///////////////////////
    ///Redstone
    ///////////////////////
    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof ISEGenericCable) {
            ISEGenericCable cable = (ISEGenericCable) te;
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side.getOpposite());

            return coverPanel instanceof ISERedstoneEmitterCoverPanel;
        }

        return false;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity te = world.getTileEntity(pos);

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
