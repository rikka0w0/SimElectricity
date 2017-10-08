package simelectricity.essential.grid.transformer;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

public enum EnumPowerTransformerBlockType implements IStringSerializable {
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

    public static final PropertyEnum<EnumPowerTransformerBlockType> property = PropertyEnum.create("blocktype", EnumPowerTransformerBlockType.class);

    public static final EnumPowerTransformerBlockType[] values = new EnumPowerTransformerBlockType[12];
    static final EnumPowerTransformerBlockType[] rawStructure = new EnumPowerTransformerBlockType[6];
    static final EnumPowerTransformerBlockType[] formedStructure = new EnumPowerTransformerBlockType[6];

    static {
        int i = 0;
        int j = 0;

        for (EnumPowerTransformerBlockType value : EnumPowerTransformerBlockType.values()) {
            if (value.formed) {
                formedStructure[j] = value;
                j++;
            } else {
                rawStructure[i] = value;
                i++;
            }
            values[value.index] = value;
        }
    }

    public final int index;
    public final boolean formed;
    public final Vec3i offset;
    private final String name;

    EnumPowerTransformerBlockType(int index, boolean formed, String name, Vec3i offset) {
        this.index = index;
        this.formed = formed;
        this.name = name;
        this.offset = offset;
    }

    public static String[] getRawStructureNames() {
        int length = rawStructure.length;
        String[] ret = new String[length];

        for (int i = 0; i < length; i++) {
            ret[i] = rawStructure[i].name;
        }

        return ret;
    }

    public static EnumPowerTransformerBlockType fromInt(int in) {
        if (in >= values.length || in < 0) {
            return null;
        }

        return values[in];
    }

    @Override
    @Nonnull
    public String getName() {
        return this.name;
    }
}