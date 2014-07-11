package simElectricity.Blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockWire extends ItemBlock {
    public ItemBlockWire(Block block) {
        super(block);
        setHasSubtypes(true);
        setUnlocalizedName("ItemBlock_SEWire");
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + "." + BlockWire.subNames[itemstack.getItemDamage()];
    }
}