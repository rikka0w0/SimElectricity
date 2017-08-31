package rikka.librikka.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ISubBlock;

public class ItemBlockBase extends ItemBlock {
    public ItemBlockBase(Block block) {
        super(block);

        boolean hasSubBlocks = block instanceof ISubBlock;

        if (!(block instanceof BlockBase))
            throw new RuntimeException("ItemBlockBase should be used with BlockBase!");

		this.setHasSubtypes(hasSubBlocks);

        if (hasSubBlocks)
			setMaxDamage(0);    //The item can not be damaged
    }

    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
        if (getHasSubtypes()) {
            BlockBase blockBase = (BlockBase) getBlock();
            String[] subBlockUnlocalizedNames = ((ISubBlock) blockBase).getSubBlockUnlocalizedNames();
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
