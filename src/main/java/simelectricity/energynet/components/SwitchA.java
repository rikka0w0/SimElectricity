package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISESwitch;
import simelectricity.api.node.ISEPairedComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class SwitchA extends Tile<ISESwitch> implements ISEPairedComponent<SwitchB>, ISESwitch {
    protected volatile boolean isOn;
    protected volatile double resistance;

    protected volatile SwitchB B;

    public SwitchA(ISESwitch dataProvider, TileEntity te) {
        super(dataProvider, te);
        B = new SwitchB(this, te);
    }

    @Override
    public synchronized SwitchB getComplement() {
        return this.B;
    }

    @Override
    public synchronized void updateComponentParameters() {
        isOn = this.dataProvider.isOn();
        resistance = this.dataProvider.getResistance();
    }

    @Override
    public synchronized boolean isOn() {
        return this.isOn;
    }

    @Override
    public synchronized double getResistance() {
        return this.resistance;
    }
}
