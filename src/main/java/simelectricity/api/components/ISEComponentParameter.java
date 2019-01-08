package simelectricity.api.components;


/**
 * ISEComponentParameter provides parameters for creating circuit nodes.
 * Additionally, the instance returned by {@link simelectricity.api.internal.ISEEnergyNetAgent.newComponent} also implements
 * this interface, which is for retrieving simulation results and cached parameters.
 *
 *
 * Normally, in one machine or cable, there should be 2 instances implementing this interface,
 * one is from newComponent() as mentioned above, it stores the current state e.g. last simulation results
 * and parameter used for the last simulation step;
 * the other is implemented by the data provider of that machine (the first parameter of newComponent(),
 * sometime it is implemented by the host TileEntity), which determines the parameters used for the next simulation step.
 */
public interface ISEComponentParameter {

}
