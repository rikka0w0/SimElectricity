package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;

import simelectricity.api.components.ISEDiode;
import simelectricity.api.node.ISESubComponent;

public class DiodeInput extends SEComponent.Tile<ISEDiode> implements ISESubComponent, ISEDiode{
	protected double Rs, Is, Vt, Vfw = 0;
	public DiodeOutput output;
	
	private double const1, const2;
	
	public DiodeInput(ISEDiode dataProvider, TileEntity te){
		super(dataProvider, te);
		output = new DiodeOutput(this, te);
	}

	@Override
	public ISESubComponent getComplement() {
		return output;
	}

	@Override
	public void updateComponentParameters() {
		this.Rs = dataProvider.getForwardResistance();
		this.Is = dataProvider.getSaturationCurrent();
		this.Vt = dataProvider.getThermalVoltage();
		
		const1 = Vt*Math.log(Vt/Is/Rs) + Vfw;
		const2 = - Vt/Rs*(1-Math.log(Vt/Is/Rs)) - Is;
	}

	@Override
	public double getForwardResistance() {
		return Rs;
	}

	@Override
	public double getSaturationCurrent() {
		return Is;
	}

	@Override
	public double getThermalVoltage() {
		return Vt;
	}
	
	public double calcId(double Vd){
		if (Vd > const1)
			return (Vd-Vfw) / Rs + const2;
		else
			return Is*Math.exp((Vd-Vfw)/Vt) - Is;
	}
	
	public double calcG(double Vd){
		if (Vd > const1)
			return 1.0D / Rs;
		else
			return Is/Vt*Math.exp((Vd-Vfw)/Vt);
	}
	
	@Override
	public String toString(){
		return "DIn";
	}
}
