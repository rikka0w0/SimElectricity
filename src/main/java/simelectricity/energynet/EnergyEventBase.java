package simelectricity.energynet;

public abstract class EnergyEventBase {
	public static final int numOfPriority = 4;
	
	public boolean 	changedStructure = false,	//This event changes connections and/or component parameters
						  needUpdate = false;	//This event only change component parameters
	
	public final int priority;
	
	protected EnergyEventBase(int priority) {
		this.priority = priority;
	}
	
	public abstract void process(EnergyNetDataProvider dataProvider);
}
