package simElectricity.Common.Blocks.Container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import simElectricity.API.Common.ContainerBase;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileSwitch;

public class ContainerSwitch extends ContainerBase {
    public ContainerSwitch(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
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

    @Override
    public void init() {
        if (!tileEntity.getWorldObj().isRemote) {
            Util.updateTileEntityField(tileEntity, "resistance");
            Util.updateTileEntityField(tileEntity, "maxCurrent");
            Util.updateTileEntityField(tileEntity, "isOn");
            Util.updateTileEntityField(tileEntity, "inputSide");
            Util.updateTileEntityField(tileEntity, "outputSide");
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        Util.updateTileEntityField(tileEntity, "current");
    }
}