package rikka.librikka.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISESimpleTextureItem {
    @SideOnly(Side.CLIENT)
    String getIconName(int damage);
}
