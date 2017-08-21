package simelectricity.essential.common.semachine;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public class ExtendedProperties {
	public final static IProperty<EnumFacing> propertyFacing = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.VALUES);
	public final static IProperty<Boolean> propertyIs2state = PropertyBool.create("is2state");
	
	public final static IUnlistedProperty<Integer> propertyDownSocket = 
			new Properties.PropertyAdapter<Integer>(PropertyInteger.create("downsocket", 0, ISESocketProvider.numOfSockets));
	
	public final static IUnlistedProperty<Integer> propertyUpSocket = 
			new Properties.PropertyAdapter<Integer>(PropertyInteger.create("upsocket", 0, ISESocketProvider.numOfSockets));
	
	public final static IUnlistedProperty<Integer> propertyNorthSocket = 
			new Properties.PropertyAdapter<Integer>(PropertyInteger.create("northsocket", 0, ISESocketProvider.numOfSockets));
	
	public final static IUnlistedProperty<Integer> propertySouthSocket = 
			new Properties.PropertyAdapter<Integer>(PropertyInteger.create("southsocket", 0, ISESocketProvider.numOfSockets));
	
	public final static IUnlistedProperty<Integer> propertyWestSocket = 
			new Properties.PropertyAdapter<Integer>(PropertyInteger.create("westsocket", 0, ISESocketProvider.numOfSockets));
	
	public final static IUnlistedProperty<Integer> propertyEastSocket = 
			new Properties.PropertyAdapter<Integer>(PropertyInteger.create("eastsocket", 0, ISESocketProvider.numOfSockets));
	
	/**
	 * Facing order, DUNSWE
	 */
	public final static IUnlistedProperty<Integer>[] propertySockets = 
			new IUnlistedProperty[] {
				propertyDownSocket, propertyUpSocket, propertyNorthSocket, propertySouthSocket, propertyWestSocket, propertyEastSocket
			};
}
