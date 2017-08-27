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

public class VoltageSensorPanel implements ISEElectricalCoverPanel, ISERedstoneEmitterCoverPanel, ISEGuiCoverPanel {
    public boolean emitRedStoneSignal;
    public boolean inverted;
    public double thresholdVoltage = 100;

    private TileEntity hostTileEntity;
    private EnumFacing installedSide;
    private double voltage;

    public VoltageSensorPanel() {
    }

    public VoltageSensorPanel(NBTTagCompound nbt) {
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
    public void toNBT(NBTTagCompound nbt) {
        nbt.setString("coverPanelType", "VoltageSensorPanel");

        nbt.setBoolean("inverted", this.inverted);
        nbt.setDouble("thresholdVoltage", this.thresholdVoltage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISECoverPanelRender getCoverPanelRender() {
        return VoltageSensorRender.instance;
    }

    @Override
    public void setHost(TileEntity hostTileEntity, EnumFacing side) {
        this.hostTileEntity = hostTileEntity;
        installedSide = side;
    }

    @Override
    public ItemStack getDroppedItemStack() {
        return new ItemStack(ItemRegistry.itemMisc, 1, 1);
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
    public Container getServerContainer(TileEntity te) {
        return new ContainerVoltageSensor(this, te);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getClientGuiContainer(TileEntity te) {
        return new GuiVoltageSensor(this.getServerContainer(te));
    }

    /////////////////////////
    ///ISEElectricalCoverPanel
    /////////////////////////
    @Override
    public void onEnergyNetUpdate(double voltage) {
        this.voltage = voltage;

        WorldServer world = (WorldServer) this.hostTileEntity.getWorld();
        world.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                VoltageSensorPanel.this.checkRedStoneSignal();
            }
        });
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
            this.hostTileEntity.getWorld().neighborChanged(this.hostTileEntity.getPos().offset(this.installedSide), this.hostTileEntity.getBlockType(), this.hostTileEntity.getPos());

            return true;
        }

        return false;
    }
}
