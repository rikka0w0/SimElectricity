package simelectricity.essential.grid;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;

public class Properties {
	public final static IProperty<Integer> propertyType = PropertyInteger.create("type", 0, 1);			//bit4
	public final static IProperty<Integer> propertyFacing = PropertyInteger.create("facing", 0 , 7);	//bit3-bit0
	
	public final static IProperty<Integer> propertyFacing2 = PropertyInteger.create("facing", 0 , 3);	//bit2-bit0
	public final static IProperty<Boolean> propertyIsRod = PropertyBool.create("isrod");				//bit3
}
