package simElectricity.API.EnergyTile;

/**
 * This interface is the base of components that participate in the simulation of energyNet,
 * but a normal simElectricity machine should not implement this interface >_<
 * See {@link simElectricity.API.EnergyTile.ICircuitComponent}, {@link simElectricity.API.EnergyTile.IEnergyTile} and {@link simElectricity.API.EnergyTile.IConductor} for more information
 * <p/>
 * Detailed instruction is available on
 * <a href="https://github.com/RoyalAliceAcademyOfSciences/SimElectricity/wiki">the wiki of simElectricity</a>
 */
public interface IBaseComponent {
    /**
     * Return the resistance of the machine
     * or
     * the internal resistance of the battery of the energy sink.
     * <p/>
     * NEVER return 0 (0 will crash the EnergyNet!),
     * but for {@link simElectricity.API.EnergyTile.IManualJunction}, 0 is allowed and mean something else, see {@link simElectricity.API.EnergyTile.IManualJunction} for further details
     * <p/>
     * Tips:
     * For a energy sink, the smaller resistance it has, the more energy it will consume.
     * For a conductor and energy source, the smaller resistance it has, the lower voltage drop it will have (better performance)
     */
    float getResistance();
}
