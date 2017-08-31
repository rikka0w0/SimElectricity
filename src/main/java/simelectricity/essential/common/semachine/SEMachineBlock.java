package simelectricity.essential.common.semachine;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.block.MetaBlock;
import rikka.librikka.item.ItemBlockBase;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;

import java.util.ArrayList;

public abstract class SEMachineBlock extends MetaBlock implements ITileEntityProvider, ISubBlock {
    protected final String[] subNames;

    public SEMachineBlock(String unlocalizedName, String[] subNames) {
        super(unlocalizedName, Material.IRON, ItemBlockBase.class);

        this.subNames = new String[subNames.length];
        for (int i = 0; i < subNames.length; i++)
            this.subNames[i] = subNames[i];
        
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void beforeRegister() {
        isBlockContainer = true;
        setCreativeTab(SEAPI.SETab);
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
    public String[] getSubBlockUnlocalizedNames() {
        return this.subNames;
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
        properties.add(ExtendedProperties.propertyFacing);
        properties.add(ExtendedProperties.propertyIs2state);

        unlisted.add(ExtendedProperties.propertyDownSocket);
        unlisted.add(ExtendedProperties.propertyUpSocket);
        unlisted.add(ExtendedProperties.propertyNorthSocket);
        unlisted.add(ExtendedProperties.propertySouthSocket);
        unlisted.add(ExtendedProperties.propertyWestSocket);
        unlisted.add(ExtendedProperties.propertyEastSocket);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof ISidedFacing) {
            EnumFacing facing = ((ISidedFacing) te).getFacing();
            state = state.withProperty(ExtendedProperties.propertyFacing, facing);
        }

        state = state.withProperty(ExtendedProperties.propertyIs2state, this.isSecondState(te));

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
                    IUnlistedProperty<Integer> prop = ExtendedProperties.propertySockets[facing.ordinal()];
                    retval = retval.withProperty(prop, socketIconIndex);
                }
            }

            return retval;
        }
        return state;
    }
}
