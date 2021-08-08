package simelectricity.essential.grid.transformer;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.StringRepresentable;
import rikka.librikka.ITileMeta;
import simelectricity.essential.grid.TileMultiBlockPlaceHolder;

public enum EnumDistributionTransformerBlockType implements ITileMeta, StringRepresentable {
	Pole10kVNormal,
	Pole10kVSpec,
	Pole10kVAux,
	Pole415VNormal,
	Transformer,
	
	Pole10kV(TileDistributionTransformer.Pole10kV.class),
	PlaceHolder(TileMultiBlockPlaceHolder.class),
	Pole415V(TileDistributionTransformer.Pole415V.class);

	public final Class<? extends BlockEntity> teCls;
    public final boolean formed;

    EnumDistributionTransformerBlockType() {
    	this.teCls = null;
    	this.formed = false;
    }
    
    EnumDistributionTransformerBlockType(Class<? extends BlockEntity> teCls) {
        this.teCls = teCls;
        this.formed = true;
    }

	@Override
	public String toString() {
		return getSerializedName();
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
    
	@Override
	public Class<? extends BlockEntity> teCls() {
		return teCls;
	}
	
	public static EnumDistributionTransformerBlockType forName(String name) {
		for(EnumDistributionTransformerBlockType type: EnumDistributionTransformerBlockType.values()) {
			if (type.getSerializedName().equals(name.toLowerCase()))
				return type;
		}
		return null;
	}
}
