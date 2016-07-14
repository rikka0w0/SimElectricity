package simElectricity.Common.EnergyNet.Grid;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;

public class GridNode extends GridObject{
	public GridNode(GridDataProvider dataProvider) {
		super(dataProvider);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt, LinkedList<GridObject> neighbors) {	
		super.writeToNBT(nbt, neighbors);
	}
}
