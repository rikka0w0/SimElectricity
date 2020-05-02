package simelectricity.essential.coverpanel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISEElectricalCoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.items.ItemMisc;

public class VoltageSensorPanel implements ISEElectricalCoverPanel, ISERedstoneEmitterCoverPanel, ISEGuiCoverPanel{
    public boolean emitRedStoneSignal;
    public boolean inverted;
    public double thresholdVoltage = 100;

    private TileEntity hostTileEntity;
    private Direction installedSide;
    private double voltage;

    public VoltageSensorPanel() {
    }

    public VoltageSensorPanel(CompoundNBT nbt) {
        this.inverted = nbt.getBoolean("inverted");
        this.thresholdVoltage = nbt.getDouble("thresholdVoltage");
    }

    /////////////////////////
    ///ISERedstoneEmitterCoverPanel
    /////////////////////////
    @Override
    public boolean isProvidingWeakPower() {
        return this.emitRedStoneSignal;
    }

    /////////////////////////
    ///ISECoverPanel
    /////////////////////////
    @Override
    public boolean isHollow() {
        return false;
    }

    @Override
    public void toNBT(CompoundNBT nbt) {
        nbt.putString("coverPanelType", "VoltageSensorPanel");

        nbt.putBoolean("inverted", this.inverted);
        nbt.putDouble("thresholdVoltage", this.thresholdVoltage);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ISECoverPanelRender getCoverPanelRender() {
        return VoltageSensorRender.instance;
    }

    @Override
    public void setHost(TileEntity hostTileEntity, Direction side) {
        this.hostTileEntity = hostTileEntity;
        installedSide = side;
    }

    @Override
    public ItemStack getDroppedItemStack() {
        return new ItemStack(ItemRegistry.itemMisc[1], 1);
    }

    @Override
    public void onPlaced(double voltage) {
        this.voltage = voltage;

        this.checkRedStoneSignal();
    }

    /////////////////////////
    ///ISEGuiCoverPanel
    /////////////////////////
    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerVoltageSensor(this, windowId);
    }

	@Override
	public ITextComponent getDisplayName() {
		return ItemRegistry.itemMisc[ItemMisc.ItemType.voltagesensor.ordinal()].getName();
	}

    /////////////////////////
    ///ISEElectricalCoverPanel
    /////////////////////////
    @Override
    public void onEnergyNetUpdate(double voltage) {
        this.voltage = voltage;

        Utils.enqueueServerWork(this::checkRedStoneSignal);
    }

    /**
     * @return true - redstone signal has changed
     */
    public boolean checkRedStoneSignal() {
        boolean emitRedStoneSignal = this.voltage > this.thresholdVoltage;

        emitRedStoneSignal ^= this.inverted;

        if (emitRedStoneSignal != this.emitRedStoneSignal) {
            this.emitRedStoneSignal = emitRedStoneSignal;
            //Notify neighbor blocks if redstone signal polarity changes
            this.hostTileEntity.getWorld().neighborChanged(this.hostTileEntity.getPos().offset(this.installedSide), this.hostTileEntity.getBlockState().getBlock(), this.hostTileEntity.getPos());

            return true;
        }

        return false;
    }
}
