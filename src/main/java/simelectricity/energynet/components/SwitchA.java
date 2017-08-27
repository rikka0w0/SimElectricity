package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISESwitch;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class SwitchA extends Tile<ISESwitch> implements ISESubComponent, ISESwitch {
    public boolean isOn;
    public double resistance;

    public SwitchB B;

    public SwitchA(ISESwitch dataProvider, TileEntity te) {
        super(dataProvider, te);
        B = new SwitchB(this, te);
    }

    @Override
    public ISESubComponent getComplement() {
        return this.B;
    }

    @Override
    public void updateComponentParameters() {
        isOn = this.dataProvider.isOn();
        resistance = this.dataProvider.getResistance();
    }

    @Override
    public boolean isOn() {
        return this.isOn;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }
}
