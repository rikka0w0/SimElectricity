package simElectricity.Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import simElectricity.API.Util;

public class Item_Fan extends Item {
    public Item_Fan() {
        super();
        setUnlocalizedName("Item_Fan");
        setCreativeTab(Util.SETab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_Fan");
    }
}