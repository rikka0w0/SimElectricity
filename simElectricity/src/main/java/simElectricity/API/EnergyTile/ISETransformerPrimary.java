package simElectricity.API.EnergyTile;

public interface ISETransformerPrimary extends ISESubComponent{
	public ISETransformerSecondary getSecondary();
	
	public double getRatio();
	
	/**
	 * The winding resistance of the transformer
	 * <p/>
	 * REFER TO SECONDARY!!!
	 */
	public double getResistance();
}
