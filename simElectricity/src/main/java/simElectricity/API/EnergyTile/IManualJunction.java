package simElectricity.API.EnergyTile;

import java.util.List;

public interface IManualJunction extends IBaseComponent {
    void addNeighbors(List<IBaseComponent> list);
}
