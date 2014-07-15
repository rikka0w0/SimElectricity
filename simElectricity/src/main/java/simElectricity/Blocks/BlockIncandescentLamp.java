package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Util;

import java.util.Random;

public class BlockIncandescentLamp extends BlockContainer {
    private IIcon[] iconBuffer = new IIcon[6];

    public BlockIncandescentLamp() {
        super(Material.rock);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("IncandescentLamp");
        setCreativeTab(Util.SETab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:SolarPanel_Bottom");
        iconBuffer[1] = r.registerIcon("simElectricity:SolarPanel_Bottom");
        iconBuffer[2] = r.registerIcon("simElectricity:SolarPanel_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:SolarPanel_Side");
        iconBuffer[4] = r.registerIcon("simElectricity:SolarPanel_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:SolarPanel_Side");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileIncandescentLamp))
            return iconBuffer[0];

        int iconIndex = Util.getTextureOnSide(side, ((TileIncandescentLamp) te).getFunctionalSide());

        if (((TileIncandescentLamp) te).getFunctionalSide() == ForgeDirection.DOWN) {
            if (iconIndex == 3) {
                iconIndex = 1;
            } else if (iconIndex == 1) {
                iconIndex = 3;
            }
        }

        return iconBuffer[iconIndex];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileIncandescentLamp))
            return;

        ((TileIncandescentLamp) te).setFunctionalSide(Util.getPlayerSight(player).getOpposite());
    }

    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileIncandescentLamp))
            return 0;

        return ((TileIncandescentLamp) te).lightLevel;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
        TileEntity te = world.getTileEntity(x, y, z);


        if (world.isRemote)
            return;

        if (!(te instanceof TileIncandescentLamp))
            return;
        Util.updateTileEntityFunctionalSide(te);
        Util.updateTileEntityField(te, "lightLevel");

        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isRemote)
            return;

        //Server side only!
        TileEntity te = world.getTileEntity(x, y, z);
        Util.updateTileEntityFunctionalSide(te);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileIncandescentLamp();
    }

    @Override
    public int damageDropped(int par1) {
        return par1;
    }
}
