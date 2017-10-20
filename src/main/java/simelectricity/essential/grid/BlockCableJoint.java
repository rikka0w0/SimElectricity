package simelectricity.essential.grid;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.Properties;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockCableJoint extends BlockBase implements ISubBlock, ISEHVCableConnector, ISimpleTexture {
	private final static String[] subNames = new String[] {"10kv", "415v"};
	
    public BlockCableJoint() {
        super("essential_cable_joint", Material.GLASS, ItemBlockBase.class);
		setCreativeTab(SEAPI.SETab);
		setHardness(0.2F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getIconName(int damage) {
        return "essential_cable_joint_" + subNames[damage];
    }

	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected final BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{Properties.facing3bit, Properties.type1bit});
    }

    @Override
    public final IBlockState getStateFromMeta(int meta) {
    	int facing = meta & 7;
    	int type = (meta&8)>>3;
        return getDefaultState().withProperty(Properties.facing3bit, facing).withProperty(Properties.type1bit, type);
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        int facing = state.getValue(Properties.facing3bit);
        int type = state.getValue(Properties.type1bit);
        return (type<<3)&8 | facing&7;
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}
	
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int type = state.getValue(Properties.type1bit);
        return type==0 ? new TileCableJoint.Type10kV() : new TileCableJoint.Type415V();
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
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

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(Properties.type1bit);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int facingInt = 8 - MathHelper.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D - 4) & 7;
        return this.getDefaultState().withProperty(Properties.type1bit, meta & 1).withProperty(Properties.facing3bit, facingInt);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGridTile) {
        	
        	int numOfConductor = te instanceof TileCableJoint.Type10kV ? 3 : 4;
        	SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, numOfConductor));
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
        if (te instanceof ISEGridTile)
            SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());

        super.breakBlock(world, pos, state);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileCableJoint)
            return (TileCableJoint) te;
        else
            return null;
    }
}
