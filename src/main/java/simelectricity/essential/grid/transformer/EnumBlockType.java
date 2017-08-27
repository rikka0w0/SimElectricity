package simelectricity.essential.grid.transformer;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3i;

public enum EnumBlockType implements IStringSerializable {
    Casing(0, false, "casing", null),
    IronCore(1, false, "ironcore", null),
    Winding(2, false, "winding", null),
    OilTank(3, false, "oiltank", null),
    OilTankSupport(4, false, "oiltanksupport", null),
    OilPipe(5, false, "oilpipe", null),

    PlaceholderPrimary(6, true, "placeholderprimary", null),        //Connection to primary
    Primary(7, true, "primary", new Vec3i(3, 2, 1)),                    //Primary node
    PlaceholderSecondary(8, true, "placeholdersecondary", null),    //Connection to secondary
    Secondary(9, true, "secondary", new Vec3i(4, 2, 3)),                //Secondary node
    Placeholder(10, true, "placeholder", null),
    Render(11, true, "render", new Vec3i(3, 2, 2));                    //Render the structure

    public static final PropertyEnum<EnumBlockType> property = PropertyEnum.create("blocktype", EnumBlockType.class);

    public static final EnumBlockType[] values = new EnumBlockType[12];
    public static final EnumBlockType[] rawStructure = new EnumBlockType[6];
    public static final EnumBlockType[] formedStructure = new EnumBlockType[6];

    static {
        int i = 0, j = 0;
        for (EnumBlockType value : EnumBlockType.values()) {
            if (value.formed) {
                EnumBlockType.formedStructure[j] = value;
                j++;
            } else {
                EnumBlockType.rawStructure[i] = value;
                i++;
            }
            EnumBlockType.values[value.index] = value;
        }
    }

    public final int index;
    public final boolean formed;
    public final Vec3i offset;
    private final String name;
    EnumBlockType(int index, boolean formed, String name, Vec3i offset) {
        this.index = index;
        this.formed = formed;
        this.name = name;
        this.offset = offset;
    }

    public static String[] getRawStructureNames() {
        String[] ret = new String[EnumBlockType.rawStructure.length];

        for (int i = 0; i < EnumBlockType.rawStructure.length; i++)
            ret[i] = EnumBlockType.rawStructure[i].name;

        return ret;
    }

    public static EnumBlockType fromInt(int in) {
        if (in >= EnumBlockType.values.length || in < 0)
            return null;

        return EnumBlockType.values[in];
    }

    @Override
    public String getName() {
        return this.name;
    }
}