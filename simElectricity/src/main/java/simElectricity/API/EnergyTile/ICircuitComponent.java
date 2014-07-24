package simElectricity.API.EnergyTile;

/**
 * This represents a circuit component in the simulation.
 * 
 * Can be a source or sink, determined by getOutputVoltage();
 * Another important function is getResistance(), it can represent the resistance of a load or the internal resistance of a generator
 * 
 * Usually, this interface should NOT implemented by a tileEntity, this interface can just represents subComponents of a IComplexTile TileEntity
 * A normal machine or generator should implements IEnergyTile, see IEnergyTile for further information.
 */
public interface ICircuitComponent extends IBaseComponent {
    /**
     * Return 0 for sink(typically machines), other value for source(e.g generator)
     */
    float getOutputVoltage();
}
