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
import simElectricity.simulator.Simulator;

public final class EnergyNet {
	WeightedMultigraph<Node, Resistor> graph = new WeightedMultigraph<Node, Resistor>(
			Resistor.class);
	// for source or sink
	Map<TileEntity, Node> toGirdNodes = new HashMap<TileEntity, Node>();
	Map<TileEntity, Node> definedVoltageNodes = new HashMap<TileEntity, Node>();
	// for conductor
	Map<TileEntity, Resistor> inResistor = new HashMap<TileEntity, Resistor>();
	Map<TileEntity, Node> realNodes = new HashMap<TileEntity, Node>();

	boolean needCalculate = false;
	boolean needOptimization = false;

	public static List<TileEntity> neighborListOf(TileEntity te) {
		List<TileEntity> result = new ArrayList<TileEntity>();
		TileEntity temp;

		temp = te.worldObj.getBlockTileEntity(te.xCoord + 1, te.yCoord,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord - 1, te.yCoord,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord + 1,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord - 1,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord,
				te.zCoord + 1);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord,
				te.zCoord - 1);
		if (temp instanceof IBaseComponent)
			result.add(temp);

		return result;
	}

	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);
		if (energyNet.needCalculate) {
			energyNet.needCalculate = false;
			List<Node> nodes = Simulator.runSimulator(energyNet.graph);
			for (Node node : nodes) {
				System.out.println(node.getVoltage());
			}
		}
	}

	public EnergyNet() {
		System.out.println("EnergyNet create");
	}

	public Node getNodeOf(TileEntity te) {
		Node result = null;
		if (te instanceof IConductor) {
			result = realNodes.get(te);
			if ((result == null) && (inResistor.containsKey(te) == false)) {
				result = new Node(graph);
				realNodes.put(te, result);
			}
		} else {
			result = toGirdNodes.get(te);
			if (result == null) {
				// create this node
				result = new Node(graph);
				Node definedVoltage = null;
				if (te instanceof IPowerSink) {
					definedVoltage = new Node(graph, 0);
				} else if (te instanceof IPowerSource) {
					definedVoltage = new Node(graph,
							((IPowerSource) te).getOutputVoltage());
				}
				result.connect(definedVoltage,
						((IBaseComponent) te).getResistance());
				toGirdNodes.put(te, result);
				definedVoltageNodes.put(te, definedVoltage);
			}
		}
		return result;
	}

	public void addTileEntity(TileEntity te) {
		if (!(te instanceof IBaseComponent)) {
			System.out.println("Invalid tileentity " + te
					+ " is trying to attach to energy network, aborting");
			return;
		}

		List<TileEntity> neighborList = neighborListOf(te);
		System.out.println("neighborList size: " + neighborList.size());
		// TileEntity is single, return
		if (neighborList.size() == 0)
			return;

		Node teNode = getNodeOf(te);
		// connect to neighbor nodes
		for (TileEntity neighborTe : neighborList) {
			Node neighborTeNode = getNodeOf(neighborTe);
			float resistance = te instanceof IConductor ? ((IBaseComponent) te).getResistance() : 0;
			resistance += neighborTe instanceof IConductor ? ((IBaseComponent) neighborTe).getResistance() : 0;
			teNode.connect(neighborTeNode, resistance);
		}
		
		needCalculate = true;

		System.out.println("Tileentity " + te
				+ " is attached to energy network!");
	}

	public void removeTileEntity(TileEntity te) {
		if (te instanceof IConductor) {
			// tileEntity is fake node
			if (inResistor.containsKey(te)) {
				inResistor.remove(te);
			} else {
				graph.removeVertex(realNodes.get(te));
				realNodes.remove(te);
			}
		} else {
			graph.removeVertex(toGirdNodes.get(te));
			toGirdNodes.remove(te);
			graph.removeVertex(definedVoltageNodes.get(te));
			definedVoltageNodes.remove(te);
		}
		
		needCalculate = true;

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
