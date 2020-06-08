package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISESwitch;
import simelectricity.api.node.ISEPairedComponent;

public class SwitchB extends SEComponent implements ISEPairedComponent<SwitchA>, ISESwitch {
    protected volatile SwitchA A;

    public SwitchB(SwitchA A, TileEntity te) {
        this.A = A;
        this.te = te;
    }

    @Override
    public synchronized SwitchA getComplement() {
        return this.A;
    }

    @Override
    public synchronized boolean isOn() {
        return this.A.isOn;
    }

    @Override
    public synchronized double getResistance() {
        return this.A.resistance;
    }
}
