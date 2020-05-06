package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.IMetaBase;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.multiblock.BlockMapping;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockStructure;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockPoleConcrete35kV extends BlockBase implements ICustomBoundingBox, ISEHVCableConnector {
	public final MultiBlockStructure structureTemplate;
	public final static EnumProperty<Type> propType = EnumProperty.create("type", Type.class);
	
	public static enum Type implements IMetaBase, IStringSerializable {
		pole, collisionbox, pole_collisionbox, host;

		@Override
		public String getName() {
			return name();
		}
	}
	
	private BlockPoleConcrete35kV(int type) {
		super("pole_concrete_35kv_" + String.valueOf(type), 
				Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(0.2F, 10.0F)
        		.sound(SoundType.METAL), 
        		ItemBlock.class, 
        		(new Item.Properties()).group(SEAPI.SETab));
		
		this.structureTemplate = this.createStructureTemplate();
	}

	public static BlockPoleConcrete35kV[] create() {
		return new BlockPoleConcrete35kV[] {new BlockPoleConcrete35kV(0), new BlockPoleConcrete35kV(1)};
	}
	
	public final static Vec3i hostOffset = new Vec3i(5, 11, 0);
    protected MultiBlockStructure createStructureTemplate() {
        //y,z,x facing NORTH(Z-), do not change
        BlockMapping[][][] configuration = new BlockMapping[15][][];
        
        BlockMapping p = blockMappingFromType(Type.pole);
        BlockMapping c = blockMappingFromType(Type.collisionbox);
        BlockMapping pc = blockMappingFromType(Type.pole_collisionbox);
        BlockMapping h = blockMappingFromType(Type.host);
        //  .-->x+ (East)
        //  |                           Facing/Looking at North(z-)
        // \|/
        //  z+ (South)
        for (int i=0; i<11; i++) {
	        configuration[i] = new BlockMapping[][]{
	        {null, null,  p, null, null, null, null, null, p , null, null}};
        }
        configuration[11] = new BlockMapping[][]{
        	{c   , c   , pc, c   , c   , h   , c   , c   , pc, c   , c}
        };
        for (int i=12; i<15; i++) {
	        configuration[i] = new BlockMapping[][]{
	        {null, null,  p, null, null, null, null, null, p , null, null}};
        }
        
        return new MultiBlockStructure(configuration);
    }
    
    private BlockMapping blockMappingFromType(Type type) {
    	BlockState toState = this.getDefaultState().with(propType, type);
    	final Block blockThis = this;
    	
    	return new BlockMapping(Blocks.AIR.getDefaultState(), toState) {
    		@Override
    	    protected boolean cancelPlacement(BlockState state) {
    			return state==null ? false : !state.isAir();
    		}
    		
    		@Override
    		protected boolean cancelRestore(BlockState state) {
    			return state.getBlock() != blockThis;
    		}
    		
    		@Override
    	    protected BlockState getStateForPlacement(Direction facing) {
    	    	return super.getStateForPlacement(facing).with(BlockStateProperties.HORIZONTAL_FACING, facing);
    	    }
    	    
    		@Override
    	    protected BlockState getStateForRestore(Direction facing) {
    	    	return super.getStateForRestore(facing);//.with(BlockStateProperties.HORIZONTAL_FACING, facing);
    	    }
    	};
    }
	
    @Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, propType);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		Type type = state.get(propType);
		return type == Type.host ? new TilePoleConcrete35kV() : new TileMultiBlockPlaceHolder();
	}
	
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && !world.isRemote) {
            this.structureTemplate.restoreStructure(te, state, true);
        }
        
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
    
    ///////////////////
    /// BoundingBoxes
    ///////////////////
    @Override
    public VoxelShape getBoundingShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	Type type = state.get(propType);
        int facing = state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalIndex();
        VoxelShape vs = VoxelShapes.empty();

        if (type == Type.pole || type == Type.pole_collisionbox) {
			if (type == Type.pole_collisionbox)
				if (facing == 0 || facing == 2)
					return VoxelShapes.create(0, 0, 0.125F, 1, 1, 0.875F);
				else
					return VoxelShapes.create(0.125F, 0, 0, 0.875F, 1, 1);
			else
				return VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
        } else {
            if (facing == 0 || facing == 2)
                return VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F);
            else
                return VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	Type type = state.get(propType);
        int facing = state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalIndex();
        VoxelShape vs = VoxelShapes.empty();

        if (type == Type.pole || type == Type.pole_collisionbox) {        	
            vs = VoxelShapes.combine(vs, VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F), IBooleanFunction.OR);

			if (type == Type.pole_collisionbox) {
				if (facing == 0 || facing == 2)
					vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F), IBooleanFunction.OR);
				else
					vs = VoxelShapes.combine(vs, VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1), IBooleanFunction.OR);
			}
        } else {
            if (facing == 0 || facing == 2)
                vs = VoxelShapes.combine(vs, VoxelShapes.create(0, 0, 0.125F, 1, 0.25F, 0.875F), IBooleanFunction.OR);
            else
                vs = VoxelShapes.combine(vs, VoxelShapes.create(0.125F, 0, 0, 0.875F, 0.25F, 1), IBooleanFunction.OR);
        }
        
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
		return state.get(propType) == Type.collisionbox ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
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
        	BlockPos hostPos = ((IMultiBlockTile) te).getMultiBlockTileInfo().getPartPos(hostOffset);
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
			Direction facing = Utils.getPlayerSightHorizontal(context.getPlayer());
			BlockPoleConcrete35kV block = (BlockPoleConcrete35kV) this.getBlock();
			
			MultiBlockStructure.Result result = block.structureTemplate.attempToBuild(world, pos, facing);
			if (result == null)
				return false;
			
			if (!world.isRemote)
				result.createStructure();
			return true;
		}
    }
}
