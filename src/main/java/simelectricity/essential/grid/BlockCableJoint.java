package simelectricity.essential.grid;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockCableJoint extends BlockBase implements IMetaProvider<ITileMeta>, ISEHVCableConnector {	
	public static enum Type implements ITileMeta {
		_10kv(TileCableJoint.Type10kV.class),
		_415v(TileCableJoint.Type415V.class);
		
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
	
    private BlockCableJoint(Type meta) {
        super("essential_cable_joint" + meta.name(), 
        		Block.Properties.create(Material.GLASS)
        		.hardnessAndResistance(0.2F, 10.0F)
        		.sound(SoundType.METAL), SEAPI.SETab);
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
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    	builder.add(DirHorizontal8.prop);
	}

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
	
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    	try {
			return meta.teCls().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	return makeCuboidShape(0.0D, 0.0D, 0.0D, 15.0D, 15.0D, 15.0D);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	PlayerEntity placer = context.getPlayer();
        return this.getDefaultState().with(DirHorizontal8.prop, DirHorizontal8.fromSight(placer));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGridTile) {
        	
        	int numOfConductor = te instanceof TileCableJoint.Type10kV ? 3 : 4;
        	SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, numOfConductor));
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
        if (te instanceof ISEGridTile)
            SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGridTile)
            return (ISEGridTile) te;
        else
            return null;
    }
}
