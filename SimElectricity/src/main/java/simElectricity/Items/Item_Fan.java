package simElectricity.Items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import simElectricity.API.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Item_Fan extends Item{
	public Item_Fan() {
		super();
		setUnlocalizedName("sime:Item_Fan");
		setCreativeTab(Util.SETab);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r)
    {
    	itemIcon=r.registerIcon("simElectricity:Item_Fan");
    }
}
