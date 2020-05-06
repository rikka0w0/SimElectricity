package simelectricity.essential.grid.transformer;

import net.minecraft.tileentity.TileEntity;
import rikka.librikka.ITileMeta;

public enum EnumDistributionTransformerBlockType implements ITileMeta {
	Pole10kVNormal(0, false, "pole10kvnormal"),
	Pole10kVSpec(1, false, "pole10kvspec"),
	Pole10kVAux(2, false, "pole10kvaux"),
	Pole415VNormal(3, false, "pole415vnormal"),
	Transformer(4, false, "transformer"),
	
	Pole10kV(5, true, "pole10kv"),
	PlaceHolder(6, true, "placeholder"),
	Pole415V(7, true, "pole415v");

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

	@Override
	public Class<? extends TileEntity> teCls() {
		// TODO Auto-generated method stub
		return null;
	}
}
