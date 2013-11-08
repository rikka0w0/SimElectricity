package SimElectricity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	// private final Map<Integer, TileEntity> registeredTilesX = new HashMap();
	// private final Map<Integer, TileEntity> registeredTilesY = new HashMap();
	// private final Map<Integer, TileEntity> registeredTilesZ = new HashMap();
	public static int powerSource = 1;
	public static int powerSink = 2;
	public static int conductor = 3;

	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);

	}

	public EnergyNet() {
		System.out.println("EnergyNet create");
	}

	private int addTileToDb(TileEntity te) {
		int nodeType = 0;
		if (!(te instanceof IPowerSource)) {
			nodeType = powerSource;
		} else if (!(te instanceof IPowerSink)) {
			nodeType = powerSink;
		} else if (!(te instanceof IConductor)) {
			nodeType = conductor;
		}

		return Db.addNode(te.hashCode(), te.xCoord, te.yCoord, te.zCoord,
				((IBaseComponent) te).voltage,
				((IBaseComponent) te).getResistance(), nodeType);
	}
	/*
	private Map<Integer, Integer> getNeighboringNodesFromDb(int x, int y, int z){
		Map<Integer, Integer> result = new HashMap();
		ResultSet rs = Db.getNeighboringNodes(x, y ,z);
		try {
			while (rs.next()) {
				result.put(rs.getInt("node"), rs.getInt("resistance"));
				System.out.println(rs.getString("node"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	*/
	public void addTileEntity(TileEntity te) {
		if (!(te instanceof IBaseComponent)) {
			System.out.println("Invalid tileentity " + te
					+ " is trying to attach to energy network, aborting");
			return;
		}

		System.out.println("Tileentity " + te
				+ " is attached to energy network!");
		addTileToDb(te);

		// if (registeredTiles.containsKey(coords)) {
		// System.out.println("Tileentity " + te +
		// " is already added, aborting");
		// return;
		// }

		// registeredTiles.put(coords, te);
	}

	public void removeTileEntity(TileEntity te) {
		int count = Db.delNode(te.hashCode());
		if (count > 0)
			System.out.println("Tileentity " + te
					+ " is detach to energy network!");
	}

	public static EnergyNet getForWorld(World world) {
		WorldData worldData = WorldData.get(world);
		return worldData.energyNet;
	}

	/** Initialize the energy network, basically register some forge events */
	public static void initialize() {
		new EventHandler();
	}

	/** Responce to forge events */
	public static class EventHandler {
		public EventHandler() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		@ForgeSubscribe
		public void onAttachEvent(TileAttachEvent event) {
			EnergyNet.getForWorld(event.energyTile.worldObj).addTileEntity(
					(TileEntity) event.energyTile);
		}

		@ForgeSubscribe
		public void onTileDetach(TileDetachEvent event) {
			EnergyNet.getForWorld(event.energyTile.worldObj).removeTileEntity(
					(TileEntity) event.energyTile);
		}

		@ForgeSubscribe
		public void onTileChange(TileChangeEvent event) {
			// event.amount =
			// EnergyNet.getForWorld(event.world).emitEnergyFrom((IEnergySource)event.energyTile,
			// event.amount);
		}
	}
}
