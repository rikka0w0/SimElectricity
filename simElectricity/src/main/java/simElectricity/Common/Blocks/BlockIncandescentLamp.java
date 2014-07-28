package simElectricity.Common.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileIncandescentLamp;

import java.util.Random;

public class BlockIncandescentLamp extends BlockStandardSEMachine {
    private IIcon[] iconBuffer = new IIcon[6];

    public BlockIncandescentLamp() {
        super();
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("IncandescentLamp");
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
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileIncandescentLamp))
            return 0;

        return ((TileIncandescentLamp) te).lightLevel;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileIncandescentLamp();
    }
}
