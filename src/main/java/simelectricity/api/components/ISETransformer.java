package simelectricity.api.components;


public interface ISETransformer extends ISEComponentParameter{
	public double getRatio();
	
	/**
	 * The winding resistance of the transformer
	 * <p/>
	 * REFER TO SECONDARY!!!
	 */
	public double getInternalResistance();
}
