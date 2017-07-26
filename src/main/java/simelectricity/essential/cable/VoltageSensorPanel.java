package simelectricity.essential.cable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.api.ISECoverPanelRender;
import simelectricity.essential.api.ISEElectricalCoverPanel;
import simelectricity.essential.api.ISEGuiCoverPanel;
import simelectricity.essential.api.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.cable.gui.ContainerVoltageSensor;
import simelectricity.essential.cable.gui.GuiVoltageSensor;
import simelectricity.essential.cable.render.RenderVoltageSensorPanel;

public class VoltageSensorPanel implements ISEElectricalCoverPanel, ISERedstoneEmitterCoverPanel, ISEGuiCoverPanel{
	public boolean emitRedStoneSignal = false;
	public boolean inverted = false;
	public double thresholdVoltage = 100;
	
	private TileEntity hostTileEntity;
	private ForgeDirection installedSide;
	private double voltage;
	
	public VoltageSensorPanel(){}
	
	public VoltageSensorPanel(NBTTagCompound nbt) {
		inverted = nbt.getBoolean("inverted");
		thresholdVoltage = nbt.getDouble("thresholdVoltage");
	}
	
	/////////////////////////
	///ISERedstoneEmitterCoverPanel
	/////////////////////////
	@Override
	public boolean isProvidingWeakPower(){
		return emitRedStoneSignal;
	}
	
	/////////////////////////
	///ISECoverPanel
	/////////////////////////
	@Override
	public boolean isHollow() {
		return false;
	}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "VoltageSensorPanel");
		
		nbt.setBoolean("inverted", inverted);
		nbt.setDouble("thresholdVoltage",thresholdVoltage);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return RenderVoltageSensorPanel.instance;
	}

	@Override
	public void setHost(TileEntity hostTileEntity, ForgeDirection side) {
		this.hostTileEntity = hostTileEntity;
		this.installedSide = side;
	}
	
	@Override
	public void onPlaced(double voltage) {
		this.voltage = voltage;
		
		checkRedStoneSignal();
	}
	/////////////////////////
	///ISEGuiCoverPanel
	/////////////////////////
	@Override
	public Container getServerContainer(TileEntity te) {
		return new ContainerVoltageSensor(this, te);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getClientGuiContainer(TileEntity te) {
		return new GuiVoltageSensor(getServerContainer(te));
	}

	/////////////////////////
	///ISEElectricalCoverPanel
	/////////////////////////
	@Override
	public void onEnergyNetUpdate(double voltage) {
		this.voltage = voltage;
		
		checkRedStoneSignal();
	}
	
	/**
	 * @return true - redstone signal has changed
	 */
	public boolean checkRedStoneSignal(){
		boolean emitRedStoneSignal = voltage > thresholdVoltage;
		
		emitRedStoneSignal ^= inverted;
		
		if (emitRedStoneSignal != this.emitRedStoneSignal){
			this.emitRedStoneSignal = emitRedStoneSignal;
			
			//Notify neighbor blocks if redstone signal polarity changes
			hostTileEntity.getWorldObj().notifyBlocksOfNeighborChange(
					hostTileEntity.xCoord, hostTileEntity.yCoord, hostTileEntity.zCoord, hostTileEntity.getBlockType(), installedSide.getOpposite().ordinal());
			return true;
		}
		
		return false;
	}
}
