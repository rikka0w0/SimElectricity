package simelectricity.essential.grid.transformer;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import rikka.librikka.properties.Properties;

public class BlockDistributionTransformer extends BlockAbstractTransformer{
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
			break;
		case Pole415VNormal:
			break;
		case Primary10kV:
			break;
		case Secondary415V:
			break;
		case Transformer:
			break;
		default:
			break;
        }
    	
    	return new TileDistributionTransformerPole();
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
    public IBlockState getStateFromMeta(int meta) {
        return this.stateFromType(EnumDistributionTransformerBlockType.fromInt(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(EnumDistributionTransformerBlockType.property).index;
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
	protected ItemStack getItemToDrop(IBlockState state) {
    	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);

        if (blockType.formed)
        	return ItemStack.EMPTY;
            
        return new ItemStack(this.itemBlock, 1, this.getMetaFromState(state));
    }
    
   
    ///////////////////////////////
    /// MultiBlock
    ///////////////////////////////
    private EnumDistributionTransformerRenderPart[][][] renderParts;
    
    private MultiBlockStructure.BlockInfo createBlockMapping(EnumDistributionTransformerBlockType in, EnumDistributionTransformerBlockType out) {
    	return new MultiBlockStructure.BlockInfo(stateFromType(in), stateFromType(out));
    }
    
    @Override
    protected MultiBlockStructure createStructureTemplate() {
        //y,z,x facing NORTH(Z-), do not change
        MultiBlockStructure.BlockInfo[][][] configuration = new MultiBlockStructure.BlockInfo[7][][];
        
        MultiBlockStructure.BlockInfo a = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVNormal, EnumDistributionTransformerBlockType.Pole10kV);
//        MultiBlockStructure.BlockInfo a = new MultiBlockStructure.BlockInfo(BlockRegistry.powerPole3.getStateFromMeta(EnumBlockTypePole3.Crossarm10kVT1.ordinal()),
//        																	stateFromType(EnumDistributionTransformerBlockType.Pole10kV));
        MultiBlockStructure.BlockInfo b = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVSpec, EnumDistributionTransformerBlockType.Primary10kV);
        MultiBlockStructure.BlockInfo c = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVAux, EnumDistributionTransformerBlockType.PlaceHolder);
    	
        MultiBlockStructure.BlockInfo d = createBlockMapping(EnumDistributionTransformerBlockType.Pole415VNormal, EnumDistributionTransformerBlockType.Pole415V);
        MultiBlockStructure.BlockInfo e = createBlockMapping(EnumDistributionTransformerBlockType.Pole415VNormal, EnumDistributionTransformerBlockType.Secondary415V);
        
        MultiBlockStructure.BlockInfo f = createBlockMapping(EnumDistributionTransformerBlockType.Transformer, EnumDistributionTransformerBlockType.PlaceHolder);
        MultiBlockStructure.BlockInfo g = createBlockMapping(EnumDistributionTransformerBlockType.Transformer, EnumDistributionTransformerBlockType.PlaceHolder);
        //  .-->x+ (East)
        //  |                           Facing/Looking at North(x-)
        // \|/
        //  z+ (South)
        configuration[0] = new MultiBlockStructure.BlockInfo[][]{
        	{null, f   , g   , null, null, null}
        };
        
        configuration[1] = new MultiBlockStructure.BlockInfo[][]{
        	{null, null, null, null, null, null}
        };
        
        configuration[2] = new MultiBlockStructure.BlockInfo[][]{
        	{c   , null, null,    c, null, c}
        };
        
        configuration[3] = new MultiBlockStructure.BlockInfo[][]{
        	{null, null, null, null, null, null}
        };
        
        configuration[4] = new MultiBlockStructure.BlockInfo[][]{
        	{d   , null, null, null, null, e}
        };
        
        configuration[5] = new MultiBlockStructure.BlockInfo[][]{
        	{null, null, null, null, null, null}
        };
        
        configuration[6] = new MultiBlockStructure.BlockInfo[][]{
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
