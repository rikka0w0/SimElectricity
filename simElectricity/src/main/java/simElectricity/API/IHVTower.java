package simElectricity.API;

public interface IHVTower {
	float[] offsetArray();

	int[] getNeighborInfo();

	int getFacing();
}
