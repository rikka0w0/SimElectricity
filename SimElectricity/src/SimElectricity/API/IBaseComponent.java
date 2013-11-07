package SimElectricity.API;

public interface IBaseComponent {
	int voltage=0;
	int getResistance();
	void onOverloaded();
	int getMaxPowerDissipation();
}
