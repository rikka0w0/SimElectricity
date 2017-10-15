package simelectricity.essential.grid.transformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.multiblock.BlockMapping;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import rikka.librikka.properties.Properties;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockDistributionTransformer extends BlockAbstractTransformer implements ISEHVCableConnector {
	private static final String[] subNames = EnumDistributionTransformerBlockType.getRawStructureNames();	
	public BlockDistributionTransformer() {
		super("essential_disttransformer", Material.IRON);
	}

    @Override
    public String[] getSubBlockUnlocalizedNames() {
        return this.subNames;
    }
    
    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
    	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);
    	
        if (!blockType.formed)
            return null;
        
        switch (blockType) {
		case Pole415V:
			return new TileDistributionTransformer.Pole415V();
		case Pole10kV:
			return new TileDistributionTransformer.Pole10kV();
		case PlaceHolder:
			return new TileDistributionTransformer.PlaceHolder();
		default:
			break;
        }

    	return null;
    }
    
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, 
        		EnumDistributionTransformerBlockType.property, 
        		EnumDistributionTransformerRenderPart.property, 
        		BlockHorizontal.FACING, Properties.propertyMirrored);
    }
    
    @Override
    protected IBlockState getBaseState(IBlockState firstValidState) {
		return firstValidState.withProperty(Properties.propertyMirrored, false);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	IBlockState state;
    	EnumDistributionTransformerBlockType blockType = EnumDistributionTransformerBlockType.fromInt(meta & 7);
    	boolean mirrored = (meta & 8) > 0;
    	
    	state = this.stateFromType(blockType);
    	if (!blockType.formed)
    		state = state.withProperty(Properties.propertyMirrored, mirrored);

    	
    	return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
    	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);
    	int meta = blockType.index;
    	if (state.getValue(Properties.propertyMirrored) && !blockType.formed)
    		meta |= 8;
    	
        return meta;
    }

    public IBlockState stateFromType(EnumDistributionTransformerBlockType blockType) {
        return getDefaultState().withProperty(EnumDistributionTransformerBlockType.property, blockType);
    }
    
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
    	state = super.getActualState(state, world, pos);
    	
    	if (state.getValue(EnumDistributionTransformerBlockType.property).formed) {
    		TileEntity te = world.getTileEntity(pos);
    		if (te instanceof IMultiBlockTile) {
    			EnumDistributionTransformerRenderPart renderPart = MultiBlockTileInfo.lookup((IMultiBlockTile)te, renderParts);
    			if (renderPart == null)
    		    	return state;
    			
    			state = state.withProperty(EnumDistributionTransformerRenderPart.property, renderPart);
    		}
    	}
    	
    	return state;
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    	IBlockState state = stateFromType(EnumDistributionTransformerBlockType.fromInt(meta&7));
    	EnumFacing sight = Utils.getPlayerSightHorizontal(placer);
    	return state.withProperty(Properties.propertyMirrored, facing2mirrored(sight));
    }
    
    private boolean facing2mirrored(EnumFacing facing) {
    	return facing==EnumFacing.EAST || facing==EnumFacing.WEST;
    }
    
    @Override
	protected ItemStack getItemToDrop(IBlockState state) {
    	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);

        if (blockType.formed)
        	return ItemStack.EMPTY;
            
        return new ItemStack(this.itemBlock, 1, getMetaFromState(stateFromType(blockType)));
    }
    
    ///////////////////
    /// BoundingBox
    ///////////////////
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
    	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);
    	
    	if (blockType == EnumDistributionTransformerBlockType.Transformer)
    		return Block.FULL_BLOCK_AABB;
    	
    	if (blockType.formed) {
    		state = this.getActualState(state, world, pos);
    		EnumDistributionTransformerRenderPart part = state.getValue(EnumDistributionTransformerRenderPart.property);
    		if (part == EnumDistributionTransformerRenderPart.TransformerLeft || part == EnumDistributionTransformerRenderPart.TransformerRight)
    			return Block.FULL_BLOCK_AABB;
    	}
    	
    	return new AxisAlignedBB(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
    }

    ///////////////////
    /// ISEHVCableConnector
    ///////////////////
	@Override
	public ISEGridTile getGridTile(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileDistributionTransformer ? (TileDistributionTransformer)te : null;
	}
	
    ///////////////////////////////
    /// MultiBlock
    ///////////////////////////////
    private EnumDistributionTransformerRenderPart[][][] renderParts;
    
    private BlockMapping createBlockMapping(EnumDistributionTransformerBlockType in, EnumDistributionTransformerBlockType out) {
    	return new BlockMapping(stateFromType(in), stateFromType(out)) {
    		@Override
    	    protected boolean isDifferent(IBlockState state) {
    			if (state.getBlock() != this.state.getBlock())
    				return true;
    			
    	    	return this.state.getValue(EnumDistributionTransformerBlockType.property) != state.getValue(EnumDistributionTransformerBlockType.property);
    		}
    		
    		@Override
    		protected IBlockState getStateForRestore(@Nullable TileEntity tileEntity) {
    			if (tileEntity instanceof IMultiBlockTile) {
    				IMultiBlockTile tile = (IMultiBlockTile) tileEntity;
    				EnumFacing facing = tile.getMultiBlockTileInfo().facing;
    				return state.withProperty(Properties.propertyMirrored, !facing2mirrored(facing));
    			}
    			return state;
    		}
    	};
    }
    
    @Override
    protected MultiBlockStructure createStructureTemplate() {
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
        	{EnumDistributionTransformerRenderPart.AuxLeft, null, null,EnumDistributionTransformerRenderPart.AuxMiddle, null, EnumDistributionTransformerRenderPart.AuxLeft.AuxRight}
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
        
        
        
        return new MultiBlockStructure(configuration);
    }
    
    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return true;
    }
    
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }
    
    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
}
