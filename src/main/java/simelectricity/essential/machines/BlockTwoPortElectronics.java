package simelectricity.essential.machines;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
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
    	
		MetaInfo(Class<? extends TileEntity> teCls) {
			this.teCls = teCls;
		}

		public final Class<? extends TileEntity> teCls;
		
		@Override
		public final Class<? extends TileEntity> teCls() {
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		try {
			return meta.teCls().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rtResult) {
    	if (CoverPanelUtils.installCoverPanel(state, world, pos, player, hand, rtResult) == ActionResultType.SUCCESS)
    		return ActionResultType.SUCCESS;
  
    	TileEntity te = world.getTileEntity(pos);
    	if (te instanceof ISECoverPanelHost) {
    		ISECoverPanel coverPanel = ((ISECoverPanelHost) te).getCoverPanelOnSide(rtResult.getFace());
    		if (coverPanel != null && !coverPanel.isHollow())
                return ActionResultType.PASS; 
    	}
    	
    	if (player.isCrouching())
            return ActionResultType.PASS;
                
        if (te instanceof TileSwitch) {
        	TileSwitch tileSwitch = (TileSwitch) te;
        	if (tileSwitch.getFacing() == rtResult.getFace()) {
        		if (tileSwitch.isOn) {
        			world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F, false);
        		} else {
        			world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F, false);
        		}
        		if (!world.isRemote)
        			tileSwitch.setSwitchStatus(!tileSwitch.isOn);
            	return ActionResultType.SUCCESS;
        	}
        }
        
        if (te instanceof INamedContainerProvider) {
        	player.openContainer((INamedContainerProvider) te);
        	return ActionResultType.SUCCESS;
        }
        

        return ActionResultType.PASS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity te = world.getTileEntity(pos);
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
    public boolean canProvidePower(BlockState state) {
        return meta == MetaInfo.current_sensor;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        TileEntity te = world.getTileEntity(pos);

        return !(te instanceof TileCurrentSensor);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        TileEntity te = world.getTileEntity(pos);

        return te instanceof TileCurrentSensor;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileCurrentSensor)
            return ((TileCurrentSensor) te).emitRedstoneSignal ? 15 : 0;

        return 0;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (world.isRemote) {
            super.neighborChanged(state, world, pos, block, fromPos, isMoving);
            return;
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileRelay) {
            boolean isPowered = RedstoneHelper.isBlockPowered(world, pos, 4);
            ((TileRelay) te).setSwitchStatus(isPowered);
        }
        
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }
}
