package SimElectricity.API;

public interface IConductor extends IBaseComponent{
	int getInsulationBreakdownVoltage();
	void onInsulationBreakdown();
}
