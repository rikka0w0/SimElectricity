package SimElectricity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import SimElectricity.API.*;
import SimElectricity.Samples.TileEnergyBase;
import SimElectricity.sqlite.Db;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public final class EnergyNet {
//	private final Map<Integer, TileEntity> registeredTilesX = new HashMap();
//	private final Map<Integer, TileEntity> registeredTilesY = new HashMap();
//	private final Map<Integer, TileEntity> registeredTilesZ = new HashMap();
	
	
	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);
		
	}

	
	public EnergyNet() {
		System.out.println("EnergyNet create");
	}


	public void addTileEntity(TileEntity te){
		if (!(te instanceof IBaseComponent)) {
			System.out.println("Invalid tileentity "+te+" is trying to attach to energy network, aborting");
			return;
		}
		
		System.out.println("Tileentity "+te+" is attached to energy network!");
		
		ChunkCoordinates coords = new ChunkCoordinates(te.xCoord,te.yCoord,te.zCoord);
		
		Db.addNode(te.toString(), te.xCoord, te.yCoord, te.zCoord, 0, 0, 0);
		
		ResultSet rs = Db.getNeighboringNode(te.xCoord, te.yCoord, te.zCoord);
		try {
			while (rs.next()) {
				System.out.println(rs.getString("node"));			
			}
			System.out.println(rs.getRow());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (registeredTiles.containsKey(coords)) {
//			System.out.println("Tileentity " + te + " is already added, aborting");
//			return;
//		}
		
//		registeredTiles.put(coords, te);		
	}
	
	public void removeTileEntity(TileEntity te){
		
	}
	
	public static EnergyNet getForWorld(World world) {
		WorldData worldData = WorldData.get(world);
		return worldData.energyNet;
	}
	
	/**Initialize the energy network, basically register some forge events*/
	public static void initialize(){
		new EventHandler();
	}

	/**Responce to forge events*/
	public static class EventHandler{
		public EventHandler(){
			MinecraftForge.EVENT_BUS.register(this);
		}

		@ForgeSubscribe
		public void onAttachEvent(TileAttachEvent event) {
			EnergyNet.getForWorld(event.energyTile.worldObj).addTileEntity((TileEntity)event.energyTile);
		}
		
		@ForgeSubscribe
		public void onTileDetach(TileDetachEvent event) {
			EnergyNet.getForWorld(event.energyTile.worldObj).removeTileEntity((TileEntity)event.energyTile);
		}

		@ForgeSubscribe
		public void onTileChange(TileChangeEvent event) {
			//event.amount = EnergyNet.getForWorld(event.world).emitEnergyFrom((IEnergySource)event.energyTile, event.amount);
		}
	}
}
