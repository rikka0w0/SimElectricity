package rikka.librikka;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;

public class Properties {
	public static final IProperty<Integer> facing3bit = PropertyInteger.create("facing", 0, 7);
	public static final IProperty<Integer> facing2bit = PropertyInteger.create("facing", 0, 3);
    public static final IProperty<Integer> type1bit = PropertyInteger.create("type", 0, 1);
}
