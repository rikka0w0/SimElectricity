package simelectricity.essential.grid.transformer;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumBlockType implements IStringSerializable{
	Casing(0, false, "casing"),
	IronCore(1, false, "ironcore"),
	Winding(2, false, "winding"),
	OilTank(3, false, "oiltank"),
	OilTankSupport(4, false, "oiltanksupport"),
	OilPipe(5, false, "oilpipe"),
	
	PlaceholderPrimary(6, true, "placeholderprimary"),		//Connection to primary
	Primary(7,true, "primary"),								//Primary node
	PlaceholderSecondary(8, true, "placeholdersecondary"),	//Connection to secondary
	Secondary(9, true, "secondary"),							//Secondary node
	Placeholder(10, true, "placeholder"),
	Render(11, true, "render");								//Render the structure
	
	public static final PropertyEnum<EnumBlockType> property = PropertyEnum.create("blocktype", EnumBlockType.class);
	
	public static final EnumBlockType[] values = new EnumBlockType[12];
	public static final EnumBlockType[] rawStructure = new EnumBlockType[6];
	public static final EnumBlockType[] formedStructure = new EnumBlockType[6];
	
	static {
		int i=0, j=0;
		for (EnumBlockType value: values()) {
			if (value.formed) {
				formedStructure[j] = value;
				j++;
			}else {
				rawStructure[i] = value;
				i++;
			}
			values[value.index] = value;
		}
	}
	
	public static String[] getRawStructureNames() {
		String[] ret = new String[rawStructure.length];
		
		for (int i=0; i<rawStructure.length; i++)
			ret[i] = rawStructure[i].name;
		
		return ret;
	}
	
	public final int index;
	private final String name;
	public final boolean formed;
	
	private EnumBlockType(int index, boolean formed, String name) {
		this.index = index;
		this.formed = formed;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public static EnumBlockType fromInt(int in) {
		if (in >= values.length || in < 0)
			return null;
		
		return values[in];
	}
}