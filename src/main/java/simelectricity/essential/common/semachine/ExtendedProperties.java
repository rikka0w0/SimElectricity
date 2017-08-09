package simelectricity.essential.common.semachine;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public class ExtendedProperties {
	public final static IUnlistedProperty<EnumFacing> propertyFacing =
			new Properties.PropertyAdapter<EnumFacing>(PropertyEnum.create("facing", EnumFacing.class, EnumFacing.VALUES));
	
	public final static IUnlistedProperty<Boolean> propertIs2State = 
			new Properties.PropertyAdapter<Boolean>(PropertyBool.create("is2state"));
}
