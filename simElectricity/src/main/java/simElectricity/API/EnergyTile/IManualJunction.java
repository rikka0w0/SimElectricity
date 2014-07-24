package simElectricity.API.EnergyTile;

import java.util.List;

/**
 * This interface can represent a manual junction, should be implemented by a tileEntity
 * (A little bit hard to explain clearly in few words, so see simElectricity wiki for more information)
 * 
 * */
public interface IManualJunction extends IBaseComponent {
	/**
	 * Return the neighbors of this junction
	 * Do not have to be geographically next to this tileEntity!
	 * 
	 * */
    void addNeighbors(List<IBaseComponent> list);
    
    
    /**
     * A advanced version of getResistance() in IBaseComponent
     * 
     * Return 0 in getResistance() of IBaseComponent to make this function valid
     * 
     * Should return the resistance between this tileEntity and the neighbor
     * 
     * */
    float getResistance(IBaseComponent neighbor);
}
