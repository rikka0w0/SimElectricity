package simelectricity.essential.grid.transformer;

import javax.annotation.Nonnull;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumDistributionTransformerBlockType implements IStringSerializable {
	Pole10kVNormal(0, false, "pole10kvnormal"),
	Pole10kVSpec(1, false, "pole10kvspec"),
	Pole10kVAux(2, false, "pole10kvaux"),
	Pole415VNormal(3, false, "pole415vnormal"),
	Transformer(4, false, "transformer"),
	
	Pole10kV(5, true, "pole10kv"),
	PlaceHolder(6, true, "placeholder"),
	Pole415V(7, true, "pole415v");
	
    public static final PropertyEnum<EnumDistributionTransformerBlockType> property = PropertyEnum.create("blocktype", EnumDistributionTransformerBlockType.class);

    public static final EnumDistributionTransformerBlockType[] values = new EnumDistributionTransformerBlockType[8];
    static final EnumDistributionTransformerBlockType[] rawStructure = new EnumDistributionTransformerBlockType[5];
    static final EnumDistributionTransformerBlockType[] formedStructure = new EnumDistributionTransformerBlockType[3];

    static {
        int i = 0;
        int j = 0;

        for (EnumDistributionTransformerBlockType value : EnumDistributionTransformerBlockType.values()) {
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
    private final String name;

    EnumDistributionTransformerBlockType(int index, boolean formed, String name) {
        this.index = index;
        this.formed = formed;
        this.name = name;
    }

    public static String[] getRawStructureNames() {
        int length = rawStructure.length;
        String[] ret = new String[length];

        for (int i = 0; i < length; i++) {
            ret[i] = rawStructure[i].name;
        }

        return ret;
    }

    public static EnumDistributionTransformerBlockType fromInt(int in) {
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
