package simelectricity.energynet;

public abstract class EnergyEventBase {
    public static final int numOfPass = 7;
    public static final int GADD = 0,
    						GCHANGE = 1,
    						GDEL = 2,
    						TADD = 3,
    						TPARAMCHANGE = 4,
    						TCONCHANGE = 5,
    						TDEL = 6;
    
    public abstract void process(EnergyNetDataProvider dataProvider, int pass);
   
    
    //EnergyNet Update Strategy
    public abstract boolean changedStructure();
    
    public abstract boolean needUpdate();
}
