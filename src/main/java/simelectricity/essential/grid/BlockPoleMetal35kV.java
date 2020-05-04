package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaBase;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.multiblock.BlockMapping;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockPoleMetal35kV extends BlockBase implements ICustomBoundingBox, ISEHVCableConnector {
	public final MultiBlockStructure structureTemplate;
	public final MultiBlockStructure structureTemplate45;
	public final static EnumProperty<Type> propType = EnumProperty.create("type", Type.class);
	
	public static enum Type implements IMetaBase, IStringSerializable {
		pole, 
		collisionbox_full,
		collisionbox_half_xn,
		collisionbox_half_xp,
		collisionbox_half_zn,
		collisionbox_half_zp,
		collisionbox_quarter_q,
		collisionbox_quarter_w,
		collisionbox_quarter_s,
		collisionbox_quarter_a,
		host;

		@Override
		public String getName() {
			return name();
		}
	}
	
	private BlockPoleMetal35kV(int type) {
		super("essential_pole_metal_35kv_" + String.valueOf(type), Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(0.2F, 10.0F)
        		.sound(SoundType.METAL), 
        		ItemBlock.class, 
        		(new Item.Properties()).group(SEAPI.SETab));
		
		this.structureTemplate = this.createStructureTemplate();
		this.structureTemplate45 = this.createStructureTemplate45();
	}

	public static BlockPoleMetal35kV[] create() {
		return new BlockPoleMetal35kV[] {new BlockPoleMetal35kV(0), new BlockPoleMetal35kV(1)};
	}
	
	public final static Vec3i hostOffset = new Vec3i(5, 18, 2);
	public final static Vec3i hostOffset45 = new Vec3i(4, 18, 4);
    protected MultiBlockStructure createStructureTemplate() {
        //y,z,x facing NORTH(Z-), do not change
        BlockMapping[][][] configuration = new BlockMapping[19][][];
        
        BlockMapping p = blockMappingFromType(Type.pole);
        BlockMapping cf = blockMappingFromType(Type.collisionbox_full);
        BlockMapping cxn = blockMappingFromType(Type.collisionbox_half_xn);
        BlockMapping cxp = blockMappingFromType(Type.collisionbox_half_xp);
        BlockMapping czn = blockMappingFromType(Type.collisionbox_half_zn);
        BlockMapping czp = blockMappingFromType(Type.collisionbox_half_zp);
        BlockMapping cq = blockMappingFromType(Type.collisionbox_quarter_q);
        BlockMapping cw = blockMappingFromType(Type.collisionbox_quarter_w);
        BlockMapping cs = blockMappingFromType(Type.collisionbox_quarter_s);
        BlockMapping ca = blockMappingFromType(Type.collisionbox_quarter_a);
        BlockMapping h = blockMappingFromType(Type.host);
        //  .-->x+ (East)
        //  |                           Facing/Looking at North(z-)
        // \|/
        //  z+ (South)
        configuration[0] = new BlockMapping[][]{
        	{null, null, null, p   , null, null, null, p   , null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, p   , null, null, null, p   , null, null, null},
        };
        
        for (int i=1; i<18; i++)
        configuration[i] = new BlockMapping[][]{
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null, null, null},
        };
        
        configuration[18] = new BlockMapping[][]{
        	{null, null, null, null, null, null, null, null, null, null, null},
        	{cq  , czn , czn , czn , czn , czn , czn , czn , czn , czn , cw},
        	{cxn , cf  , cf  , cf  , cf  , h   , cf  , cf  , cf  , cf  , cxp},
        	{ca  , czp , czp , czp , czp , czp , czp , czp , czp , czp , cs},
        	{null, null, null, null, null, null, null, null, null, null, null},
        };

        
        return new MultiBlockStructure(configuration);
    }

    protected MultiBlockStructure createStructureTemplate45() {
        //y,z,x facing NORTH(Z-), do not change
        BlockMapping[][][] configuration = new BlockMapping[19][][];
        
        BlockMapping p = blockMappingFromType45(Type.pole);
        BlockMapping c = blockMappingFromType45(Type.collisionbox_full);
        BlockMapping h = blockMappingFromType45(Type.host);
        //  .-->x+ (East)
        //  |                           Facing/Looking at North(z-)
        // \|/
        //  z+ (South)
        configuration[0] = new BlockMapping[][]{
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, p   , null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, p   , null, null, null, null, null, p   , null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, p   , null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        };
        
        for (int i=1; i<18; i++)
        configuration[i] = new BlockMapping[][]{
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        	{null, null, null, null, null, null, null, null, null},
        };
        
        configuration[18] = new BlockMapping[][]{
        	{null, c   , null, null, null, null, null, null, null},
        	{c   , c   , c   , null, null, null, null, null, null},
        	{null, c   , c   , c   , null, null, null, null, null},
        	{null, null, c   , c   , c   , null, null, null, null},
        	{null, null, null, c   , h   , c   , null, null, null},
        	{null, null, null, null, c   , c   , c   , null, null},
        	{null, null, null, null, null, c   , c   , c   , null},
        	{null, null, null, null, null, null, c   , c   , c   },
        	{null, null, null, null, null, null, null, c   , null},
        };

        
        return new MultiBlockStructure(configuration);
    }
    
    private BlockMapping blockMappingFromType(Type type) {
    	BlockState toState = this.getDefaultState().with(propType, type);
    	final Block blockThis = this;
    	
    	return new BlockMapping(toState) {
    		@Override
    	    protected boolean cancelPlacement(BlockState state) {
    			return !state.isAir();
    		}
    		
    		@Override
    		protected boolean cancelRestore(BlockState state) {
    			return state.getBlock() != blockThis;
    		}
    		
    		@Override
    	    protected BlockState getStateForPlacement(Direction facing) {
    	    	return super.getStateForPlacement(facing).with(DirHorizontal8.prop, DirHorizontal8.fromDirection4(facing));
    	    }
    	};
    }
    
    private BlockMapping blockMappingFromType45(Type type) {
    	BlockState toState = this.getDefaultState().with(propType, type);
    	final Block blockThis = this;
    	
    	return new BlockMapping(toState) {
    		@Override
    	    protected boolean cancelPlacement(BlockState state) {
    			return !state.isAir();
    		}
    		
    		@Override
    		protected boolean cancelRestore(BlockState state) {
    			return state.getBlock() != blockThis;
    		}
    		
    		@Override
    	    protected BlockState getStateForPlacement(Direction facing) {
    	    	return super.getStateForPlacement(facing).with(DirHorizontal8.prop, DirHorizontal8.fromDirection4(facing).clockwise());
    	    }
    	};
    }
    
    public static DirHorizontal8 getFacing(BlockState blockstate) {
    	return blockstate.get(DirHorizontal8.prop);
    }
    

    /**
     * Standing at center, looking at facing
     * return	meaning (when is45 is true)<p>
     * 0		Upper-Right<p>
     * 1		Lower-Right<p>
     * 2		Lower-Left<p>
     * 3		Upper-Left<p>
     * 
     * @param xOffset
     * @param zOffset
     * @param xCenter
     * @param zCenter
     * @return the relative position to the center block
     */
    public static int getPartId(int xOffset, int zOffset, int xCenter, int zCenter) {
    	int partId = -1;
    	
		if (xOffset < xCenter) {
			if (zOffset < zCenter) {
				partId = 3;
			} else {
				partId = 2;
			}
		} else {
			if (zOffset < zCenter) {
				partId = 0;
			} else {
				partId = 1;
			}
		}
		
		return partId;
    }
    
    public static int getPartId45(int xOffset, int zOffset, int xCenter, int zCenter) {
    	int partId = -1;
    	
		if (xOffset == xCenter) {
			if (zOffset < zCenter) {
				partId = 3;
			} else {
				partId = 1;
			}
		} else {
			if (xOffset < xCenter) {
				partId = 2;
			} else {
				partId = 0;
			}
		}
		
		return partId;
    }
 
    /**
     * @param blockstate
     * @param xOffset the x of a given block in the blueprint
     * @param zOffset the z of a given block in the blueprint
     * @param isOnAxis the default facing of the blueprint is N rather than NE
     * @return the relative position to the center block
     */
    public static int getPartId(DirHorizontal8 facing, int xOffset, int zOffset) {
		return facing.isOnAxis() ? 
				getPartId(xOffset, zOffset, BlockPoleMetal35kV.hostOffset.getX(), BlockPoleMetal35kV.hostOffset.getZ()) :
				getPartId45(xOffset, zOffset, BlockPoleMetal35kV.hostOffset45.getX(), BlockPoleMetal35kV.hostOffset45.getZ());
    }
    
	
    @Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DirHorizontal8.prop, propType);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		Type type = state.get(propType);
		if (type == Type.host)
			return new TilePoleMetal35kV();
		else if (type == Type.pole)
			return new TilePoleMetal35kV.Bottom();
		else
			return new TileMultiBlockPlaceHolder();
	}
	
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && !world.isRemote) {
        	DirHorizontal8 facing = state.get(DirHorizontal8.prop);
        	if (facing.isOnAxis())
        		this.structureTemplate.restoreStructure(te, state, true);
        	else
        		this.structureTemplate45.restoreStructure(te, state, true);
        }
        
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
    
    ///////////////////
    /// BoundingBoxes
    ///////////////////
    private final static VoxelShape[] poleShapes = new VoxelShape[4];
    private final static VoxelShape[] poleShapes45 = new VoxelShape[4];
    
    private static void initPoleShapes() {
		double x1 = 0.4, y1 = 0, z1 = 0;
		double x2 = 1, y2 = 0.8, z2 = 0.6;

		poleShapes[0] = VoxelShapes.create(x1, y1, z1, x2, y2, z2);
		poleShapes[1] = VoxelShapes.create(x1, y1, 1 - z2, x2, y2, 1 - z1);
		poleShapes[2] = VoxelShapes.create(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
		poleShapes[3] = VoxelShapes.create(1 - x2, y1, z1, 1 - x1, y2, z2);
    }
    
    private static void initPoleShapes45() {
		double x1 = 0.4, y1 = 0, z1 = 0.2;
		double x2 = 1, y2 = 0.8, z2 = 0.8;
		
		poleShapes45[0] = VoxelShapes.create(x1, y1, z1, x2, y2, z2);
		poleShapes45[1] = VoxelShapes.create(z1, y1, x1, z2, y2, x2);
		poleShapes45[2] = VoxelShapes.create(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
		poleShapes45[3] = VoxelShapes.create(1 - z2, y1, 1 - x2, 1 - z1, y2, 1 - x1);
    }
    
    static {
    	initPoleShapes();
    	initPoleShapes45();
    }
    
    @Override
    public VoxelShape getBoundingShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	Type type = state.get(propType);
    	DirHorizontal8 facing = getFacing(state);
    	TileEntity te = world.getTileEntity(pos);
    	
    	if (!(te instanceof IMultiBlockTile))
    		return VoxelShapes.empty();
    	
    	MultiBlockTileInfo mbInfo = ((IMultiBlockTile) te).getMultiBlockTileInfo();
    	
        VoxelShape vs = VoxelShapes.fullCube();

        if (type == Type.pole) {
        	if (facing.isOnAxis()) {
        		BlockPos centerPos = mbInfo.getPartPos(hostOffset);
        		int partId = getPartId(pos.getX(), pos.getZ(), centerPos.getX(), centerPos.getZ());
        		vs = poleShapes[partId];
        	} else {
        		BlockPos centerPos = mbInfo.getPartPos(hostOffset45);
        		int partId = getPartId45(pos.getX(), pos.getZ(), centerPos.getX(), centerPos.getZ());
        		vs = poleShapes45[partId];    		
        	}
        }
        
        return vs;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	Type type = state.get(propType);
        VoxelShape vs = getBoundingShape(state, world, pos, context);
        
        
        return vs;
    }
    
    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
    
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		Type type = state.get(propType);
		return type==Type.pole||type==Type.host ? BlockRenderType.MODEL:BlockRenderType.INVISIBLE;
	}
    
    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        
        if (te instanceof ISEGridTile)
        	return (ISEGridTile) te;
        else if (te instanceof IMultiBlockTile) {
        	DirHorizontal8 facing = world.getBlockState(pos).get(DirHorizontal8.prop);
        	BlockPos hostPos = ((IMultiBlockTile) te).getMultiBlockTileInfo()
        			.getPartPos(facing.isOnAxis()?hostOffset:hostOffset45);
        	TileEntity host = world.getTileEntity(hostPos);
        	
        	if (host instanceof ISEGridTile)
        		return (ISEGridTile) host;
        }
        
        return null;
    }
    
    //////////////////////////////////////
    /// BlockItem
    //////////////////////////////////////
    public static class ItemBlock extends ItemBlockBase {
		public ItemBlock(Block block, Properties props) {
			super(block, props);
		}

		@Override
		protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
			World world = context.getWorld();
			BlockPos pos = context.getPos();
			DirHorizontal8 facing = DirHorizontal8.fromSight(context.getPlayer());
			BlockPoleMetal35kV block = (BlockPoleMetal35kV) this.getBlock();
			
			MultiBlockStructure.Result result = null;
			if (facing.isOnAxis())
				result = block.structureTemplate.attempToBuild(world, pos, facing.toDirection4());
			else
				result = block.structureTemplate45.attempToBuild(world, pos, facing.anticlockwise().toDirection4());
			
			if (result == null)
				return false;
			
			if (!world.isRemote)
				result.createStructure();
			return true;
		}
    }
}
