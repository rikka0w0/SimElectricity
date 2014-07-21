package simElectricity.Common.Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import simElectricity.API.Common.Items.ItemSE;

public class ItemFan extends ItemSE {
    public ItemFan() {
        super();
        setUnlocalizedName("Fan");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_Fan");
    }
}
