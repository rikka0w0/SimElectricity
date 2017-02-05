package simElectricity.Templates.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import simElectricity.Templates.Blocks.BlockTower;
import simElectricity.Templates.Common.ItemBlockSE;

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
        return getUnlocalizedName() + "." + BlockTower.subNames[itemstack.getItemDamage()];
    }
}
