package simelectricity.essential.grid;

import java.util.function.Supplier;

import javax.annotation.Nullable;

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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISEHVCableConnector;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockCableJoint extends BlockBase implements IMetaProvider<ITileMeta>, EntityBlock, ISEHVCableConnector {
	public static enum Type implements ITileMeta {
		_10kv(TileCableJoint.Type10kV.class),
		_415v(TileCableJoint.Type415V.class);

		Type(Class<? extends BlockEntity> teCls) {
			this.teCls = teCls;
			this.beType = Essential.beTypeOf(teCls)::get;
		}

		public final Class<? extends BlockEntity> teCls;
		public final Supplier<BlockEntityType<?>> beType;

		@Override
		public final Class<? extends BlockEntity> teCls() {
			return teCls;
		}

		@Override
		public BlockEntityType<?> beType() {
			return beType.get();
		}
	}

	private final Type meta;
	@Override
	public final ITileMeta meta() {
		return meta;
	}

    private BlockCableJoint(Type meta) {
        super("cable_joint" + meta.name(),
        		BlockBehaviour.Properties.of(Material.GLASS)
        		.strength(0.2F, 10.0F)
        		.sound(SoundType.METAL)
        		.isRedstoneConductor((a,b,c)->false), SEAPI.SETab);
        this.meta = meta;
    }

    public static BlockCableJoint[] create() {
    	BlockCableJoint[] ret = new BlockCableJoint[Type.values().length];
    	for (Type meta: Type.values()) {
    		ret[meta.ordinal()] = new BlockCableJoint(meta);
    	}
    	return ret;
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    	builder.add(DirHorizontal8.prop);
	}

    ///////////////////////////////
    /// BlockEntity
    ///////////////////////////////
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    	try {
			return meta.create(pos, state);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	return box(0.0D, 0.0D, 0.0D, 15.0D, 15.0D, 15.0D);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
    	Player placer = context.getPlayer();
        return this.defaultBlockState().setValue(DirHorizontal8.prop, DirHorizontal8.fromSight(placer));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isClientSide)
            return;

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ISEGridTile) {

        	int numOfConductor = te instanceof TileCableJoint.Type10kV ? 3 : 4;
        	SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, numOfConductor));
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity te = world.getBlockEntity(pos);    //Do this before the tileEntity is removed!
        if (!world.isClientSide && te instanceof ISEGridTile) {
            SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());
        }

        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ISEGridTile)
            return (ISEGridTile) te;
        else
            return null;
    }
}
