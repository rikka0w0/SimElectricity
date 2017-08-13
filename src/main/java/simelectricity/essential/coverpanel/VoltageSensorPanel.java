package simelectricity.essential.coverpanel;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISEElectricalCoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;

public class VoltageSensorPanel implements ISEElectricalCoverPanel, ISERedstoneEmitterCoverPanel, ISEGuiCoverPanel{
	public boolean emitRedStoneSignal = false;
	public boolean inverted = false;
	public double thresholdVoltage = 100;
	
	private TileEntity hostTileEntity;
	private EnumFacing installedSide;
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
		return VoltageSensorRender.instance;
	}

	@Override
	public void setHost(TileEntity hostTileEntity, EnumFacing side) {
		this.hostTileEntity = hostTileEntity;
		this.installedSide = side;
	}
	
	@Override
	public ItemStack getDroppedItemStack() {
		return new ItemStack(ItemRegistry.itemMisc, 1, 1);
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
		
		WorldServer world = (WorldServer) hostTileEntity.getWorld();
		world.addScheduledTask(new Runnable(){
			@Override
			public void run() {
				checkRedStoneSignal();
			}
		});
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
			hostTileEntity.getWorld().neighborChanged(hostTileEntity.getPos().offset(installedSide), hostTileEntity.getBlockType(), hostTileEntity.getPos());
			
			return true;
		}
		
		return false;
	}
}
