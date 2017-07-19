package simelectricity.essential.utils;

public class BlockPos implements Comparable<BlockPos> {
	public int x;
	public int y;
	public int z;
	
	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//////////////////////
	///Comparable
	//////////////////////
	@Override
	public int compareTo(BlockPos o) {
		if (o.x < x) {
			return 1;
		} else if (o.x > x) {
			return -1;
		} else if (o.z < z) {
			return 1;
		} else if (o.z > z) {
			return -1;
		} else if (o.y < y) {
			return 1;
		} else if (o.y > y) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return "{" + x + ", " + y + ", " + z + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockPos) {
			BlockPos b = (BlockPos) obj;

			return b.x == x && b.y == y && b.z == z;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return (x * 37 + y) * 37 + z;
	}
}
