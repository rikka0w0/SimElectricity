package SimElectricity.Samples;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockSample extends ItemBlock {
	public ItemBlockSample(int id) {
		super(id);
		setHasSubtypes(true);
		setUnlocalizedName("Item_SESample");
	}
	
	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + "." + BlockSample.subNames[itemstack.getItemDamage()];
	}
}