package simelectricity.essential.coverpanel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.ISEIuminousCoverPanelHost;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISEElectricalLoadCoverPanel;
import simelectricity.essential.api.coverpanel.ISEIuminousCoverPanel;
import simelectricity.essential.client.coverpanel.LedPanelRender;

public class LedPanel implements ISEElectricalLoadCoverPanel, ISEIuminousCoverPanel {
    private volatile byte lightLevel;
    private volatile TileEntity hostTileEntity;

    @Override
    public boolean isHollow() {
        return false;
    }

    @Override
    public void toNBT(NBTTagCompound nbt) {
        nbt.setString("coverPanelType", "LedPanel");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISECoverPanelRender getCoverPanelRender() {
        return LedPanelRender.instance;
    }

    @Override
    public void setHost(TileEntity hostTileEntity, EnumFacing side) {
        this.hostTileEntity = hostTileEntity;
    }

    @Override
    public ItemStack getDroppedItemStack() {
        return new ItemStack(ItemRegistry.itemMisc, 1, 0);
    }

    @Override
    public void onPlaced(double voltage) {
    }

    @Override
    public double getResistance() {
        return 9900;
    }

    @Override
    public void onEnergyNetUpdate(double voltage) {
        double power = voltage * voltage / this.getResistance() / 0.3;
        if (power > 15)
            power = 15;
        byte lightLevel = (byte) power;

        if (this.lightLevel != lightLevel) {
            //If light value changes, send a sync. packet to client
            this.lightLevel = lightLevel;

            if (this.hostTileEntity instanceof ISEIuminousCoverPanelHost) {
                WorldServer world = (WorldServer) this.hostTileEntity.getWorld();
                world.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        ((ISEIuminousCoverPanelHost) LedPanel.this.hostTileEntity).onLightValueUpdated();
                    }
                });
            }

        }
    }

    /////////////////////////
    ///ISEIuminousCoverPanel
    /////////////////////////
    @Override
    public byte getLightValue() {
        return lightLevel;
    }
}
