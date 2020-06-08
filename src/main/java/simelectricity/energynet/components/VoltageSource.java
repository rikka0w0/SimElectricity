package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class VoltageSource extends Tile<ISEVoltageSource> implements ISESubComponent<ISESubComponent<?>>, ISEVoltageSource {
    private volatile double v, r;
    private volatile boolean enabled;

    public VoltageSource(ISEVoltageSource dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public synchronized void updateComponentParameters() {
        v = this.dataProvider.getOutputVoltage();
        r = this.dataProvider.getResistance();
        enabled = this.dataProvider.isOn();
    }

    @Override
    public synchronized double getOutputVoltage() {
        return this.v;
    }

    @Override
    public synchronized double getResistance() {
        return this.r;
    }

	@Override
    public boolean isOn() {
        return enabled;
    }
    
    @Override
    public String toString() {
        return "V";
    }

}
