package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.ISECoverPanelRender;
import simelectricity.essential.api.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.cable.render.RenderVoltageSensorPanel;

public class VoltageSensorPanel implements ISERedstoneEmitterCoverPanel{
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
}
