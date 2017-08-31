package rikka.librikka.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISimpleTexture {
    @SideOnly(Side.CLIENT)
    String getIconName(int damage);
}
