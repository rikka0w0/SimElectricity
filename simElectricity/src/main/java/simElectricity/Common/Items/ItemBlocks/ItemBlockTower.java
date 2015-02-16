package simElectricity.Common.Items.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import simElectricity.API.Common.Items.ItemBlockSE;
import simElectricity.Common.Blocks.BlockTower;

public class ItemBlockTower extends ItemBlockSE {

    public ItemBlockTower(Block block) {
        super(block);
        setHasSubtypes(true);
        setUnlocalizedName("SETower");
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + "." + BlockTower.TowerType.values()[itemstack.getMetadata()];
    }
}
