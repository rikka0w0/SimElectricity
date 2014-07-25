package simElectricity.API.Events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

/**
 * Should be posted when a tileEntity is joining the energy net,
 * use {@link simElectricity.API.Energy#postTileAttachEvent(net.minecraft.tileentity.TileEntity) Energy.postTileAttachEvent()}
 */
public class TileAttachEvent extends Event {
    public TileEntity energyTile;

    public TileAttachEvent(TileEntity energyTile) {
        this.energyTile = energyTile;
    }
}
