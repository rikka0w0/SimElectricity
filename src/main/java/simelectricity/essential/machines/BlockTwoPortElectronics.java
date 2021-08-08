package simelectricity.essential.machines;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.Utils;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.common.CoverPanelUtils;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.tile.*;
import simelectricity.essential.utils.RedstoneHelper;

import javax.annotation.Nullable;

public abstract class BlockTwoPortElectronics extends SEMachineBlock implements IMetaProvider<ITileMeta> {
    public static enum MetaInfo implements ITileMeta {
    	adjustable_transformer(TileAdjustableTransformer.class),
    	current_sensor(TileCurrentSensor.class),
    	diode(TileDiode.class),
    	circuit_breaker(TileSwitch.class),
    	relay(TileRelay.class),
    	power_meter(TilePowerMeter.class);

		MetaInfo(Class<? extends BlockEntity> teCls) {
			this.teCls = teCls;
		}

		public final Class<? extends BlockEntity> teCls;

		@Override
		public final Class<? extends BlockEntity> teCls() {
			return teCls;
		}
    }

	private final ITileMeta meta;
	@Override
	public final ITileMeta meta() {
		return meta;
	}

    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    public BlockTwoPortElectronics(ITileMeta meta) {
        super("electronics2_" + meta.name());
    	this.meta = meta;
    }

    public static BlockTwoPortElectronics[] create() {
    	BlockTwoPortElectronics[] ret = new BlockTwoPortElectronics[MetaInfo.values().length];

    	for (ITileMeta meta: MetaInfo.values()) {
    		ret[meta.ordinal()] = new BlockTwoPortElectronics(meta) {
    			@Override
    			public boolean hasSecondState() {
    				return meta == MetaInfo.circuit_breaker;
    			}

    		    @Override
    		    public boolean useObjModel() {
    				return false;
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

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
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

        if (te instanceof TileSwitch) {
        	TileSwitch tileSwitch = (TileSwitch) te;
        	if (tileSwitch.getFacing() == rtResult.getDirection()) {
        		if (tileSwitch.isOn) {
        			world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F, false);
        		} else {
        			world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F, false);
        		}
        		if (!world.isClientSide)
        			tileSwitch.setSwitchStatus(!tileSwitch.isOn);
            	return InteractionResult.SUCCESS;
        	}
        }

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
        if (te instanceof SETwoPortMachine) {
            Direction sight = Utils.getPlayerSight(placer);

            if (te instanceof TileSwitch)
                ((SETwoPortMachine<?>) te).setFunctionalSide(Direction.UP, Direction.DOWN);
            else
                ((SETwoPortMachine<?>) te).setFunctionalSide(sight.getOpposite(), sight);
        }
    }

    ///////////////////////
    ///Redstone
    ///////////////////////
    @Override
    public boolean isSignalSource(BlockState state) {
        return meta == MetaInfo.current_sensor;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        BlockEntity te = world.getBlockEntity(pos);

        return !(te instanceof TileCurrentSensor);
    }

    /*
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        BlockEntity te = world.getBlockEntity(pos);

        return te instanceof TileCurrentSensor;
    }*/

    @Override
    public int getSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof TileCurrentSensor)
            return ((TileCurrentSensor) te).emitRedstoneSignal ? 15 : 0;

        return 0;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide) {
            super.neighborChanged(state, world, pos, block, fromPos, isMoving);
            return;
        }

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileRelay) {
            boolean isPowered = RedstoneHelper.isBlockPowered(world, pos, 4);
            ((TileRelay) te).setSwitchStatus(isPowered);
        }

        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }
}
