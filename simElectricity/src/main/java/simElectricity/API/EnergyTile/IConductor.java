package simElectricity.API.EnergyTile;

/**
 * A wire should implements this interface, the getResistance() should return the resistance(Ohm) of a block of wire, see SimElectricity for more information
 */
public interface IConductor extends IBaseComponent {
    /**
     * Return the color of the wire
     * </p>
     * 0 will allow any other wires connect to this wire
     * </p>
     * Any other values will only connect to the certain value or 0
     */
    int getColor();
}
