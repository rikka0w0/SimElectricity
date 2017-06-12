package simelectricity.essential.fluids;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SEItemBlockFluid extends ItemBlock{
	public SEItemBlockFluid(Block block) {
		super(block);
		setHasSubtypes(false);
	}

	@Override
	public final String getUnlocalizedNameInefficiently(ItemStack stack){
		String prevName = super.getUnlocalizedNameInefficiently(stack);
		return "fluid." + prevName.substring(5);
	}
}
