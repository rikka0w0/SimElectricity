package simelectricity.essential.grid;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockInfo {
	public final int part;
	public final Vec3i offset;
	
	public BlockInfo (int x, int y, int z, int part) {
		this(new Vec3i(x, y, z), part);
	}
	
	public BlockInfo(Vec3i offset, int part) {
		this.offset = offset;
		this.part = part;
	}
	
	public BlockPos getRealPos(BlockPos origin) {
		return origin.add(offset);
	}
}
