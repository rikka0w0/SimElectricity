package simelectricity.essential.grid;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumBlockTypePole3 implements IStringSerializable {
	Pole(0, "pole", true),
	Crossarm10KvT0(1, "crossarm10kvt0", false),
	Crossarm10KvT1(2, "crossarm10kvt1", false)
	;
	public static final PropertyEnum<EnumBlockTypePole3> property = PropertyEnum.create("blocktype", EnumBlockTypePole3.class);
	
	public static final EnumBlockTypePole3[] values;
	public static final String[] names;
	
	public final int index;
	private final String name;
	public final boolean ignoreFacing;
	
	static {
		values = new EnumBlockTypePole3[values().length];
		names = new String[values().length];
		for (EnumBlockTypePole3 value: values()) {
			int index = value.ordinal();
			values[index] = value;
			names[index] = value.name;
		}
	}
	
	private EnumBlockTypePole3(int index, String name, boolean ignoreFacing) {
		this.index = index;
		this.name = name;
		this.ignoreFacing = ignoreFacing;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public static EnumBlockTypePole3 fromInt(int in) {
		if (in >= values.length || in < 0)
			return null;
		
		return values[in];
	}
}
