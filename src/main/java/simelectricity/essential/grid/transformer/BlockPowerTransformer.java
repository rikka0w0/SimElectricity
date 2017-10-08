package simelectricity.essential.grid.transformer;

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
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.properties.Properties;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Primary;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Render;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Secondary;

public class BlockPowerTransformer extends BlockAbstractTransformer implements ISEHVCableConnector {
    private static final String[] subNames = EnumPowerTransformerBlockType.getRawStructureNames();

    public BlockPowerTransformer() {
        super("essential_powertransformer", Material.IRON);
    }

    @Override
    public String[] getSubBlockUnlocalizedNames() {
        return this.subNames;
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        EnumPowerTransformerBlockType blockType = state.getValue(EnumPowerTransformerBlockType.property);

        if (!blockType.formed)
            return null;

        switch (blockType) {
            case Placeholder:
                return new TilePowerTransformerPlaceHolder();
            case PlaceholderPrimary:
                return new Primary();
            case PlaceholderSecondary:
                return new Secondary();
            case Primary:
                return new TilePowerTransformerWinding.Primary();
            case Secondary:
                return new TilePowerTransformerWinding.Secondary();
            case Render:
                return new Render();
            default:
                return null;
        }
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EnumPowerTransformerBlockType.property, BlockHorizontal.FACING, Properties.propertyMirrored);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.stateFromType(EnumPowerTransformerBlockType.fromInt(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(EnumPowerTransformerBlockType.property).index;
    }
       
    public IBlockState stateFromType(EnumPowerTransformerBlockType blockType) {
        return getDefaultState().withProperty(EnumPowerTransformerBlockType.property, blockType);
    }
    
	@Override
	protected ItemStack getItemToDrop(IBlockState state) {
        EnumPowerTransformerBlockType blockType = state.getValue(EnumPowerTransformerBlockType.property);

        if (blockType.formed)
        	return ItemStack.EMPTY;
            
        return new ItemStack(this.itemBlock, 1, this.getMetaFromState(state));
	}
    
    @Override
    protected MultiBlockStructure createStructureTemplate() {
        //y,z,x facing NORTH(Z-), do not change
        MultiBlockStructure.BlockInfo[][][] configuration = new MultiBlockStructure.BlockInfo[5][][];

        MultiBlockStructure.BlockInfo core2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.IronCore), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        MultiBlockStructure.BlockInfo coil2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Winding), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        MultiBlockStructure.BlockInfo support2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.OilTankSupport), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        MultiBlockStructure.BlockInfo pipe2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.OilPipe), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        MultiBlockStructure.BlockInfo tank2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.OilTank), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        MultiBlockStructure.BlockInfo casing2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        MultiBlockStructure.BlockInfo casing2PHpri = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.PlaceholderPrimary));
        MultiBlockStructure.BlockInfo casing2PHsec = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.PlaceholderSecondary));
        MultiBlockStructure.BlockInfo casing2pri = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Primary));
        MultiBlockStructure.BlockInfo casing2sec = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Secondary));
        MultiBlockStructure.BlockInfo casing2render = new MultiBlockStructure.BlockInfo(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Render));


        //  .-->x+ (East)
        //  |                           Facing/Looking at North(x-)
        // \|/
        //  z+ (South)
        configuration[0] = new MultiBlockStructure.BlockInfo[][]{
                {null, casing2PHpri, casing2PHpri, null, casing2PHpri, casing2PHpri, null},
                {casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri},
                {casing2PH, casing2PH, casing2PH, casing2PH, casing2PH, casing2PH, casing2PH},
                {casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec},
                {null, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, null}
        };

        configuration[1] = new MultiBlockStructure.BlockInfo[][]{
                {null, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, null},
                {casing2PHpri, coil2PH, coil2PH, coil2PH, coil2PH, coil2PH, casing2PHpri},
                {casing2PH, coil2PH, core2PH, core2PH, core2PH, coil2PH, casing2PH},
                {casing2PHsec, coil2PH, coil2PH, coil2PH, coil2PH, coil2PH, casing2PHsec},
                {null, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, null}
        };

        configuration[2] = new MultiBlockStructure.BlockInfo[][]{
                {null, casing2PHpri, casing2PHpri, null, casing2PHpri, casing2PHpri, null},
                {casing2PHpri, casing2PHpri, casing2PHpri, casing2pri, casing2PHpri, casing2PHpri, casing2PHpri},
                {casing2PH, casing2PH, casing2PH, casing2render, casing2PH, casing2PH, casing2PH},
                {casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2sec, casing2PHsec, casing2PHsec},
                {support2PH, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, null}
        };

        configuration[3] = new MultiBlockStructure.BlockInfo[][]{
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {support2PH, null, null, null, null, null, null},
                {null, pipe2PH, null, null, null, null, null},
                {support2PH, null, null, null, null, null, null}
        };

        configuration[4] = new MultiBlockStructure.BlockInfo[][]{
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {tank2PH, null, null, null, null, null, null},
                {tank2PH, pipe2PH, null, null, null, null, null},
                {tank2PH, null, null, null, null, null, null}
        };

        return new MultiBlockStructure(configuration);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof Primary)
            return ((Primary) te).getWinding();
        else if (te instanceof Secondary)
            return ((Secondary) te).getWinding();
        else if (te instanceof TilePowerTransformerWinding)
            return (TilePowerTransformerWinding) te;

        return null;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        EnumPowerTransformerBlockType blockType = blockState.getValue(EnumPowerTransformerBlockType.property);
        return !blockType.formed;
        //return true;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
