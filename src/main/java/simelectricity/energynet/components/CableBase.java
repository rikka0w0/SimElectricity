package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISECableBase;

public abstract class CableBase<T extends ISECableBase> extends SEComponent.Tile<T> implements ISECableBase{
    private volatile double resistance;
    private volatile boolean hasShuntResistance;
    private volatile double shuntResistance;

    public CableBase(T dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public synchronized double getResistance() {
        return this.resistance;
    }

    @Override
    public synchronized boolean hasShuntResistance() {
        return this.hasShuntResistance;
    }

    @Override
    public synchronized double getShuntResistance() {
        return this.shuntResistance;
    }

    @Override
    public void updateComponentParameters() {
        resistance = this.dataProvider.getResistance();
        hasShuntResistance = this.dataProvider.hasShuntResistance();
        shuntResistance = this.dataProvider.getShuntResistance();
    }
}
