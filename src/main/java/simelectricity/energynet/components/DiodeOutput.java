package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEDiode;
import simelectricity.api.node.ISEPairedComponent;

public class DiodeOutput extends SEComponent implements ISEPairedComponent<DiodeInput>, ISEDiode {
	private volatile DiodeInput input;

    public DiodeOutput(DiodeInput input, TileEntity te) {
        this.input = input;
        this.te = te;
    }

    @Override
    public synchronized DiodeInput getComplement() {
        return this.input;
    }

    @Override
    public synchronized double getForwardResistance() {
        return this.input.Rs;
    }

    @Override
    public synchronized double getSaturationCurrent() {
        return this.input.Is;
    }

    @Override
    public synchronized double getThermalVoltage() {
        return this.input.Vt;
    }

    @Override
    public String toString() {
        return "DOut";
    }
}
