package simElectricity.Common.Blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.Util;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.Common.Blocks.TileEntity.TileCableClamp;

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
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCableClamp();
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
