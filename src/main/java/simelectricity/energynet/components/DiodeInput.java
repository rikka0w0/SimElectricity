package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEDiode;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class DiodeInput extends Tile<ISEDiode> implements ISESubComponent, ISEDiode {
    public DiodeOutput output;
    protected double Rs, Is, Vt, Vfw;
    private double const1, const2;

    public DiodeInput(ISEDiode dataProvider, TileEntity te) {
        super(dataProvider, te);
        this.output = new DiodeOutput(this, te);
    }

    @Override
    public ISESubComponent getComplement() {
        return this.output;
    }

    @Override
    public void updateComponentParameters() {
        Rs = this.dataProvider.getForwardResistance();
        Is = this.dataProvider.getSaturationCurrent();
        Vt = this.dataProvider.getThermalVoltage();

        this.const1 = this.Vt * Math.log(this.Vt / this.Is / this.Rs) + this.Vfw;
        this.const2 = -this.Vt / this.Rs * (1 - Math.log(this.Vt / this.Is / this.Rs)) - this.Is;
    }

    @Override
    public double getForwardResistance() {
        return this.Rs;
    }

    @Override
    public double getSaturationCurrent() {
        return this.Is;
    }

    @Override
    public double getThermalVoltage() {
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
