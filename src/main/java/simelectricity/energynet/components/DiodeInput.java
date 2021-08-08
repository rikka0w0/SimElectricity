package simelectricity.energynet.components;

import net.minecraft.world.level.block.entity.BlockEntity;
import simelectricity.api.components.ISEDiode;
import simelectricity.api.node.ISEPairedComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class DiodeInput extends Tile<ISEDiode> implements ISEPairedComponent<DiodeOutput>, ISEDiode {
    protected volatile DiodeOutput output;
    protected volatile double Rs, Is, Vt, Vfw;
    private double const1, const2;

    public DiodeInput(ISEDiode dataProvider, BlockEntity te) {
        super(dataProvider, te);
        this.output = new DiodeOutput(this, te);
    }

    @Override
    public synchronized DiodeOutput getComplement() {
        return this.output;
    }

    @Override
    public synchronized void updateComponentParameters() {
        Rs = this.dataProvider.getForwardResistance();
        Is = this.dataProvider.getSaturationCurrent();
        Vt = this.dataProvider.getThermalVoltage();

        this.const1 = this.Vt * Math.log(this.Vt / this.Is / this.Rs) + this.Vfw;
        this.const2 = -this.Vt / this.Rs * (1 - Math.log(this.Vt / this.Is / this.Rs)) - this.Is;
    }

    @Override
    public synchronized double getForwardResistance() {
        return this.Rs;
    }

    @Override
    public synchronized double getSaturationCurrent() {
        return this.Is;
    }

    @Override
    public synchronized double getThermalVoltage() {
        return this.Vt;
    }

    public double calcId(double Vd) {
        if (Vd > this.const1)
            return (Vd - this.Vfw) / this.Rs + this.const2;
        else
            return this.Is * Math.exp((Vd - this.Vfw) / this.Vt) - this.Is;
    }

    public double calcG(double Vd) {
        if (Vd > this.const1)
            return 1.0D / this.Rs;
        else
            return this.Is / this.Vt * Math.exp((Vd - this.Vfw) / this.Vt);
    }

    @Override
    public String toString() {
        return "DIn";
    }
}
