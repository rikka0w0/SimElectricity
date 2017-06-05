package simelectricity.Templates.Blocks;

import simelectricity.api.SEAPI;
import simelectricity.Templates.Common.BlockContainerSE;
import simelectricity.Templates.TileEntity.TileCableClamp;
import simelectricity.Templates.Utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        tower.facing = Utils.getPlayerSight(player, true);
        
        SEAPI.energyNetAgent.attachGridObject(world, x, y, z, (byte)0);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        SEAPI.energyNetAgent.detachGridObject(world, x, y, z);
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
