package simElectricity.API.Common;

import simElectricity.API.Energy;
import simElectricity.API.Util;
import simElectricity.API.EnergyTile.IComplexTile;

public abstract class TileComplexMachine extends TileInventoryMachine implements IComplexTile{
	protected boolean isAddedToEnergyNet = false;
	
	public void onLoad(){}
	public void onUnload(){}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			onLoad();
			Energy.postTileAttachEvent(this);
			this.isAddedToEnergyNet=true;
			Util.scheduleBlockUpdate(this);
		}
	}
	
	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet){
			onUnload();
			Energy.postTileDetachEvent(this);
			this.isAddedToEnergyNet=false;
		}
		
		super.invalidate();
	}
}
