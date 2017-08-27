package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class VoltageSource extends Tile<ISEVoltageSource> implements ISESubComponent, ISEVoltageSource {
    public double v, r;

    public VoltageSource(ISEVoltageSource dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public void updateComponentParameters() {
        v = this.dataProvider.getOutputVoltage();
        r = this.dataProvider.getResistance();
    }

    @Override
    public double getOutputVoltage() {
        return this.v;
    }

    @Override
    public double getResistance() {
        return this.r;
    }

    @Override
    public String toString() {
        return "V";
    }
}
