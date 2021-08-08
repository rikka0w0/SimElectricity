package simelectricity.energynet.components;

import net.minecraft.world.level.block.entity.BlockEntity;
import simelectricity.api.components.ISEConstantPowerSource;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class ConstantPowerSource extends Tile<ISEConstantPowerSource> implements ISESubComponent<ISESubComponent<?>>, ISEConstantPowerSource {
    private volatile double pRated, vMin, vMax;
    private volatile boolean enabled;

    public ConstantPowerSource(ISEConstantPowerSource dataProvider, BlockEntity te) {
        super(dataProvider, te);
    }

    @Override
    public double getRatedPower() {
        return pRated;
    }

    @Override
    public double getMinimumOutputVoltage() {
        return vMin;
    }

    @Override
    public double getMaximumOutputVoltage() {
        return vMax;
    }

    @Override
    public boolean isOn() {
        return enabled;
    }

    @Override
    public void updateComponentParameters() {
        pRated = this.dataProvider.getRatedPower();
        vMin = this.dataProvider.getMinimumOutputVoltage();
        vMax = this.dataProvider.getMaximumOutputVoltage();
        enabled = this.dataProvider.isOn();
    }
}
