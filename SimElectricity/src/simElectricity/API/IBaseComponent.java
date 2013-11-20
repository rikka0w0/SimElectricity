package simElectricity.API;

public interface IBaseComponent {
	int getResistance();

	void onOverloaded();

	int getMaxPowerDissipation();
}
