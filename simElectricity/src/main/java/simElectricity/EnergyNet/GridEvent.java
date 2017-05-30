package simElectricity.EnergyNet;

import net.minecraft.world.World;

public abstract class GridEvent implements IEnergyNetEvent{
	protected World world;
	protected int x1, y1, z1;
	
	public GridEvent(World world, int x1, int y1, int z1){
		this.world = world;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}
	
	public static class AppendNode extends GridEvent{
		protected byte type;
		public AppendNode(World world, int x1, int y1, int z1, byte type) {
			super(world, x1, y1, z1);
			this.type = type;
		}
	}
	
	public static class RemoveNode extends GridEvent{
		public RemoveNode(World world, int x1, int y1, int z1) {
			super(world, x1, y1, z1);
		}
	}
	
	public static class Connect extends GridEvent{
		protected int x2, y2, z2;
		protected double resistance;
		public Connect(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance) {
			super(world, x1, y1, z1);
			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;
			this.resistance = resistance;
		}
	}
	
	/**
	 * Coord1 - primary, Coord2 - secondary
	 * ratio = Nsec/Npri, resistance refer to secondary side (Coord2)
	 */
	public static class MakeTransformer extends GridEvent{
		protected int x2, y2, z2;
		protected double resistance, ratio;
		public MakeTransformer(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance, double ratio) {
			super(world, x1, y1, z1);
			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;
			this.resistance = resistance;
			this.ratio = ratio;
		}
	}
	
	public static class BreakConnection extends GridEvent{
		protected int x2, y2, z2;
		public BreakConnection(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
			super(world, x1, y1, z1);
			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;
		}
	}
}
