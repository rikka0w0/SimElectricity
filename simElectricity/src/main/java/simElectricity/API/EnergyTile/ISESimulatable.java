package simElectricity.API.EnergyTile;

import simElectricity.API.DataProvider.ISEComponentDataProvider;

/**
 * A object which is able to evolve in the circuit simulation
 */
public interface ISESimulatable {
	public ISESubComponent getComplement();
}
