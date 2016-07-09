package simElectricity.Common.EnergyNet;

import net.minecraft.nbt.NBTTagCompound;

public class GridTower extends GridObject{
	public GridTower(){}
	
	public GridTower(NBTTagCompound nbt){
		this.fromNBT(nbt);
	}
	
	public void toNBT(NBTTagCompound nbt){
		super.toNBT(nbt);
		nbt.setByte("type", (byte) 0); // 0 = Tower
	}
}
