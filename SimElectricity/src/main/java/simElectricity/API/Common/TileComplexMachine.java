package simElectricity.API.Common;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.Energy;
import simElectricity.API.Util;
import simElectricity.API.EnergyTile.IComplexTile;

public abstract class TileComplexMachine extends TileEntity implements IComplexTile{
	protected boolean isAddedToEnergyNet = false;
	
	public void onLoad(){}
	public void onUnload(){}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			Energy.postTileAttachEvent(this);
			onLoad();
			this.isAddedToEnergyNet=true;
			Util.scheduleBlockUpdate(this);
		}
	}
	
	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet){
			Energy.postTileDetachEvent(this);
			onUnload();
			this.isAddedToEnergyNet=false;
		}
		
		super.invalidate();
	}
}
