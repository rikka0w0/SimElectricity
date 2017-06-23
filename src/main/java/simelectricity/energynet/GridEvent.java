package simelectricity.energynet;

import simelectricity.api.node.ISEGridNode;
import net.minecraft.world.World;

public abstract class GridEvent implements IEnergyNetEvent{
	protected World world;

	
	public GridEvent(World world){
		this.world = world;
	}
	
	public static class AppendNode extends GridEvent{
		protected ISEGridNode node;
		public AppendNode(World world, ISEGridNode node) {
			super(world);
			this.node = node;
		}
	}
	
	public static class RemoveNode extends GridEvent{
		protected ISEGridNode node;
		public RemoveNode(World world, ISEGridNode node) {
			super(world);
			this.node = node;
		}
	}
	
	public static class Connect extends GridEvent{
		protected ISEGridNode node1, node2;
		protected double resistance;
		public Connect(World world, ISEGridNode node1, ISEGridNode node2, double resistance) {
			super(world);
			this.node1 = node1;
			this.node2 = node2;
			this.resistance = resistance;
		}
	}
	
	/**
	 * Coord1 - primary, Coord2 - secondary
	 * ratio = Nsec/Npri, resistance refer to secondary side (Coord2)
	 */
	public static class MakeTransformer extends GridEvent{
		protected ISEGridNode pri, sec;
		protected double resistance, ratio;
		public MakeTransformer(World world, ISEGridNode pri, ISEGridNode sec, double resistance, double ratio) {
			super(world);
			this.resistance = resistance;
			this.ratio = ratio;
		}
	}
	
	public static class BreakConnection extends GridEvent{
		protected ISEGridNode node1, node2;
		public BreakConnection(World world, ISEGridNode node1, ISEGridNode node2) {
			super(world);
		}
	}
}
