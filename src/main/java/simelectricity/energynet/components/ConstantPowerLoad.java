package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class ConstantPowerLoad extends Tile<ISEConstantPowerLoad> implements ISESubComponent<ISESubComponent<?>>, ISEConstantPowerLoad {
	private volatile double pRated, rMin, rMax;
    private volatile boolean enabled;

    public ConstantPowerLoad(ISEConstantPowerLoad dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public synchronized void updateComponentParameters() {
        pRated = this.dataProvider.getRatedPower();
        rMin = this.dataProvider.getMinimumResistance();
        rMax = this.dataProvider.getMaximumResistance();
        enabled = this.dataProvider.isOn();
    }

    @Override
    public synchronized double getRatedPower() {
        return this.pRated;
    }

    @Override
    public synchronized double getMinimumResistance() {
        return this.rMin;
    }

    @Override
    public synchronized double getMaximumResistance() {
        return this.rMax;
    }

    @Override
    public synchronized boolean isOn() {
        return this.enabled;
    }
}
