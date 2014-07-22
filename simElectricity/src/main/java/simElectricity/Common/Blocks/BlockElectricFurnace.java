package simElectricity.Common.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileElectricFurnace;
import simElectricity.SimElectricity;

import java.util.Random;

public class BlockElectricFurnace extends BlockStandardSEMachine {
    private IIcon[] iconBuffer = new IIcon[7];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (player.isSneaking())
            return false;

        if (!(te instanceof TileElectricFurnace))
            return false;

        player.openGui(SimElectricity.instance, 0, world, x, y, z);
        return true;
    }

    public BlockElectricFurnace() {
        super();
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("ElectricFurnace");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:ElectricFurnace_Bottom");
        iconBuffer[1] = r.registerIcon("simElectricity:ElectricFurnace_Top");
        iconBuffer[2] = r.registerIcon("simElectricity:ElectricFurnace_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:ElectricFurnace_Side");
        iconBuffer[4] = r.registerIcon("simElectricity:ElectricFurnace_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:ElectricFurnace_Side");
        iconBuffer[6] = r.registerIcon("simElectricity:ElectricFurnace_Front_W");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileElectricFurnace))
            return iconBuffer[0];

        int iconIndex = Util.getTextureOnSide(side, ((TileElectricFurnace) te).getFacing());
        if (((TileElectricFurnace) te).isWorking && iconIndex == 2)
            iconIndex = 6;

        return iconBuffer[iconIndex];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote)
            return;
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileElectricFurnace))
            return;
        Util.updateTileEntityFacing(te);
        Util.updateTileEntityFunctionalSide(te);

        world.markBlockForUpdate(x, y, z);
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
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileElectricFurnace();
    }

    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileElectricFurnace))
            return 0;

        return ((TileElectricFurnace) te).isWorking ? 13 : 0;
    }
}
