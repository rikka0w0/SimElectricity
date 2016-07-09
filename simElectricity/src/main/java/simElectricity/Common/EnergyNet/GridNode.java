package simElectricity.Common.EnergyNet;

import net.minecraft.nbt.NBTTagCompound;
import simElectricity.API.EnergyTile.IBaseComponent;

public class GridNode extends GridObject implements IBaseComponent{
	public double resistance;
	
	
	@Override
	public double getResistance() {return resistance;}
	
	
	public void toNBT(NBTTagCompound nbt){
		super.toNBT(nbt);
		nbt.setByte("type", (byte) 1); // 1 = Node
		nbt.setDouble("resistance", this.resistance);
	}
	
	public void fromNBT(NBTTagCompound nbt){
		super.fromNBT(nbt);
		this.resistance = nbt.getDouble("resistance");
	}
	
	public GridNode(NBTTagCompound nbt){
		this.fromNBT(nbt);
	}
	
	public GridNode(){
		
	}
}
