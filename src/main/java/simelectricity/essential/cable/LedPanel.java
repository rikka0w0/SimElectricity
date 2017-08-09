package simelectricity.essential.cable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.api.ISEIuminousCoverPanelHost;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISEElectricalLoadCoverPanel;
import simelectricity.essential.api.coverpanel.ISEIuminousCoverPanel;

public class LedPanel implements ISEElectricalLoadCoverPanel, ISEIuminousCoverPanel{
	private byte lightLevel;
	private TileEntity hostTileEntity;
	
	@Override
	public boolean isHollow() {	return false;}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "LedPanel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return null;	//TODO: Missing Render
	}

	@Override
	public void setHost(TileEntity hostTileEntity, EnumFacing side) {
		this.hostTileEntity = hostTileEntity;
	}
	
	@Override
	public void onPlaced(double voltage) {}

	@Override
	public double getResistance(){
		return 9900;
	}
	
	@Override
	public void onEnergyNetUpdate(double voltage) {
		double power = voltage*voltage/getResistance() / 0.3;
		if (power > 15)
			power = 15;
		byte lightLevel = (byte) power;
		
		if (this.lightLevel != lightLevel){
			//If light value changes, send a sync. packet to client
			this.lightLevel = lightLevel;
			
			if (hostTileEntity instanceof ISEIuminousCoverPanelHost)
				((ISEIuminousCoverPanelHost) hostTileEntity).onLightValueUpdated();
		}	
	}

	/////////////////////////
	///ISEIuminousCoverPanel
	/////////////////////////
	@Override
	public byte getLightValue() {
		return this.lightLevel;
	}
}
