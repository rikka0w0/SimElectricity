package simelectricity.essential.common;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public class ExtendedProperties {
	public final static IUnlistedProperty<EnumFacing> propertyFacing =
			new Properties.PropertyAdapter<EnumFacing>(PropertyEnum.create("facing", EnumFacing.class, EnumFacing.VALUES));
	
	
}
