package simelectricity.essential.grid.transformer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import rikka.librikka.IMetaProvider;
import rikka.librikka.Utils;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.multiblock.BlockMapping;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockDistributionTransformer extends BlockAbstractTransformer
	implements IMetaProvider<EnumDistributionTransformerBlockType>, EntityBlock, ICustomBoundingBox, ISEHVCableConnector {

	public static MultiBlockStructure blueprint;
    public static EnumDistributionTransformerRenderPart[][][] renderParts;
    public final EnumDistributionTransformerBlockType blockType;
	private BlockDistributionTransformer(EnumDistributionTransformerBlockType blockType) {
		super("transformer_10kv_415v_"+blockType.getSerializedName(), Material.METAL, blockType.formed ? null : SEAPI.SETab);
		this.blockType = blockType;
	}

    public static BlockDistributionTransformer[] create() {
    	BlockDistributionTransformer[] ret = new BlockDistributionTransformer[EnumDistributionTransformerBlockType.values().length];
    	for (final EnumDistributionTransformerBlockType type: EnumDistributionTransformerBlockType.values()) {
    		ret[type.ordinal()] = new BlockDistributionTransformer(type) {
    		    @Override
    		    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    		    	if (!type.formed)
    		    		builder.add(BlockStateProperties.HORIZONTAL_FACING);
    		    }
    		};
    	}
    	return ret;
    }

    @Override
	public EnumDistributionTransformerBlockType meta() {
		return this.blockType;
	}

	@Override
	protected MultiBlockStructure getBlueprint() {
		return blueprint;
	}

    ///////////////////////////////
    /// BlockEntity
    ///////////////////////////////
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (!blockType.formed)
            return null;

    	try {
			return blockType.getBlockEntitySupplier().create(pos, state);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    public static BlockState stateFromType(EnumDistributionTransformerBlockType blockType) {
        return BlockRegistry.distributionTransformer[blockType.ordinal()].defaultBlockState();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
    	BlockState state = super.getStateForPlacement(context);
    	Player placer = context.getPlayer();
    	Direction facing = Utils.getPlayerSightHorizontal(placer);
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
			return state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
		else
			return state;
    }

    @Override
	protected ItemStack getItemToDrop(BlockState state) {
        if (blockType.formed)
        	return ItemStack.EMPTY;

        return new ItemStack(this);
    }

    ///////////////////
    /// BoundingBox
    ///////////////////
	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	if (blockType == EnumDistributionTransformerBlockType.Transformer)
    		return Shapes.block();

    	if (blockType.formed) {
    		BlockEntity te = world.getBlockEntity(pos);
    		if (te instanceof TileDistributionTransformer) {
        		EnumDistributionTransformerRenderPart part =
        				MultiBlockTileInfo.lookup((TileDistributionTransformer) te, BlockDistributionTransformer.renderParts);
        		if (part == EnumDistributionTransformerRenderPart.TransformerLeft || part == EnumDistributionTransformerRenderPart.TransformerRight)
        			return Shapes.block();
    		}
    	}

    	return Shapes.box(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
	}

    ///////////////////
    /// ISEHVCableConnector
    ///////////////////
	@Override
	public ISEGridTile getGridTile(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		return te instanceof TileDistributionTransformer ? (TileDistributionTransformer)te : null;
	}

    ///////////////////////////////
    /// MultiBlock
    ///////////////////////////////
    private static BlockMapping createBlockMapping(EnumDistributionTransformerBlockType in, EnumDistributionTransformerBlockType out) {
    	return new BlockMapping(stateFromType(in), stateFromType(out)) {
    		@Override
    	    protected boolean cancelPlacement(BlockState state) {
    			// Ignore blockstate differences, only check for blocks
    	    	return state.getBlock() != super.getStateForRestore(null).getBlock();
    		}

    		@Override
    		protected BlockState getStateForRestore(Direction facing) {
    			BlockState state = super.getStateForRestore(facing);
    			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
    				BlockDistributionTransformer block = (BlockDistributionTransformer) state.getBlock();
    				return state.setValue(BlockStateProperties.HORIZONTAL_FACING,
    						(block.blockType == EnumDistributionTransformerBlockType.Transformer) ?
    								facing.getClockWise().getClockWise() : facing.getClockWise());
    			}
    			else {
    				return state;
    			}
    		}
    	};
    }

    public static void createBluePrint() {
        //y,z,x facing NORTH(Z-), do not change
        BlockMapping[][][] configuration = new BlockMapping[7][][];

        BlockMapping a = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVNormal, EnumDistributionTransformerBlockType.Pole10kV);
//        MultiBlockStructure.BlockInfo a = new MultiBlockStructure.BlockInfo(BlockRegistry.powerPole3.getStateFromMeta(EnumBlockTypePole3.Crossarm10kVT1.ordinal()),
//        																	stateFromType(EnumDistributionTransformerBlockType.Pole10kV));
        BlockMapping b = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVSpec, EnumDistributionTransformerBlockType.Pole10kV);
        BlockMapping c = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVAux, EnumDistributionTransformerBlockType.PlaceHolder);

        BlockMapping d = createBlockMapping(EnumDistributionTransformerBlockType.Pole415VNormal, EnumDistributionTransformerBlockType.Pole415V);
        BlockMapping e = createBlockMapping(EnumDistributionTransformerBlockType.Pole415VNormal, EnumDistributionTransformerBlockType.Pole415V);

        BlockMapping f = createBlockMapping(EnumDistributionTransformerBlockType.Transformer, EnumDistributionTransformerBlockType.PlaceHolder);
        BlockMapping g = createBlockMapping(EnumDistributionTransformerBlockType.Transformer, EnumDistributionTransformerBlockType.PlaceHolder);
        //  .-->x+ (East)
        //  |                           Facing/Looking at North(x-)
        // \|/
        //  z+ (South)
        configuration[0] = new BlockMapping[][]{
        	{null, f   , g   , null, null, null}
        };

        configuration[1] = new BlockMapping[][]{
        	{null, null, null, null, null, null}
        };

        configuration[2] = new BlockMapping[][]{
        	{c   , null, null,    c, null, c}
        };

        configuration[3] = new BlockMapping[][]{
        	{null, null, null, null, null, null}
        };

        configuration[4] = new BlockMapping[][]{
        	{d   , null, null, null, null, e}
        };

        configuration[5] = new BlockMapping[][]{
        	{null, null, null, null, null, null}
        };

        configuration[6] = new BlockMapping[][]{
        	{a   , null, null, null, null, b}
        };



        //  .-->x+ (East)
        //  |                           Facing/Looking at North(x-)
        // \|/
        //  z+ (South)
        renderParts = new EnumDistributionTransformerRenderPart[7][][];
        renderParts[0] = new EnumDistributionTransformerRenderPart[][]{
        	{null, EnumDistributionTransformerRenderPart.TransformerLeft, EnumDistributionTransformerRenderPart.TransformerRight   , null, null, null}
        };

        renderParts[1] = new EnumDistributionTransformerRenderPart[][]{
        	{null, null, null, null, null, null}
        };

        renderParts[2] = new EnumDistributionTransformerRenderPart[][]{
        	{EnumDistributionTransformerRenderPart.AuxLeft, null, null,EnumDistributionTransformerRenderPart.AuxMiddle, null, EnumDistributionTransformerRenderPart.AuxRight}
        };

        renderParts[3] = new EnumDistributionTransformerRenderPart[][]{
        	{null, null, null, null, null, null}
        };

        renderParts[4] = new EnumDistributionTransformerRenderPart[][]{
        	{EnumDistributionTransformerRenderPart.Pole415VLeft   , null, null, null, null, EnumDistributionTransformerRenderPart.Pole415VRight}
        };

        renderParts[5] = new EnumDistributionTransformerRenderPart[][]{
        	{null, null, null, null, null, null}
        };

        renderParts[6] = new EnumDistributionTransformerRenderPart[][]{
        	{EnumDistributionTransformerRenderPart.Pole10kVLeft   , null, null, null, null, EnumDistributionTransformerRenderPart.Pole10kVRight}
        };

        blueprint =  new MultiBlockStructure(configuration);
    }
}
