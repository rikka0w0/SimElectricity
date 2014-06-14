package simElectricity.Test;

import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.Common.TileComplexMachine;
import simElectricity.API.EnergyTile.ICircuitComponent;

public class TileBatteryBox extends TileComplexMachine{
	public static class CircuitComponent implements ICircuitComponent{
		public float resistance=1;
		public float voltage=0;
		
		@Override
		public float getResistance() {return resistance;}

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
	
	public static int maxEnergy=10000;
	public static int outputVoltage=230;
	
	public int progress=50;
	public int energy=0;
	public CircuitComponent sink,source; 
	public ForgeDirection sinkDirection,sourceDirection;
	
	@Override 
	public void onLoad(){
		sink = new CircuitComponent();
		source = new CircuitComponent();
		
		sink.resistance=100;
		sink.voltage=0;
		source.resistance=1;
		source.voltage=outputVoltage;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
	
		if(worldObj.isRemote)
			return;
		
		float powerAbsorbed = Energy.getPower(sink,worldObj)*0.05F;
		
		if(energy+powerAbsorbed<maxEnergy){
			energy+=powerAbsorbed;
		}
		
		float workDone = Energy.getWorkDonePerTick(source, worldObj);
		
		if(source.voltage==outputVoltage){
			if(energy>0){
				energy-=workDone;		
				if(energy<0)
					energy=0;
			}else{
				source.voltage=0;
				source.resistance=10000000;
				Energy.postTileChangeEvent(this);		
			}			
		}else{
			if(energy>0){
				source.voltage=outputVoltage;
				source.resistance=1;
				Energy.postTileChangeEvent(this);					
			}
		}
		

		
		progress=(energy*100/maxEnergy);
	}
	
	@Override
	public ICircuitComponent getCircuitComponent(ForgeDirection side) {
		if(side==sinkDirection)
			return sink;
		else if(side==sourceDirection)
			return source;
		else
			return null;
	}

	@Override
	public boolean canConnectOnSide(ForgeDirection side) {
		if (side==sinkDirection||side==sourceDirection)
			return true;
		else 
			return false;
	}
	
	@Override
	public int getInventorySize() {return 2;}
}

