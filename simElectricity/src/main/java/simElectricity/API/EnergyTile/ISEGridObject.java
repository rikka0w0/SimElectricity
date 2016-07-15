package simElectricity.API.EnergyTile;

import java.util.LinkedList;

public interface ISEGridObject {
	int getXCoord();
	int getYCoord();
	int getZCoord();
	LinkedList<ISEGridObject> getNeighborList();
	double getResistance(ISEGridObject neighbor);
}
