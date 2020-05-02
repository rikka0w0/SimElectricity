package simelectricity.essential.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import rikka.librikka.ITileMeta;

public enum EnumBlockTypePole3 implements IStringSerializable, ITileMeta {
	pole(null, 0),
	crossarm10kvt0(TilePowerPole3.Pole10Kv.Type0.class,3),
	crossarm10kvt1(TilePowerPole3.Pole10Kv.Type1.class, 3),
	crossarm415vt0(TilePowerPole3.Pole415vType0.class, 4),
	branching10kv(TilePoleBranch.Type10kV.class, 3),
	branching415v(TilePoleBranch.Type415V.class, 4);

	public final Class<? extends TileEntity> teCls;
    public final int numOfConductor;

    EnumBlockTypePole3(Class<? extends TileEntity> teCls, int numOfConductor) {
    	this.teCls = teCls;
        this.numOfConductor = numOfConductor;
    }

    public static EnumBlockTypePole3 fromInt(int in) {
        if (in >= EnumBlockTypePole3.values().length || in < 0)
            return null;

        return EnumBlockTypePole3.values()[in];
    }

    @Override
    public String getName() {
        return this.name();
    }

	@Override
	public Class<? extends TileEntity> teCls() {
		return this.teCls;
	}
}
