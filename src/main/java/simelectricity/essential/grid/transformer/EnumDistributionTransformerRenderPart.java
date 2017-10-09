package simelectricity.essential.grid.transformer;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumDistributionTransformerRenderPart implements IStringSerializable {
	TransformerLeft("transformerleft"),
	TransformerRight("transformerright"),
	AuxLeft("auxleft"),
	AuxMiddle("auxmiddle"),
	AuxRight("auxright"),
	Pole415VLeft("pole415vleft"),
	Pole415VRight("pole415vright"),
	Pole10kVLeft("pole10kvleft"),
	Pole10kVRight("pole10kvright");

	public static final PropertyEnum<EnumDistributionTransformerRenderPart> property = PropertyEnum.create("renderpart", EnumDistributionTransformerRenderPart.class);
	
	private final String name;
	EnumDistributionTransformerRenderPart(String name){
		this.name = name;
	}
	
    public static EnumDistributionTransformerRenderPart fromInt(int in) {
        if (in >= EnumDistributionTransformerRenderPart.values().length || in < 0) {
            return null;
        }

        return EnumDistributionTransformerRenderPart.values()[in];
    }
	
	@Override
	public String getName() {
		return name;
	}
}
