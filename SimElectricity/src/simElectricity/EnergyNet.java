package simElectricity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import org.jgrapht.graph.WeightedMultigraph;

import simElectricity.API.IBaseComponent;
import simElectricity.API.IConductor;
import simElectricity.API.IPowerSink;
import simElectricity.API.IPowerSource;
import simElectricity.API.TileAttachEvent;
import simElectricity.API.TileChangeEvent;
import simElectricity.API.TileDetachEvent;
import simElectricity.simulator.Node;
import simElectricity.simulator.Resistor;

public final class EnergyNet {
	WeightedMultigraph<Node, Resistor> graph = new WeightedMultigraph<Node, Resistor>(
			Resistor.class);
	// for source or sink
	Map<TileEntity, Node> toGirdNodes = new HashMap<TileEntity, Node>();
	Map<TileEntity, Node> definedVoltageNodes = new HashMap<TileEntity, Node>();
	// for conductor
	Map<TileEntity, Resistor> inResistor = new HashMap<TileEntity, Resistor>();

	public static List<TileEntity> neighborListOf(TileEntity te) {
		List<TileEntity> result = new ArrayList<TileEntity>();
		TileEntity temp;

		temp = te.worldObj.getBlockTileEntity(te.xCoord + 1, te.yCoord,
				te.zCoord);
		if (!(temp instanceof IBaseComponent))
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord - 1, te.yCoord,
				te.zCoord);
		if (!(temp instanceof IBaseComponent))
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord + 1,
				te.zCoord);
		if (!(temp instanceof IBaseComponent))
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord - 1,
				te.zCoord);
		if (!(temp instanceof IBaseComponent))
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord,
				te.zCoord + 1);
		if (!(temp instanceof IBaseComponent))
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord,
				te.zCoord - 1);
		if (!(temp instanceof IBaseComponent))
			result.add(temp);

		return result;
	}

	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);
	}

	public EnergyNet() {
		System.out.println("EnergyNet create");
	}

	public void addTileEntity(TileEntity te) {
		if (!(te instanceof IBaseComponent)) {
			System.out.println("Invalid tileentity " + te
					+ " is trying to attach to energy network, aborting");
			return;
		}

		List<TileEntity> neighborList = neighborListOf(te);
		if (neighborList.size() == 0)
			return;

		if (te instanceof IConductor) {

		} else {
			Node toGird = new Node(graph);
			Node definedVoltage = null;
			if (te instanceof IPowerSink) {
				definedVoltage = new Node(graph, 0);
			} else if (te instanceof IPowerSource) {
				definedVoltage = new Node(graph,
						((IPowerSource) te).getOutputVoltage());
			}
			toGird.connect(definedVoltage,
					((IBaseComponent) te).getResistance());
			toGirdNodes.put(te, toGird);
			definedVoltageNodes.put(te, definedVoltage);
		}

		System.out.println("Tileentity " + te
				+ " is attached to energy network!");
	}

	public void removeTileEntity(TileEntity te) {
		System.out
				.println("Tileentity " + te + " is detach to energy network!");
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
