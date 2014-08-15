package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import simElectricity.Common.Network.NetworkManager;

public class Network {
    /**
     * Update a client tileEntity field from the server
     */
    public static void updateTileEntityFields(TileEntity tileEntity, String ... field) {
    	NetworkManager.updateTileEntityFields(tileEntity, field);
    }

    /**
     * Update a server tileEntity field from a client
     */
    public static void updateTileEntityFieldsToServer(TileEntity tileEntity, String ... field) {
        NetworkManager.updateTileEntityFieldsToServer(tileEntity, field);
    }

    /**
     * Update a tileEntity's functional side
     */
    public static void updateFunctionalSide(TileEntity tileEntity) {
        NetworkManager.updateFunctionalSide(tileEntity);
    }

    /**
     * Update a tileEntity's facing
     */
    public static void updateFacing(TileEntity tileEntity) {
        NetworkManager.updateFacing(tileEntity);
    }

    /**
     * Attempt to update a tileEntity's network fields
     */
    public static void updateNetworkFields(TileEntity tileEntity) {
        NetworkManager.updateNetworkFields(tileEntity);
    }

    /**
     * Send the NBT of a tileEntity from the server to the client
     */
    public static void updateTileEntityNBT(TileEntity tileEntity) {
        NetworkManager.updateTileEntityNBT(tileEntity);
    }
}
