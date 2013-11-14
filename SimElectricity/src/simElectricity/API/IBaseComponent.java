package simElectricity.API;

public interface IBaseComponent {
	public static final int powerSource = 1;
	public static final int powerSink = 2;
	public static final int conductor = 3;

	int getResistance();

	void onOverloaded();

	int getMaxPowerDissipation();
}
