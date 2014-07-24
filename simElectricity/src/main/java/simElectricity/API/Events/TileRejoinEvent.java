package simElectricity.API.Events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

/**
 * Should be posted when a tileEntity is rejoined to the energy net,
 * use {@link simElectricity.API.Energy#postTileRejoinEvent(net.minecraft.tileentity.TileEntity) Energy.postTileRejoinEvent()}
 */
public class TileRejoinEvent extends Event {
    public TileEntity energyTile;

    public TileRejoinEvent(TileEntity energyTile) {
        this.energyTile = energyTile;
    }
}