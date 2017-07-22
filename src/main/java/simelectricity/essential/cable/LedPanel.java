package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.ISECoverPanelRender;
import simelectricity.essential.api.ISEElectricalLoadCoverPanel;
import simelectricity.essential.api.ISEIuminousCoverPanel;
import simelectricity.essential.api.ISEIuminousCoverPanelHost;
import simelectricity.essential.cable.render.RenderLedPanel;

public class LedPanel implements ISEElectricalLoadCoverPanel, ISEIuminousCoverPanel{
	private byte lightLevel;
	private TileEntity hostTileEntity;
	
	@Override
	public boolean isHollow() {	return false;}
	
	@Override
	public ItemStack getCoverPanelItem() {
		return new ItemStack(ItemRegistry.itemMisc, 1, 0);
	}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "LedPanel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return RenderLedPanel.instance;
	}

	@Override
	public void setHost(TileEntity hostTileEntity, ForgeDirection side) {
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
