package simElectricity.Common.Blocks;

import simElectricity.API.Util;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.Common.Blocks.TileEntity.TileTower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTower extends BlockContainerSE{
	
	
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
    	if(playerSight == ForgeDirection.EAST || playerSight == ForgeDirection.WEST){
    		tower.facing=1;
    	}else{
    		tower.facing=0;
    	}
    }
    
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileTower();
	}

	//This will tell minecraft not to render any side of our cube.
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l){return false;}

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube(){return false;}
}
