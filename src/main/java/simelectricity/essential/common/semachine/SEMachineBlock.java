package simelectricity.essential.common.semachine;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties.PropertyAdapter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.block.MetaBlock;
import rikka.librikka.item.ItemBlockBase;
import simelectricity.api.ISESidedFacing;
import simelectricity.api.SEAPI;

import java.util.ArrayList;

public abstract class SEMachineBlock extends MetaBlock implements ISubBlock {
    public SEMachineBlock(String unlocalizedName, String[] subNames) {
        super(unlocalizedName, subNames, Material.IRON, ItemBlockBase.class);
        
        setCreativeTab(SEAPI.SETab);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this.itemBlock, 1, getMetaFromState(world.getBlockState(pos)));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    protected abstract boolean isSecondState(TileEntity te);

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        //Need this otherwise sockets won't be rendered correctly
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected void createProperties(ArrayList<IProperty> properties, ArrayList<IUnlistedProperty> unlisted) {
        super.createProperties(properties, unlisted);
        properties.add(propertyFacing);
        properties.add(propertyIs2state);

        unlisted.add(propertyDownSocket);
        unlisted.add(propertyUpSocket);
        unlisted.add(propertyNorthSocket);
        unlisted.add(propertySouthSocket);
        unlisted.add(propertyWestSocket);
        unlisted.add(propertyEastSocket);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof ISESidedFacing) {
            EnumFacing facing = ((ISESidedFacing) te).getFacing();
            state = state.withProperty(propertyFacing, facing);
        }

        state = state.withProperty(propertyIs2state, this.isSecondState(te));

        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState retval = (IExtendedBlockState) state;

            TileEntity te = world.getTileEntity(pos);

            if (te instanceof ISESocketProvider) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    int socketIconIndex = ((ISESocketProvider) te).getSocketIconIndex(facing);
                    socketIconIndex++; //Shift the range, so 0 becomes no icon
                    IUnlistedProperty<Integer> prop = propertySockets[facing.ordinal()];
                    retval = retval.withProperty(prop, socketIconIndex);
                }
            }

            return retval;
        }
        return state;
    }
    
    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}
	
	@Override
	public abstract TileEntity createTileEntity(World world, IBlockState state);
	
	/////////////////////////////////////////////////////
	/// BlockStates and Extended BlockState Declaration
	/////////////////////////////////////////////////////
    public static final IProperty<EnumFacing> propertyFacing = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.VALUES);
    public static final IProperty<Boolean> propertyIs2state = PropertyBool.create("is2state");

    public static final IUnlistedProperty<Integer> propertyDownSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("downsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyUpSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("upsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyNorthSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("northsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertySouthSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("southsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyWestSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("westsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyEastSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("eastsocket", 0, ISESocketProvider.numOfSockets));

    /**
     * Facing order, DUNSWE
     */
    public static final IUnlistedProperty<Integer>[] propertySockets =
            new IUnlistedProperty[]{
                    propertyDownSocket, propertyUpSocket, propertyNorthSocket, propertySouthSocket, propertyWestSocket, propertyEastSocket
            };
}
