package simelectricity.essential.cable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.ISECoverPanelRender;
import simelectricity.essential.api.ISEGuiCoverPanel;
import simelectricity.essential.api.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.cable.gui.ContainerVoltageSensor;
import simelectricity.essential.cable.gui.GuiVoltageSensor;
import simelectricity.essential.cable.render.RenderVoltageSensorPanel;

public class VoltageSensorPanel implements ISERedstoneEmitterCoverPanel, ISEGuiCoverPanel{
	private boolean inverted = false;
	private double threshold = 100;
	private boolean emitRedStoneSignal = false;
	
	@Override
	public boolean isProvidingWeakPower(){
		return emitRedStoneSignal;
	}
	
	@Override
	public boolean checkRedStoneSignal(TileEntity te, double voltage){
		boolean changed = false;
		boolean emitRedStoneSignal = voltage > 100;
		
		if (emitRedStoneSignal != this.emitRedStoneSignal){
			this.emitRedStoneSignal = emitRedStoneSignal;
			changed = true;
		}
		
		return changed;
	}
	
	@Override
	public boolean isHollow() {
		return false;
	}
	
	@Override
	public ItemStack getCoverPanelItem() {
		return new ItemStack(ItemRegistry.itemMisc, 1, 1);
	}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "VoltageSensorPanel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return RenderVoltageSensorPanel.instance;
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
}
