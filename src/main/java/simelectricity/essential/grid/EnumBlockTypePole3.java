package simelectricity.essential.grid;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumBlockTypePole3 implements IStringSerializable {
    Pole(0, "pole", true, 0),
    Crossarm10kVT0(1, "crossarm10kvt0", false, 3),
    Crossarm10kVT1(2, "crossarm10kvt1", false, 3),
    Crossarm415VT0(3, "crossarm415vt0", false, 4),
    Branching10kV(4, "branching10kv", false, 3),
    Branching415V(5, "branching415v", false, 4);

    public static final PropertyEnum<EnumBlockTypePole3> property = PropertyEnum.create("blocktype", EnumBlockTypePole3.class);

    public static final EnumBlockTypePole3[] values;
    public static final String[] names;

    static {
        values = new EnumBlockTypePole3[EnumBlockTypePole3.values().length];
        names = new String[EnumBlockTypePole3.values().length];
        for (EnumBlockTypePole3 value : EnumBlockTypePole3.values()) {
            int index = value.ordinal();
            EnumBlockTypePole3.values[index] = value;
            EnumBlockTypePole3.names[index] = value.name;
        }
    }

    public final int index;
    public final boolean ignoreFacing;
    public final int numOfConductor;
    private final String name;

    EnumBlockTypePole3(int index, String name, boolean ignoreFacing, int numOfConductor) {
        this.index = index;
        this.name = name;
        this.ignoreFacing = ignoreFacing;
        this.numOfConductor = numOfConductor;
    }

    public static EnumBlockTypePole3 fromInt(int in) {
        if (in >= EnumBlockTypePole3.values.length || in < 0)
            return null;

        return EnumBlockTypePole3.values[in];
    }

    @Override
    public String getName() {
        return this.name;
    }
}
