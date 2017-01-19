package simElectricity.API.EnergyTile;

public interface ISERegulatorController extends ISESimulatable{
	ISERegulatorInput getInput();
	
	/***
	 * @return Maximum allowed duty cycle </p>
	 * recommended value: 1
	 */
	double getDMax();
	
	/***
	 * @return Duty cycle clamp resistor</p>
	 * recommended value: 1
	 * large value: poor clamping effect
	 * small value: poor convergence behavior
	 */
	double getRc();
	
	/***
	 * @return The gain of the error amplifier</p>
	 * recommended value: 1e5
	 * large value: poor convergence behavior
	 * small value: large output voltage error
	 */
	double getGain();	
	
	/***
	 * @return The output resistance of the error amplifier</p>
	 * recommended value: 1e6
	 */
	double getRs();
}