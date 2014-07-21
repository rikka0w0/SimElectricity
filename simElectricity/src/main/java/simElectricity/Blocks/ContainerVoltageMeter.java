package simElectricity.Blocks;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import simElectricity.API.Common.ContainerBase;
import simElectricity.API.Energy;
import simElectricity.API.Util;

public class ContainerVoltageMeter extends ContainerBase {
    public ContainerVoltageMeter(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        ((TileVoltageMeter) tileEntity).voltage = Energy.getVoltage(((TileVoltageMeter) tileEntity));
        Util.updateTileEntityField(tileEntity, "voltage");
    }

    @Override
    public int getPlayerInventoryStartIndex() {
        return 27;
    }

    @Override
    public int getPlayerInventoryEndIndex() {
        return 36;
    }

    @Override
    public int getTileInventoryStartIndex() {
        return 0;
    }

    @Override
    public int getTileInventoryEndIndex() {
        return 27;
    }
}
