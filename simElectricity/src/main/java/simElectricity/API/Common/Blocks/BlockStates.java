package simElectricity.API.Common.Blocks;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

public class BlockStates {
    public static final PropertyBool ISWORKING = PropertyBool.create("working");
    public static final PropertyDirection HORIFACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
}
