package simelectricity.energynet;

public abstract class EnergyEventBase {
    public static final int numOfPriority = 6;
    public final int priority;
    public boolean changedStructure,    //This event changes connections and/or component parameters
            needUpdate;    //This event only change component parameters

    protected EnergyEventBase(int priority) {
        this.priority = priority;
    }

    public abstract void process(EnergyNetDataProvider dataProvider);
}
