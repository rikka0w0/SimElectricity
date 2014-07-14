package simElectricity.Blocks;

import java.util.Random;

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
import simElectricity.mod_SimElectricity;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;
import simElectricity.API.EnergyTile.IEnergyTile;

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
        setBlockName("Transformer");
        setCreativeTab(Util.SETab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:WindMill_Back");
        iconBuffer[1] = r.registerIcon("simElectricity:SolarPanel_Front");
        iconBuffer[2] = r.registerIcon("simElectricity:QuantumGenerator_Front");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
    	TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);

    	
    	if (side==te.getPrimarySide().ordinal())
    		return iconBuffer[2];
    	else if (side==te.getSecondarySide().ordinal())
    		return iconBuffer[1];
    	else 
    		return iconBuffer[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[0];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);

        te.secondarySide=Util.getPlayerSight(player);
        te.primarySide=te.secondarySide.getOpposite();
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
        if (world.isRemote)
            return;
        
        TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);


        Util.updateTileEntityField(te,"primarySide");
        Util.updateTileEntityField(te,"secondarySide");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileAdjustableTransformer();
    }
}
