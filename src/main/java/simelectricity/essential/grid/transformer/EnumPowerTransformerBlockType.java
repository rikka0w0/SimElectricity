package simelectricity.essential.grid.transformer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3i;
import rikka.librikka.ITileMeta;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;

public enum EnumPowerTransformerBlockType implements ITileMeta, IStringSerializable {
    Casing,
    IronCore,
    Winding,
    OilTank,
    OilTankSupport,
    OilPipe,

    PlaceholderPrimary(TilePowerTransformerPlaceHolder.Primary.class, null),        //Connection to primary
    Primary(TilePowerTransformerWinding.Primary.class, new Vec3i(3, 2, 1)),         //Primary node
    PlaceholderSecondary(TilePowerTransformerPlaceHolder.Secondary.class, null),    //Connection to secondary
    Secondary(TilePowerTransformerWinding.Secondary.class, new Vec3i(4, 2, 3)),     //Secondary node
    Placeholder(TilePowerTransformerPlaceHolder.class, null),
    Render(TilePowerTransformerPlaceHolder.Render.class, new Vec3i(3, 2, 2));       //Render the structure

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
        }
    }

    public final boolean formed;
    public final Class<? extends TileEntity> teCls;
    public final Vec3i offset;

    EnumPowerTransformerBlockType() {
        this.formed = false;
        this.teCls = null;
        this.offset = null;
    }
    
    EnumPowerTransformerBlockType(Class<? extends TileEntity> teCls, Vec3i offset) {
        this.formed = true;
        this.teCls = teCls;
        this.offset = offset;
    }
    
	@Override
	public Class<? extends TileEntity> teCls() {
		return teCls;
	}

	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
}