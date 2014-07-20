package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.Util;
import simElectricity.mod_SimElectricity;

import java.util.Random;

public class BlockAdjustableTransformer extends BlockContainer {
    private IIcon[] iconBuffer = new IIcon[6];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;

        player.openGui(mod_SimElectricity.instance, 0, world, x, y, z);
        return true;
    }

    public BlockAdjustableTransformer() {
        super(Material.rock);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("AdjustableTransformer");
        setCreativeTab(Util.SETab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:Transformer_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:Transformer_Secondary");
        iconBuffer[2] = r.registerIcon("simElectricity:Transformer_Primary");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);


        if (side == te.primarySide.ordinal())
            return iconBuffer[2];
        else if (side == te.secondarySide.ordinal())
            return iconBuffer[1];
        else
            return iconBuffer[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4)
            return iconBuffer[1];
        else
            return iconBuffer[0];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);

        te.secondarySide = Util.getPlayerSight(player);

        if (world.getTileEntity(x + 1, y, z) instanceof IConductor)
            te.secondarySide = ForgeDirection.EAST;
        else if (world.getTileEntity(x - 1, y, z) instanceof IConductor)
            te.secondarySide = ForgeDirection.WEST;
        else if (world.getTileEntity(x, y, z + 1) instanceof IConductor)
            te.secondarySide = ForgeDirection.SOUTH;
        else if (world.getTileEntity(x, y, z - 1) instanceof IConductor)
            te.secondarySide = ForgeDirection.NORTH;
        else if (world.getTileEntity(x, y + 1, z) instanceof IConductor)
            te.secondarySide = ForgeDirection.UP;
        else if (world.getTileEntity(x, y - 1, z) instanceof IConductor)
            te.secondarySide = ForgeDirection.DOWN;

        te.primarySide = te.secondarySide.getOpposite();

        if (te.secondarySide != ForgeDirection.EAST && world.getTileEntity(x + 1, y, z) instanceof IConductor)
            te.primarySide = ForgeDirection.EAST;
        else if (te.secondarySide != ForgeDirection.WEST && world.getTileEntity(x - 1, y, z) instanceof IConductor)
            te.primarySide = ForgeDirection.WEST;
        else if (te.secondarySide != ForgeDirection.SOUTH && world.getTileEntity(x, y, z + 1) instanceof IConductor)
            te.primarySide = ForgeDirection.SOUTH;
        else if (te.secondarySide != ForgeDirection.NORTH && world.getTileEntity(x, y, z - 1) instanceof IConductor)
            te.primarySide = ForgeDirection.NORTH;
        else if (te.secondarySide != ForgeDirection.UP && world.getTileEntity(x, y + 1, z) instanceof IConductor)
            te.primarySide = ForgeDirection.UP;
        else if (te.secondarySide != ForgeDirection.DOWN && world.getTileEntity(x, y - 1, z) instanceof IConductor)
            te.primarySide = ForgeDirection.DOWN;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
        if (world.isRemote)
            return;

        TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);


        Util.updateTileEntityField(te, "primarySide");
        Util.updateTileEntityField(te, "secondarySide");
        world.notifyBlockChange(x, y, z, this);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileAdjustableTransformer();
    }
}
