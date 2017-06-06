package simelectricity.api.components;

public interface ISETransformerData extends ISEComponentDataProvider{
	public double getRatio();
	
	/**
	 * The winding resistance of the transformer
	 * <p/>
	 * REFER TO SECONDARY!!!
	 */
	public double getInternalResistance();
}
