package simElectricity.API.EnergyTile;

public interface ISETransformerPrimary extends ISESubComponent{
	public ISETransformerSecondary getSecondary();
	
	public double getRatio();
	
	public double getResistance();
}
