package simElectricity.API.EnergyTile;

public interface ISEConductor extends ISEPlaceable,ISESimulatable{
    /**
     * Return the color of the wire
     * <p/>
     * 0 will allow any other wires connect to this wire
     * <p/>
     * Any other values will only connect to the certain value or 0
     */
    int getColor();
    
    public double getResistance();
}
