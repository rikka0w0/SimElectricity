package simElectricity.API.EnergyTile;

/**
 * This interface is the base of components that participate in the simulation of energyNet,
 * but a normal simElectricity machine should not implement this interface >_<
 * See ICircuitComponent,IEnergyTile and IConductor for more information
 * 
 * Detailed instruction is available on the wiki of simElectricity
 */
public interface IBaseComponent {
    /**
     * Return the resistance of the machine 
     * or 
     * the internal resistance of the battery of the energy sink.
     * 
     * NEVER return 0 (0 will crash the EnergyNet!),
     * but for IManualJunction, 0 is allowed and mean something else, see IManualJunction for further details
     * 
     * Tips: 
     * For a energy sink, the smaller resistance it has, the more energy it will consume.
     * For a conductor and energy source, the smaller resistance it has, the lower voltage drop it will have (better performance)
     * 
     */
    float getResistance();
}
