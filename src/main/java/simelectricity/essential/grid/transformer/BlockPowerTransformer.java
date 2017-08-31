package simelectricity.essential.grid.transformer;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Properties;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockStructure.Result;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Primary;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Render;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Secondary;

public class BlockPowerTransformer extends BlockBase implements ITileEntityProvider, ISubBlock, ISimpleTexture, ISEHVCableConnector {
    public static final String[] subNames = EnumBlockType.getRawStructureNames();
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    public static final IProperty<Boolean> propertyMirrored = PropertyBool.create("mirrored");
    public final MultiBlockStructure structureTemplate;

    public BlockPowerTransformer() {
        super("essential_powertransformer", Material.IRON, ItemBlockBase.class);

        structureTemplate = this.createStructureTemplate();
        
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public String[] getSubBlockUnlocalizedNames() {
        return BlockPowerTransformer.subNames;
    }

    @Override
    public String getIconName(int damage) {
        return "powertransformer_" + BlockPowerTransformer.subNames[damage];
    }

    @Override
    public void beforeRegister() {
        isBlockContainer = true;
        setCreativeTab(SEAPI.SETab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        EnumBlockType blockType = EnumBlockType.fromInt(meta);

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

    @Override
    protected final BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EnumBlockType.property, Properties.facing2bit, BlockPowerTransformer.propertyMirrored);
    }

    @Override
    public final IBlockState getStateFromMeta(int meta) {
        return this.stateFromType(EnumBlockType.fromInt(meta));
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        return state.getValue(EnumBlockType.property).index;
    }

    public IBlockState stateFromType(EnumBlockType blockType) {
        return getDefaultState().withProperty(EnumBlockType.property, blockType);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof Render) {
            Render render = (Render) te;
            EnumFacing facing = render.getFacing();
            boolean mirrored = render.isMirrored();
            if (facing == null)
                return state; //Prevent crashing!

            state = state.withProperty(Properties.facing2bit, facing.ordinal() - 2 & 3)
                    .withProperty(BlockPowerTransformer.propertyMirrored, mirrored);
        }
        return state;
    }

    ///////////////////////////////
    /// Block activities
    ///////////////////////////////
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        Result ret = this.structureTemplate.attempToBuild(world, pos);
        if (ret != null) {
            ret.createStructure();
        }
        return;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null)
            this.structureTemplate.restoreStructure(te, state);

        super.breakBlock(world, pos, state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        EnumBlockType blockType = state.getValue(EnumBlockType.property);

        if (blockType.formed)
            return 0;

        return this.getMetaFromState(state);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        EnumBlockType blockType = state.getValue(EnumBlockType.property);

        if (blockType.formed) {
        	TileEntity te = world.getTileEntity(pos);
        	if (te instanceof IMultiBlockTile) {
        		MultiBlockTileInfo mbInfo = ((IMultiBlockTile) te).getMultiBlockTileInfo();
        		if (mbInfo != null) {
        			//TODO: No item drop
        		}
        	}
        	return ItemStack.EMPTY;
        }
            
        return new ItemStack(this.itemBlock, 1, this.damageDropped(state));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    public MultiBlockStructure createStructureTemplate() {
        //y,z,x facing NORTH(Z-), do not change
        MultiBlockStructure.BlockInfo[][][] configuration = new MultiBlockStructure.BlockInfo[5][][];

        MultiBlockStructure.BlockInfo core2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.IronCore), stateFromType(EnumBlockType.Placeholder));
        MultiBlockStructure.BlockInfo coil2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Winding), stateFromType(EnumBlockType.Placeholder));
        MultiBlockStructure.BlockInfo support2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.OilTankSupport), stateFromType(EnumBlockType.Placeholder));
        MultiBlockStructure.BlockInfo pipe2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.OilPipe), stateFromType(EnumBlockType.Placeholder));
        MultiBlockStructure.BlockInfo tank2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.OilTank), stateFromType(EnumBlockType.Placeholder));
        MultiBlockStructure.BlockInfo casing2PH = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Placeholder));
        MultiBlockStructure.BlockInfo casing2PHpri = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.PlaceholderPrimary));
        MultiBlockStructure.BlockInfo casing2PHsec = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.PlaceholderSecondary));
        MultiBlockStructure.BlockInfo casing2pri = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Primary));
        MultiBlockStructure.BlockInfo casing2sec = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Secondary));
        MultiBlockStructure.BlockInfo casing2render = new MultiBlockStructure.BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Render));


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
    public ISESimulatable getNode(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof Primary)
            return ((Primary) te).getPrimaryTile();
        else if (te instanceof Secondary)
            return ((Secondary) te).getSecondaryTile();
        else if (te instanceof TilePowerTransformerWinding)
            return ((TilePowerTransformerWinding) te).getGridNode();

        return null;
    }

    @Override
    public boolean canHVCableConnect(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof Primary)
            return ((Primary) te).canConnect();
        else if (te instanceof Secondary)
            return ((Secondary) te).canConnect();
        else if (te instanceof TilePowerTransformerWinding)
            return ((TilePowerTransformerWinding) te).canConnect();

        return false;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        EnumBlockType blockType = blockState.getValue(EnumBlockType.property);
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
