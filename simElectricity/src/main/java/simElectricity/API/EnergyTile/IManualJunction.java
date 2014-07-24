package simElectricity.API.EnergyTile;

import java.util.List;

/**
 * This interface can represent a manual junction, should be implemented by a tileEntity
 * (A little bit hard to explain clearly in few words, so see
 * <a href="https://github.com/RoyalAliceAcademyOfSciences/SimElectricity/wiki">SimElectricity wiki</a>
 * for more information)
 */
public interface IManualJunction extends IBaseComponent {
    /**
     * Return the neighbors of this junction
     * Do not have to be geographically next to this tileEntity!
     */
    void addNeighbors(List<IBaseComponent> list);


    /**
     * A advanced version of {@link simElectricity.API.EnergyTile.IBaseComponent#getResistance() getResistance()} in {@link simElectricity.API.EnergyTile.IBaseComponent}
     * <p/>
     * Return 0 in {@link simElectricity.API.EnergyTile.IBaseComponent#getResistance() getResistance()} in {@link simElectricity.API.EnergyTile.IBaseComponent} to make this function valid
     * <p/>
     * Should return the resistance between this tileEntity and the neighbor
     */
    float getResistance(IBaseComponent neighbor);
}
