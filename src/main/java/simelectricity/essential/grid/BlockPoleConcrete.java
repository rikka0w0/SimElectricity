package simelectricity.essential.grid;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISEHVCableConnector;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockPoleConcrete extends BlockBase implements IMetaProvider<BlockPoleConcrete.Type>, EntityBlock, ISEHVCableConnector {
	public enum Type implements ITileMeta {
		pole(null, 0),
		crossarm10kvt0(TilePoleConcrete.Pole10Kv.Type0.class,3),
		crossarm10kvt1(TilePoleConcrete.Pole10Kv.Type1.class, 3),
		crossarm415vt0(TilePoleConcrete.Pole415vType0.class, 4),
		branching10kv(TilePoleBranch.Type10kV.class, 3),
		branching415v(TilePoleBranch.Type415V.class, 4);

		public final Class<? extends BlockEntity> teCls;
		public final Supplier<BlockEntityType<?>> beType;
	    public final int numOfConductor;

		Type(Class<? extends BlockEntity> teCls, int numOfConductor) {
			this.teCls = teCls;
			this.beType = Essential.beTypeOf(teCls)::get;
			this.numOfConductor = numOfConductor;
		}

		@Override
		public Class<? extends BlockEntity> teCls() {
			return this.teCls;
		}

		@Override
		public BlockEntityType<?> beType() {
			return beType.get();
		}

		public static Type forName(String name) {
			for (Type type: Type.values()) {
				if (type.name().toLowerCase().equals(name.toLowerCase()))
					return type;
			}
			return null;
		}
	}

	public static BlockPoleConcrete[] create() {
		BlockPoleConcrete[] ret = new BlockPoleConcrete[Type.values().length];
		for (Type blockType: Type.values()) {
			ret[blockType.ordinal()] = new BlockPoleConcrete(blockType);
		}
		return ret;
	}

    public final Type blockType;
	@Override
	public Type meta() {
		return blockType;
	}

    private BlockPoleConcrete(Type blockType) {
        super("pole_concrete_" + blockType.name(),
        		BlockBehaviour.Properties.of(Material.STONE)
        		.strength(3F, 10F)
        		.sound(SoundType.METAL)
        		.isRedstoneConductor((a,b,c)->false),
        		SEAPI.SETab);
        this.blockType = blockType;
    }
    ///////////////////////////////
    /// BlockEntity
    ///////////////////////////////
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    	try {
			return blockType.create(pos, state);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    	builder.add(DirHorizontal8.prop);
	}

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
    	Player placer = context.getPlayer();
        return this.defaultBlockState().setValue(DirHorizontal8.prop, DirHorizontal8.fromSight(placer));
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);

		if (!world.isClientSide) {
			// TODO: CHECK!
			SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, blockType.numOfConductor));

			world.sendBlockUpdated(pos, state, state, 2);
		}
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity te = world.getBlockEntity(pos);    //Do this before the tileEntity is removed!
        if (!world.isClientSide && te instanceof ISEGridTile) {
        	ISEGridNode node = ((ISEGridTile) te).getGridNode();
        	if (node != null)
                SEAPI.energyNetAgent.detachGridNode(world, node);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    ///////////////////
    /// BoundingBox
    ///////////////////
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	return Shapes.box(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
    }
}
