package simElectricity.Common.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileTower;

public class BlockTower extends BlockContainerSE {


    public BlockTower() {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("Tower");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileTower tower = (TileTower) world.getTileEntity(x, y, z);
        ForgeDirection playerSight = Util.getPlayerSight(player);
        if (playerSight == ForgeDirection.EAST || playerSight == ForgeDirection.WEST) {
            tower.facing = 1;
        } else {
            tower.facing = 0;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (world.getTileEntity(x, y, z) instanceof TileTower) {
            TileTower tower = (TileTower) world.getTileEntity(x, y, z);
            for (int i = 0; i < tower.neighborsInfo.length; i += 3)
                if (world.getTileEntity(tower.neighborsInfo[i], tower.neighborsInfo[i + 1], tower.neighborsInfo[i + 2]) instanceof TileTower)
                    ((TileTower) world.getTileEntity(tower.neighborsInfo[0], tower.neighborsInfo[1], tower.neighborsInfo[2])).delNeighbor(tower);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTower();
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
