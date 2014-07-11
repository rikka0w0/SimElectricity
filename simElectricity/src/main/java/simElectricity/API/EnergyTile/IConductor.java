package simElectricity.API.EnergyTile;

public interface IConductor extends IBaseComponent {
    /**
     * Return the maximum voltage the insulation layer can stand, 0 for no insulator
     * when there's no insulator, the wire will shock creatures near it
     */
    int getInsulationBreakdownVoltage();

    /**
     * Do burn up the insulator here
     */
    void onInsulationBreakdown();
}
