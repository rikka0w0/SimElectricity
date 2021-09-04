package simelectricity.essential.grid;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISEHVCableConnector;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockPoleConcrete35kV extends BlockBase implements ICustomBoundingBox, EntityBlock, ISEHVCableConnector {
	public final MultiBlockStructure structureTemplate;
	public final static EnumProperty<Type> propType = EnumProperty.create("type", Type.class);

	public static enum Type implements IMetaBase, StringRepresentable {
		pole, collisionbox, pole_collisionbox, host(true);

		Type() {
			this(false);
		}

		Type(boolean isHost) {
			Class<? extends BlockEntity> beCls = isHost ? TilePoleConcrete35kV.class : TileMultiBlockPlaceHolder.class;
			this.beType = Essential.beTypeOf(beCls)::get;
		}

		public final Supplier<BlockEntityType<?>> beType;

		@Override
		public String getSerializedName() {
			return name();
		}
	}

	private BlockPoleConcrete35kV(int type) {
		super("pole_concrete_35kv_" + String.valueOf(type),
				BlockBehaviour.Properties.of(Material.STONE)
        		.strength(0.2F, 10.0F)
        		.sound(SoundType.METAL)
        		.isRedstoneConductor((a,b,c)->false),
        		ItemBlock.class,
        		(new Item.Properties()).tab(SEAPI.SETab));

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
    	BlockState toState = this.defaultBlockState().setValue(propType, type);
    	final Block blockThis = this;

    	return new BlockMapping(Blocks.AIR.defaultBlockState(), toState) {
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
    	    	return super.getStateForPlacement(facing).setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
    	    }

    		@Override
    	    protected BlockState getStateForRestore(Direction facing) {
    	    	return super.getStateForRestore(facing);//.with(BlockStateProperties.HORIZONTAL_FACING, facing);
    	    }
    	};
    }

    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, propType);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		Type type = state.getValue(propType);
		return type.beType.get().create(pos, state);
	}

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && !world.isClientSide) {
            this.structureTemplate.restoreStructure(te, state, true);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    ///////////////////
    /// BoundingBoxes
    ///////////////////
    @Override
    public VoxelShape getBoundingShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	Type type = state.getValue(propType);
        int facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue();

        if (type == Type.pole || type == Type.pole_collisionbox) {
			if (type == Type.pole_collisionbox)
				if (facing == 0 || facing == 2)
					return Shapes.box(0, 0, 0.125F, 1, 1, 0.875F);
				else
					return Shapes.box(0.125F, 0, 0, 0.875F, 1, 1);
			else
				return Shapes.box(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
        } else {
            if (facing == 0 || facing == 2)
                return Shapes.box(0, 0, 0.125F, 1, 0.25F, 0.875F);
            else
                return Shapes.box(0.125F, 0, 0, 0.875F, 0.25F, 1);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	Type type = state.getValue(propType);
        int facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue();
        VoxelShape vs = Shapes.empty();

        if (type == Type.pole || type == Type.pole_collisionbox) {
            vs = Shapes.joinUnoptimized(vs, Shapes.box(0.375F, 0, 0.375F, 0.625F, 1, 0.625F), BooleanOp.OR);

			if (type == Type.pole_collisionbox) {
				if (facing == 0 || facing == 2)
					vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 0, 0.125F, 1, 0.25F, 0.875F), BooleanOp.OR);
				else
					vs = Shapes.joinUnoptimized(vs, Shapes.box(0.125F, 0, 0, 0.875F, 0.25F, 1), BooleanOp.OR);
			}
        } else {
            if (facing == 0 || facing == 2)
                vs = Shapes.joinUnoptimized(vs, Shapes.box(0, 0, 0.125F, 1, 0.25F, 0.875F), BooleanOp.OR);
            else
                vs = Shapes.joinUnoptimized(vs, Shapes.box(0.125F, 0, 0, 0.875F, 0.25F, 1), BooleanOp.OR);
        }

        return vs;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return state.getValue(propType) == Type.collisionbox ? RenderShape.INVISIBLE : RenderShape.MODEL;
	}

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof ISEGridTile)
        	return (ISEGridTile) te;
        else if (te instanceof IMultiBlockTile) {
        	BlockPos hostPos = ((IMultiBlockTile) te).getMultiBlockTileInfo().getPartPos(hostOffset);
        	BlockEntity host = world.getBlockEntity(hostPos);

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
		protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
			Level world = context.getLevel();
			BlockPos pos = context.getClickedPos();
			Direction facing = Utils.getPlayerSightHorizontal(context.getPlayer());
			BlockPoleConcrete35kV block = (BlockPoleConcrete35kV) this.getBlock();

			MultiBlockStructure.Result result = block.structureTemplate.attempToBuild(world, pos, facing);
			if (result == null)
				return false;

			if (!world.isClientSide)
				result.createStructure();
			return true;
		}
    }
}
