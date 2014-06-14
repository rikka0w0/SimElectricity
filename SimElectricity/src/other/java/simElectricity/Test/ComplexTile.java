package simElectricity.Test;

import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileComplexMachine;
import simElectricity.API.EnergyTile.ICircuitComponent;

public class ComplexTile extends TileComplexMachine{
	public class Tile_Source implements ICircuitComponent{
		float voltage;
		public Tile_Source(float voltage){
			this.voltage=voltage;
		}
		
		@Override
		public float getResistance() {return 1;}

		@Override
		public int getMaxPowerDissipation() {return 0;}

		@Override
		public void onOverloaded() {}

		@Override
		public float getMaxSafeVoltage() {return 0;}

		@Override
		public void onOverVoltage() {}

		@Override
		public float getOutputVoltage() {return voltage;}
		
	}
	
	
	public Tile_Source Tile_Up = new Tile_Source(12);
	public Tile_Source Tile_NORTH = new Tile_Source(240);	
	
	@Override
	public ICircuitComponent getCircuitComponent(ForgeDirection side) {
		switch (side){
		case UP:
			return Tile_Up;
		case NORTH:
			return Tile_NORTH;
		default:
			return null;
		}
	}

	@Override
	public int getInventorySize() {	return 0;}

	@Override
	public boolean canConnectOnSide(ForgeDirection side) {
		switch (side){
		case UP:
			return true;
		case NORTH:
			return true;
		default:
			return false;
		}
	}
}