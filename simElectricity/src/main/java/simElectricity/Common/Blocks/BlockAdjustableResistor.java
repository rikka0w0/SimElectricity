package simElectricity.Common.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileAdjustableResistor;
import simElectricity.SimElectricity;

public class BlockAdjustableResistor extends BlockStandardSEMachine {
    private IIcon[] iconBuffer = new IIcon[6];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;

        player.openGui(SimElectricity.instance, 0, world, x, y, z);
        return true;
    }

    public BlockAdjustableResistor() {
        super();
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("AdjustableResistor");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:AdjustableResistor_Bottom");
        iconBuffer[1] = r.registerIcon("simElectricity:AdjustableResistor_Top");
        iconBuffer[2] = r.registerIcon("simElectricity:AdjustableResistor_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:AdjustableResistor_Side");
        iconBuffer[4] = r.registerIcon("simElectricity:AdjustableResistor_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:AdjustableResistor_Side");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof ISidedFacing))
            return iconBuffer[0];

        return iconBuffer[Util.getTextureOnSide(side, ((ISidedFacing) te).getFacing())];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAdjustableResistor();
    }
}
