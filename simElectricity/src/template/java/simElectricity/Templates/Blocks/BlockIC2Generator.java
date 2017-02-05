package simElectricity.Templates.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.Templates.SETemplate;
import simElectricity.Templates.Common.BlockStandardSEMachine;
import simElectricity.Templates.TileEntity.TileIC2Generator;

public class BlockIC2Generator extends BlockStandardSEMachine{
	private IIcon[] iconBuffer = new IIcon[2];
	
    public BlockIC2Generator() {
        super();
        setBlockName("IC2Generator");
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;
        
        player.openGui(SETemplate.instance, 0, world, x, y, z);
		return true;
    }
        
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:Transformer_Primary");   
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
    	TileIC2Generator te = (TileIC2Generator) world.getTileEntity(x, y, z);

        return iconBuffer[te.getFunctionalSide().ordinal() == side ? 1 : 0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[4 == side ? 1 : 0];
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileIC2Generator();
    }
}
