package simelectricity.essential.grid;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;

public class Properties {
    public static final IProperty<Integer> propertyType = PropertyInteger.create("type", 0, 1);            //bit4
    public static final IProperty<Integer> propertyFacing = PropertyInteger.create("facing", 0, 7);    //bit3-bit0

    public static final IProperty<Integer> propertyFacing2 = PropertyInteger.create("facing", 0, 3);    //bit2-bit0
    public static final IProperty<Boolean> propertyIsPole = PropertyBool.create("ispole");                //bit3
}
