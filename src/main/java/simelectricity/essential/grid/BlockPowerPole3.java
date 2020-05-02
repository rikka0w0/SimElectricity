package simelectricity.essential.grid;

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

public class BlockPowerPole3 extends BlockBase implements IMetaProvider<ITileMeta>, ISEHVCableConnector {
	public static BlockPowerPole3[] create() {
		BlockPowerPole3[] ret = new BlockPowerPole3[EnumBlockTypePole3.values().length];
		for (EnumBlockTypePole3 blockType: EnumBlockTypePole3.values()) {
			ret[blockType.ordinal()] = new BlockPowerPole3(blockType);
		}
		return ret;
	}
    
    private final EnumBlockTypePole3 blockType;
	@Override
	public ITileMeta meta() {
		return blockType;
	}
	
    private BlockPowerPole3(EnumBlockTypePole3 blockType) {
        super("essential_powerpole3_" + blockType.name(), 
        		Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(3F, 10F)
        		.sound(SoundType.METAL), 
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

		TileEntity te = world.getTileEntity(pos);
		if (!world.isRemote) {
			// TODO: CHECK!
			SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, blockType.numOfConductor));

			world.notifyBlockUpdate(pos, state, state, 2);
		}
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
        if (te instanceof ISEGridTile) {
        	ISEGridNode node = ((ISEGridTile) te).getGridNode();
        	if (node != null)
                SEAPI.energyNetAgent.detachGridNode(world, node);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        // TODO: Fix BlockPowerPole3::neighborChanged
//    	if (!worldIn.isRemote) { //Server only!
//            if (!canPlaceBlockAt(worldIn, pos)) {
//				dropBlockAsItem(worldIn, pos, state, 0);
//                worldIn.setBlockToAir(pos);
//            }
//        }
    }

    ///////////////////
    /// BoundingBox
    ///////////////////
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	return VoxelShapes.create(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
}
