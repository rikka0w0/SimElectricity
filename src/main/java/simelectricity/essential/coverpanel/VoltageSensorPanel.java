package simelectricity.essential.coverpanel;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEElectricalCoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.items.ItemPanel;

public class VoltageSensorPanel implements ISEElectricalCoverPanel, ISERedstoneEmitterCoverPanel, ISEGuiCoverPanel{
    public boolean emitRedStoneSignal;
    public boolean inverted;
    public double thresholdVoltage = 100;

    private BlockEntity hostTileEntity;
    private Direction installedSide;
    private double voltage;

    public VoltageSensorPanel() {
    }

    public VoltageSensorPanel(CompoundTag nbt) {
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
    public void toNBT(CompoundTag nbt) {
        nbt.putBoolean("inverted", this.inverted);
        nbt.putDouble("thresholdVoltage", this.thresholdVoltage);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T extends ISECoverPanel> ISECoverPanelRender<T> getCoverPanelRender() {
        return VoltageSensorRender.instance.cast();
    }

    @Override
    public void setHost(BlockEntity hostTileEntity, Direction side) {
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
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        return new ContainerVoltageSensor(this, windowId);
    }

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent(ItemRegistry.itemMisc[ItemPanel.ItemType.voltagesensor.ordinal()].getDescriptionId());
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
            this.hostTileEntity.getLevel().neighborChanged(this.hostTileEntity.getBlockPos().relative(this.installedSide), this.hostTileEntity.getBlockState().getBlock(), this.hostTileEntity.getBlockPos());

            return true;
        }

        return false;
    }
}
