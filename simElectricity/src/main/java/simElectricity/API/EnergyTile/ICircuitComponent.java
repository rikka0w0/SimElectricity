package simElectricity.API.EnergyTile;

/** This represents a circuit component in the simulation*/
public interface ICircuitComponent extends IBaseComponent{
	/**Return the max safety input voltage*/
	float getMaxSafeVoltage();

	/**When the input voltage exceed the safety range, do explosions here*/
	void onOverVoltage();
	
	/**Return 0 for sink(typically machines), other value for source(e.g generator)*/
	float getOutputVoltage();
}
