package simElectricity.API.Internal;

import net.minecraft.world.World;
import simElectricity.API.EnergyTile.ISESimulatable;

public interface IEnergyNetAgent {
    /**
     * Return the voltage of a ISESimulatable instance, RELATIVE TO GROUND!
     * 
     * @param node The ISESimulatable instance
     * @param world The world that the ISESimulatable instance is in
     * @return the voltage of the node, in volts
     */
	double getVoltage(ISESimulatable node, World world);
}
