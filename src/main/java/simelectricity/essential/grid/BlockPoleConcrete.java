package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;

import javax.annotation.Nullable;

public class BlockPoleConcrete extends BlockBase implements IMetaProvider<BlockPoleConcrete.Type>, ISEHVCableConnector {
	public enum Type implements ITileMeta {
		pole(null, 0),
		crossarm10kvt0(TilePoleConcrete.Pole10Kv.Type0.class,3),
		crossarm10kvt1(TilePoleConcrete.Pole10Kv.Type1.class, 3),
		crossarm415vt0(TilePoleConcrete.Pole415vType0.class, 4),
		branching10kv(TilePoleBranch.Type10kV.class, 3),
		branching415v(TilePoleBranch.Type415V.class, 4);

		public final Class<? extends TileEntity> teCls;
	    public final int numOfConductor;

	    Type(Class<? extends TileEntity> teCls, int numOfConductor) {
	    	this.teCls = teCls;
	        this.numOfConductor = numOfConductor;
	    }

		@Override
		public Class<? extends TileEntity> teCls() {
			return this.teCls;
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
        		Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(3F, 10F)
        		.sound(SoundType.METAL)
        		.setOpaque((a,b,c)->false), 
        		SEAPI.SETab);
        this.blockType = blockType;
    }
    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return this.meta().teCls() != null;}
	
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {    	
    	try {
			return blockType.teCls().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    	builder.add(DirHorizontal8.prop);
	}
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	PlayerEntity placer = context.getPlayer();
        return this.getDefaultState().with(DirHorizontal8.prop, DirHorizontal8.fromSight(placer));
    }
    
    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (!world.isRemote) {
			// TODO: CHECK!
			SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, blockType.numOfConductor));

			world.notifyBlockUpdate(pos, state, state, 2);
		}
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
        if (te instanceof ISEGridTile) {
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
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	return VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
    }
}
