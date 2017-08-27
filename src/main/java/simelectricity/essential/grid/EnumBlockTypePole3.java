package simelectricity.essential.grid;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumBlockTypePole3 implements IStringSerializable {
	Pole(0, "pole", true, 0),
	Crossarm10kVT0(1, "crossarm10kvt0", false, 3),
	Crossarm10kVT1(2, "crossarm10kvt1", false, 3),
	Crossarm415VT0(3, "crossarm415vt0", false, 4);
	
	public static final PropertyEnum<EnumBlockTypePole3> property = PropertyEnum.create("blocktype", EnumBlockTypePole3.class);
	
	public static final EnumBlockTypePole3[] values;
	public static final String[] names;
	
	public final int index;
	private final String name;
	public final boolean ignoreFacing;
	public final int numOfConductor;
	
	static {
		values = new EnumBlockTypePole3[values().length];
		names = new String[values().length];
		for (EnumBlockTypePole3 value: values()) {
			int index = value.ordinal();
			values[index] = value;
			names[index] = value.name;
		}
	}
	
	private EnumBlockTypePole3(int index, String name, boolean ignoreFacing, int numOfConductor) {
		this.index = index;
		this.name = name;
		this.ignoreFacing = ignoreFacing;
		this.numOfConductor = numOfConductor;
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
