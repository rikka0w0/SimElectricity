package simElectricity.Common.Blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.Common.Blocks.TileEntity.TileGridTerminal;
import simElectricity.Common.Blocks.TileEntity.TileIC2Consumer;
import simElectricity.Common.EnergyNet.WorldData;

public class BlockGridTerminal extends BlockStandardSEMachine{
	private IIcon[] iconBuffer = new IIcon[2];
	
	public BlockGridTerminal() {
        super();
        setBlockName("GridTerminal");
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
        return iconBuffer[0 == side ? 1 : 0];//Bottom
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[4 == side ? 1 : 0];
    }   
    
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGridTerminal();
	}
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, player, itemStack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);
        
        if (!(te instanceof TileGridTerminal))
        	return;
        
        WorldData.getEnergyNetForWorld(world).grid.attachNode(te);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
    	WorldData.getEnergyNetForWorld(world).grid.detachNode(te);
        super.breakBlock(world, x, y, z, block, meta);
    }
}
