package simElectricity.Common.EnergyNet.Grid;

import simElectricity.API.Events.GridConnectionEvent;
import simElectricity.API.Events.GridDisconnectionEvent;
import simElectricity.API.Events.GridObjectAttachEvent;
import simElectricity.API.Events.GridObjectDetachEvent;
import simElectricity.API.Events.GridTilePresentEvent;
import simElectricity.API.Events.GridTileInvalidateEvent;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GridEventHandler {
    public GridEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onGridObjectAttach(GridObjectAttachEvent event) {    
    	GridDataProvider grid = GridDataProvider.get(event.world);
    	GridObject obj = grid.addGridObject(event.x, event.y, event.z, event.type);
    	
    	if (!ConfigManager.showEnergyNetInfo)
    		return;
    	
    	if (obj == null){
    		SEUtils.logInfo("Fail to attach gridObject at " +String.valueOf(event.x)+","+String.valueOf(event.y)+","+String.valueOf(event.z));
    	}else{
    		SEUtils.logInfo("GridObject attached at " +obj.getIDString()+", type " + obj.type);
    	}
    }
    
    
    @SubscribeEvent
    public void onGridObjectDetach(GridObjectDetachEvent event) {    
    	GridDataProvider grid = GridDataProvider.get(event.world);
    	GridObject obj = grid.getGridObjectAtCoord(event.x, event.y, event.z);
    	
    	if (obj == null){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to detach gridObject at " +String.valueOf(event.x)+","+String.valueOf(event.y)+","+String.valueOf(event.z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject detached at " +obj.getIDString()+", type " + obj.type);
    		grid.removeGridObject(obj);
    	}
    	
    	
    }
    
    @SubscribeEvent
    public void onGridConnection(GridConnectionEvent event) {    
    	GridDataProvider grid = GridDataProvider.get(event.world);
    	GridObject obj1 = grid.getGridObjectAtCoord(event.x1, event.y1, event.z1);
    	GridObject obj2 = grid.getGridObjectAtCoord(event.x2, event.y2, event.z2);
    	

    	
    	if (ConfigManager.showEnergyNetInfo){
        	if (obj1 == null || obj2 == null){
        		SEUtils.logInfo("Fail to build grid connection between " +String.valueOf(event.x1)+","+String.valueOf(event.y1)+","+String.valueOf(event.z1)+" and "
						+String.valueOf(event.x2)+","+String.valueOf(event.y2)+","+String.valueOf(event.z2));
        	}else{
        		SEUtils.logInfo("Grid connection built between " +String.valueOf(event.x1)+","+String.valueOf(event.y1)+","+String.valueOf(event.z1)+" and "
						+String.valueOf(event.x2)+","+String.valueOf(event.y2)+","+String.valueOf(event.z2));
        	}
    	}
    	
    	if (obj1 != null && obj2 != null)
    		grid.addConnection(obj1, obj2, event.resistance);
    }
    
    @SubscribeEvent
    public void onGridDisconnectionEvent(GridDisconnectionEvent event) {    
    	GridDataProvider grid = GridDataProvider.get(event.world);
    	GridObject obj1 = grid.getGridObjectAtCoord(event.x1, event.y1, event.z1);
    	GridObject obj2 = grid.getGridObjectAtCoord(event.x2, event.y2, event.z2);
    	

    	
    	if (ConfigManager.showEnergyNetInfo){
        	if (obj1 == null || obj2 == null){
        		SEUtils.logInfo("Fail to remove grid connection between " +String.valueOf(event.x1)+","+String.valueOf(event.y1)+","+String.valueOf(event.z1)+" and "
						+String.valueOf(event.x2)+","+String.valueOf(event.y2)+","+String.valueOf(event.z2));
        	}else{
        		SEUtils.logInfo("Grid connection removed between " +String.valueOf(event.x1)+","+String.valueOf(event.y1)+","+String.valueOf(event.z1)+" and "
						+String.valueOf(event.x2)+","+String.valueOf(event.y2)+","+String.valueOf(event.z2));
        	}
    	}
    	
    	if (obj1 != null && obj2 != null)
    		grid.removeConnection(obj1, obj2);
    }   
    
    @SubscribeEvent
    public void onGridTilePresentEvent(GridTilePresentEvent event) {   
    	World world = event.te.getWorldObj();
    	GridDataProvider grid = GridDataProvider.get(world);
    	grid.onGridTilePresent(event.te);
    	
    	if (ConfigManager.showEnergyNetInfo)
    		SEUtils.logInfo("GridTile assosiated with GridObject at "+String.valueOf(event.te.xCoord)+","+String.valueOf(event.te.yCoord)+","+String.valueOf(event.te.zCoord));
    }
    
    @SubscribeEvent
    public void onGridTileInvalidate(GridTileInvalidateEvent event) {   
    	World world = event.te.getWorldObj();
    	GridDataProvider grid = GridDataProvider.get(world);
    	grid.onGridTileInvalidate(event.te);
    	
    	if (ConfigManager.showEnergyNetInfo)
    		SEUtils.logInfo("GridTile destroyed at"+String.valueOf(event.te.xCoord)+","+String.valueOf(event.te.yCoord)+","+String.valueOf(event.te.zCoord));
    }
}
