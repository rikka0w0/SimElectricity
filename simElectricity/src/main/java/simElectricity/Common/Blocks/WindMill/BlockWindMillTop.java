package simElectricity.Common.Blocks.WindMill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
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
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;
import simElectricity.Common.Core.SEItems;

import java.util.Random;

public class BlockWindMillTop extends BlockContainerSE {
    private IIcon[] iconBuffer = new IIcon[6];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        TileWindMillTop te = (TileWindMillTop) world.getTileEntity(x, y, z);
        ItemStack playerItem = player.getCurrentEquippedItem();

        if (player.isSneaking())
            return false;


        if (te.settled) {
            if (playerItem != null)
                return false;

            dropBlockAsItem(world, x, y + 1, z, new ItemStack(SEItems.fan, 1));
            te.settled = false;
        } else {
            if (playerItem == null)
                return false;

            if (playerItem.getItem() != SEItems.fan)
                return false;

            te.settled = true;
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileWindMillTop) {
            if (((TileWindMillTop) te).settled)
                dropBlockAsItem(world, x, y, z, new ItemStack(SEItems.fan, 1));
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    public BlockWindMillTop() {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("WindMillTop");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[2] = r.registerIcon("simElectricity:WindMill_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:WindMill_Back");
        iconBuffer[4] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:WindMill_Side");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        return iconBuffer[Util.getTextureOnSide(side, ((ISidedFacing) te).getFacing())];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);

        ((ISidedFacing) te).setFacing(Util.getPlayerSight(player).getOpposite());
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);

        Util.updateTileEntityFacing(te);
        Util.updateTileEntityField(te, "settled");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isRemote)
            return;

        //Server side only!
        TileEntity te = world.getTileEntity(x, y, z);
        Util.updateTileEntityFacing(te);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileWindMillTop();
    }

    @Override
    public int damageDropped(int par1) {
        return par1;
    }
}
