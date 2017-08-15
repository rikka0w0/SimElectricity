package simelectricity.essential.grid;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;

public class Properties {
	public final static IProperty<Integer> propertyType = PropertyInteger.create("type", 0, 1);			//bit4
	public final static IProperty<Integer> propertyFacing = PropertyInteger.create("facing", 0 , 7);	//bit3-bit0
}
