package simElectricity.Templates.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import simElectricity.Templates.Blocks.BlockTransmissionTower;
import simElectricity.Templates.Common.ItemBlockSE;

public class ItemBlockTransmissionTower extends ItemBlockSE{
    public ItemBlockTransmissionTower(Block block) {
        super(block);
        setHasSubtypes(true);
        setUnlocalizedName("SETransmissionTower");
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + "." + BlockTransmissionTower.subNames[itemstack.getItemDamage()];
    }
}
