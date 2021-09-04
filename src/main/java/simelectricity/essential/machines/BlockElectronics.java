package simelectricity.essential.machines;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.Utils;
import rikka.librikka.blockentity.ITickableBlockEntity;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.common.CoverPanelUtils;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.tile.*;

public abstract class BlockElectronics extends SEMachineBlock implements IMetaProvider<ITileMeta> {
	public static enum Type implements ITileMeta {
		voltage_meter(TileVoltageMeter.class, false),
		quantum_generator(TileQuantumGenerator.class, false),
		adjustable_resistor(TileAdjustableResistor.class, true),
		incandescent_lamp(TileIncandescentLamp.class, false),
		electric_furnace(TileElectricFurnace.class, true),
		transformer_se2rf(TileSE2RF.class, true),
		transformer_rf2se(TileRF2SE.class, true);

		Type(Class<? extends BlockEntity> teCls, boolean tickable) {
			this.teCls = teCls;
			this.beType = Essential.beTypeOf(teCls)::get;
			this.tickable = tickable;
		}

		public final boolean tickable;
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

    @Override
    public boolean useObjModel() {
		return meta == Type.transformer_se2rf ||
				meta == Type.transformer_rf2se;
    }

    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    private BlockElectronics(Type meta) {
        super("electronics_" + meta.name());
        this.meta = meta;
    }

    public static BlockElectronics[] create() {
    	BlockElectronics[] ret = new BlockElectronics[Type.values().length];

    	for (Type meta: Type.values()) {
    		ret[meta.ordinal()] = new BlockElectronics(meta) {
    			@Override
    			public boolean hasSecondState() {
    				return meta == Type.incandescent_lamp ||
    						meta == Type.electric_furnace;
    			}
    		};
    	}

    	return ret;
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    	try {
			return meta.create(pos, state);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> beType) {
		return meta.tickable && !level.isClientSide() ? ITickableBlockEntity::serverTicker : null;
	}

    private final static VoxelShape vsXfmRFSE_NS = Shapes.or(
    		Shapes.box(0.2, 0, 0, 0.8, 1, 1),
    		Shapes.box(0, 0.2, 0, 0.2, 0.8, 1),
    		Shapes.box(0.8, 0.2, 0, 1, 0.8, 1)
    		);
    private final static VoxelShape vsXfmRFSE_WE = Shapes.or(
    		Shapes.box(0, 0, 0.2, 1, 1, 0.8),
    		Shapes.box(0, 0.2, 0, 1, 0.8, 0.2),
    		Shapes.box(0, 0.2, 0.8, 1, 0.8, 1)
    		);
    private final static VoxelShape vsXfmRFSE_DU = Shapes.or(
    		Shapes.box(0, 0, 0.2, 1, 1, 0.8),
    		Shapes.box(0.2, 0, 0, 0.8, 1, 0.8),
    		Shapes.box(0.2, 0, 0.8, 0.8, 1, 1)
    		);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (this.useObjModel()) {
			Direction.Axis axis = state.getValue(BlockStateProperties.FACING).getAxis();
			return axis == Direction.Axis.Z ? vsXfmRFSE_NS :
				(axis == Direction.Axis.X ? vsXfmRFSE_WE : vsXfmRFSE_DU);
		}
		return Shapes.block();
	}

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof TileIncandescentLamp) {
            return ((TileIncandescentLamp) te).lightLevel;
        }
        return 0;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rtResult) {
    	if (CoverPanelUtils.installCoverPanel(state, world, pos, player, hand, rtResult) == InteractionResult.SUCCESS)
    		return InteractionResult.SUCCESS;

    	BlockEntity te = world.getBlockEntity(pos);
    	if (te instanceof ISECoverPanelHost) {
    		ISECoverPanel coverPanel = ((ISECoverPanelHost) te).getCoverPanelOnSide(rtResult.getDirection());
    		if (coverPanel != null && !coverPanel.isHollow())
                return InteractionResult.PASS;
    	}

    	if (player.isCrouching())
            return InteractionResult.PASS;

        if (meta == Type.incandescent_lamp)
            return InteractionResult.PASS;    //Incandescent Lamp doesn't have an Gui!

        if (te instanceof MenuProvider) {
        	player.openMenu((MenuProvider) te);
        	return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof SESinglePortMachine) {
            Direction sight = Utils.getPlayerSight(placer);
            // Facing is now stored in the blockstate
            // which has been set during getStateForPlacement
//            ((SESinglePortMachine) te).setFacing(sight.getOpposite());

            if (sight == Direction.UP && te instanceof TileSolarPanel)
                sight = Direction.DOWN;

            ((SESinglePortMachine<?>) te).SetFunctionalSide(sight);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
    	BlockEntity blockEntity = level.getBlockEntity(pos);
    	if (blockEntity instanceof TileElectricFurnace) {
    		Containers.dropContents(level, pos, ((TileElectricFurnace)blockEntity).inventory);
    	}

    	return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
