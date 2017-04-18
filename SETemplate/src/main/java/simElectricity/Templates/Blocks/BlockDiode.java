package simElectricity.Templates.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.SEAPI;
import simElectricity.Templates.Common.BlockContainerSE;
import simElectricity.Templates.TileEntity.TileDiode;
import simElectricity.Templates.Utils.Utils;

public class BlockDiode extends BlockContainerSE {
    private IIcon[] iconBuffer = new IIcon[5];
    
    public BlockDiode() {
        super();
        setBlockName("Diode");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:ElectricFurnace_Top");
        iconBuffer[1] = r.registerIcon("simElectricity:Switch_Out");
        iconBuffer[2] = r.registerIcon("simElectricity:Switch_In");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
    	TileDiode te = (TileDiode) world.getTileEntity(x, y, z);


        if (side == te.inputSide.ordinal())
            return iconBuffer[2];
        else if (side == te.outputSide.ordinal())
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
    	TileDiode te = (TileDiode) world.getTileEntity(x, y, z);

        ForgeDirection facing = Utils.getPlayerSight(player, false).getOpposite();

        te.outputSide = Utils.getPlayerSight(player, false);
        te.inputSide = te.outputSide.getOpposite();

        if (te.outputSide == facing)
            te.outputSide = te.outputSide.getRotation(ForgeDirection.UP);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
    	return new TileDiode();
    }
}
