package simElectricity.Common.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Energy;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileCableClamp;
import simElectricity.Common.Blocks.TileEntity.TileTower;

public class BlockCableClamp extends BlockContainerSE{
    public BlockCableClamp() {
        super();
        setBlockName("CableClamp");
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return;

        TileCableClamp tower = (TileCableClamp) world.getTileEntity(x, y, z);
        tower.facing = Util.getPlayerSight(player, true).ordinal();
        
        Energy.postGridObjectAttachEvent(world, x, y, z, (byte)0);
        
        TileEntity possibleNeighbor = world.getTileEntity(x, y+2, z);
        if (possibleNeighbor instanceof TileTower && world.getBlockMetadata(x, y+2, z) == 1){
        	Energy.postGridConnectionEvent(world, x, y, z, x, y+2, z, 0.1);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        Energy.postGridObjectDetachEvent(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCableClamp();
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
