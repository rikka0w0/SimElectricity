package simElectricity.API.Internal;

import simElectricity.API.INetworkEventHandler;
import simElectricity.API.ISEWrenchable;
import simElectricity.API.ISidedFacing;
import net.minecraft.tileentity.TileEntity;

public interface INetworkManager {
    /**
     * Update/Synchronize fields of a client tileEntity from the server
     * <p/>
     * Data is sending from the server to client
     * 
     * @param tileEntity The tileEntity on server side, implementation of {@link simElectricity.API.ISEWrenchable} is not compulsory 
     * @param fields field(s) to be Synchronized
     */
	void updateTileEntityFields(TileEntity tileEntity, String ... fields);
	
    /**
     * Update/Synchronize fields of a server tileEntity from a client
     * <p/>
     * Data is sending from the client to server
     * 
     * @param tileEntity The tileEntity on client side, implementation of {@link simElectricity.API.ISEWrenchable} is not compulsory 
     * @param fields field(s) to be Synchronized
     */
	void updateTileEntityFieldsToServer(TileEntity tileEntity, String ... fields);
	
    /**
     * Update/Synchronize the functional side of a client tileEntity from the server
     * <p/>
     * Data is sending from the server to client
	 *
     * @param tileEntity The tileEntity on server side, it MUST implement {@link simElectricity.API.ISEWrenchable}
     */
	void updateFunctionalSide(TileEntity tileEntity);
	
    /**
     * Update/Synchronize the facing of a client tileEntity from the server
     * <p/>
     * Data is sending from the server to client
	 *
     * @param tileEntity The tileEntity on server side, it MUST implement {@link simElectricity.API.ISidedFacing}
     */
	void updateFacing(TileEntity tileEntity);
	
    /**
     * Update/Synchronize fields of a client tileEntity from the server
     * <p/>
     * Data is sending from the server to client
	 *
     * @param tileEntity The tileEntity on server side, it MUST implement {@link simElectricity.API.INetworkEventHandler}
     */
	void updateNetworkFields(TileEntity tileEntity);
	
    /**
     * Update/Synchronize the NBT of a client tileEntity from the server
     * <p/>
     * Data is sending from the server to client
	 *
     * @param tileEntity The tileEntity on server side
     */
	void updateTileEntityNBT(TileEntity tileEntity);
}
