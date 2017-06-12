package simelectricity.api.components;


public interface ISESwitch extends ISEComponentParameter{
	boolean isOn();
	double getResistance();
}
