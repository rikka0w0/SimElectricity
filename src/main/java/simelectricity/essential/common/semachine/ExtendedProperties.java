package simelectricity.essential.common.semachine;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties.PropertyAdapter;

public class ExtendedProperties {
    public static final IProperty<EnumFacing> propertyFacing = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.VALUES);
    public static final IProperty<Boolean> propertyIs2state = PropertyBool.create("is2state");

    public static final IUnlistedProperty<Integer> propertyDownSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("downsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyUpSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("upsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyNorthSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("northsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertySouthSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("southsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyWestSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("westsocket", 0, ISESocketProvider.numOfSockets));

    public static final IUnlistedProperty<Integer> propertyEastSocket =
            new PropertyAdapter<Integer>(PropertyInteger.create("eastsocket", 0, ISESocketProvider.numOfSockets));

    /**
     * Facing order, DUNSWE
     */
    public static final IUnlistedProperty<Integer>[] propertySockets =
            new IUnlistedProperty[]{
                    ExtendedProperties.propertyDownSocket, ExtendedProperties.propertyUpSocket, ExtendedProperties.propertyNorthSocket, ExtendedProperties.propertySouthSocket, ExtendedProperties.propertyWestSocket, ExtendedProperties.propertyEastSocket
            };
}
