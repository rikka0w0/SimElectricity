package rikka.librikka.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import rikka.librikka.block.ISESubBlock;
import rikka.librikka.block.SEBlock;

public class SEItemBlock extends ItemBlock {
    public SEItemBlock(Block block) {
        super(block);

        boolean hasSubBlocks = block instanceof ISESubBlock;

        if (!(block instanceof SEBlock))
            throw new RuntimeException("SEItemBlock should be used with SEblock!");

		this.setHasSubtypes(hasSubBlocks);

        if (hasSubBlocks)
			setMaxDamage(0);    //The item can not be damaged
    }

    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
        if (getHasSubtypes()) {
            SEBlock seBlock = (SEBlock) getBlock();
            String[] subBlockUnlocalizedNames = ((ISESubBlock) seBlock).getSubBlockUnlocalizedNames();
            return getUnlocalizedName() + "." + subBlockUnlocalizedNames[itemstack.getItemDamage()];
        } else {
            return getUnlocalizedName();
        }
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    @Override
    public final int getMetadata(int damage) {
        if (getHasSubtypes()) {
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public final String getUnlocalizedNameInefficiently(ItemStack stack) {
        String prevName = super.getUnlocalizedNameInefficiently(stack);
        String domain = this.getRegistryName().getResourceDomain();
        return "tile." + domain + ":" + prevName.substring(5);
    }
}
