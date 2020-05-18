package simelectricity.essential.machines;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.Utils;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.common.CoverPanelUtils;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.tile.*;

public abstract class BlockElectronics extends SEMachineBlock implements IMetaProvider<ITileMeta> {
	public static enum Type implements ITileMeta {
		voltage_meter(TileVoltageMeter.class),
		quantum_generator(TileQuantumGenerator.class),
		adjustable_resistor(TileAdjustableResistor.class),
		incandescent_lamp(TileIncandescentLamp.class),
		electric_furnace(TileElectricFurnace.class),
		transformer_se2rf(TileSE2RF.class),
		transformer_rf2se(TileRF2SE.class);
		
		Type(Class<? extends TileEntity> teCls) {
			this.teCls = teCls;
		}

		public final Class<? extends TileEntity> teCls;
		
		@Override
		public final Class<? extends TileEntity> teCls() {
			return teCls;
		}
	}

	private final Type meta;
	@Override
	public final ITileMeta meta() {
		return meta;
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
    			
    		    @Override
    		    public boolean useObjModel() {
    				return meta == Type.transformer_se2rf || 
    						meta == Type.transformer_rf2se;
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

    private final static VoxelShape vsXfmRFSE_NS = VoxelShapes.or(
    		VoxelShapes.create(0.2, 0, 0, 0.8, 1, 1), 
    		VoxelShapes.create(0, 0.2, 0, 0.2, 0.8, 1), 
    		VoxelShapes.create(0.8, 0.2, 0, 1, 0.8, 1)
    		);
    private final static VoxelShape vsXfmRFSE_WE = VoxelShapes.or(
    		VoxelShapes.create(0, 0, 0.2, 1, 1, 0.8), 
    		VoxelShapes.create(0, 0.2, 0, 1, 0.8, 0.2), 
    		VoxelShapes.create(0, 0.2, 0.8, 1, 0.8, 1)
    		);
    private final static VoxelShape vsXfmRFSE_DU = VoxelShapes.or(
    		VoxelShapes.create(0, 0, 0.2, 1, 1, 0.8), 
    		VoxelShapes.create(0.2, 0, 0, 0.8, 1, 0.8), 
    		VoxelShapes.create(0.2, 0, 0.8, 0.8, 1, 1)
    		);
    
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (this.useObjModel()) {
			Direction.Axis axis = state.get(BlockStateProperties.FACING).getAxis();
			return axis == Direction.Axis.Z ? vsXfmRFSE_NS : 
				(axis == Direction.Axis.X ? vsXfmRFSE_WE : vsXfmRFSE_DU);
		}
		return VoxelShapes.fullCube();
	}
    
    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileIncandescentLamp) {
            return ((TileIncandescentLamp) te).lightLevel;
        }
        return 0;
    }

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
        
        if (meta == Type.incandescent_lamp)
            return ActionResultType.PASS;    //Incandescent Lamp doesn't have an Gui!

        if (te instanceof INamedContainerProvider) {
        	player.openContainer((INamedContainerProvider) te);
        	return ActionResultType.SUCCESS;
        }
        
        return ActionResultType.PASS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);

        if (te instanceof SESinglePortMachine) {
            Direction sight = Utils.getPlayerSight(placer);
            ((SESinglePortMachine) te).setFacing(sight.getOpposite());

            if (sight == Direction.UP && te instanceof TileSolarPanel)
                sight = Direction.DOWN;

            ((SESinglePortMachine) te).SetFunctionalSide(sight);
        }
    }
}
