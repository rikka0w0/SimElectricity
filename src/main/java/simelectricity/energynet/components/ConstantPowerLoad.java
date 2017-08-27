package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class ConstantPowerLoad extends Tile<ISEConstantPowerLoad> implements ISESubComponent, ISEConstantPowerLoad {
    public double pRated, rMin, rMax;
    public boolean enabled;

    public ConstantPowerLoad(ISEConstantPowerLoad dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public void updateComponentParameters() {
        pRated = this.dataProvider.getRatedPower();
        rMin = this.dataProvider.getMinimumResistance();
        rMax = this.dataProvider.getMaximumResistance();
        enabled = this.dataProvider.isEnabled();
    }

    @Override
    public double getRatedPower() {
        return this.pRated;
    }

    @Override
    public double getMinimumResistance() {
        return this.rMin;
    }

    @Override
    public double getMaximumResistance() {
        return this.rMax;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
