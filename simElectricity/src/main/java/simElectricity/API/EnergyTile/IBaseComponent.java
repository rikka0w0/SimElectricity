package simElectricity.API.EnergyTile;

public interface IBaseComponent {
    /**
     * Return the resistance of the machine or the internal resistance of the
     * battery For a energy sink, the smaller the resistance is, the faster it
     * sinks energy For a energy source, the smaller the resistance is, the
     * lower voltage drop it will have, this means it can drive heavier load
     */
    float getResistance();

    /**
     * For load: Return the max power the load can sink, 0 for infinite For
     * conductor: Return the max power the conductor can generate, limit the
     * maximum current, using P=IIR to calculate
     */
    int getMaxPowerDissipation();

    /**
     * Do conductor melt here
     */
    void onOverloaded();
}
