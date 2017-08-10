package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItemBlock;

public class ItemBlockCable<T extends BlockCable> extends SEItemBlock implements ISESimpleTextureItem{
	public ItemBlockCable(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getIconName(int damage) {
		T block = (T)this.block;
		return "essential_cable_" + block.subNames[damage] + "_inventory";
	}
}
