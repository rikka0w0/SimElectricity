package simelectricity.essential.grid.transformer;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import rikka.librikka.multiblock.MultiBlockStructure;
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
        return new BlockStateContainer(this, EnumDistributionTransformerBlockType.property, BlockHorizontal.FACING, Properties.propertyMirrored);
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
	protected ItemStack getItemToDrop(IBlockState state) {
    	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);

        if (blockType.formed)
        	return ItemStack.EMPTY;
            
        return new ItemStack(this.itemBlock, 1, this.getMetaFromState(state));
    }
    
    private MultiBlockStructure.BlockInfo createBlockMapping(EnumDistributionTransformerBlockType in, EnumDistributionTransformerBlockType out) {
    	return new MultiBlockStructure.BlockInfo(stateFromType(in), stateFromType(out));
    }
    
    @Override
    protected MultiBlockStructure createStructureTemplate() {
        //y,z,x facing NORTH(Z-), do not change
        MultiBlockStructure.BlockInfo[][][] configuration = new MultiBlockStructure.BlockInfo[7][][];
        
        MultiBlockStructure.BlockInfo a = createBlockMapping(EnumDistributionTransformerBlockType.Pole10kVNormal, EnumDistributionTransformerBlockType.Pole10kV);
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
        
        return new MultiBlockStructure(configuration);
    }
}
