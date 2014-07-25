package simElectricity.API.EnergyTile;

/**
 * This represents a circuit component in the simulation.
 * <p/>
 * Can be a source or sink, determined by {@link #getOutputVoltage()}
 * Another important function is {@link #getResistance()},
 * it can represent the resistance of a load or the internal resistance of a generator.
 * <p/>
 * Usually, this interface should NOT implemented by a tileEntity, this interface can just represents subComponents of a {@link simElectricity.API.EnergyTile.IComplexTile} TileEntity
 * A normal machine or generator should implements {@link simElectricity.API.EnergyTile.IEnergyTile}
 */
public interface ICircuitComponent extends IBaseComponent {
    /**
     * Return 0 for sink(typically machines), other value for source(e.g generator)
     */
    float getOutputVoltage();
}
