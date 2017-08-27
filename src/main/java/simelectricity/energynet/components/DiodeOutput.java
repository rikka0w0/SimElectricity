package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEDiode;
import simelectricity.api.node.ISESubComponent;

public class DiodeOutput extends SEComponent implements ISESubComponent, ISEDiode {
    public DiodeInput input;

    public DiodeOutput(DiodeInput input, TileEntity te) {
        this.input = input;
        this.te = te;
    }

    @Override
    public ISESubComponent getComplement() {
        return this.input;
    }

    @Override
    public double getForwardResistance() {
        return this.input.Rs;
    }

    @Override
    public double getSaturationCurrent() {
        return this.input.Is;
    }

    @Override
    public double getThermalVoltage() {
        return this.input.Vt;
    }

    @Override
    public String toString() {
        return "DOut";
    }
}
