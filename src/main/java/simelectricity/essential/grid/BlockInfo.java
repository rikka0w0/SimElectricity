package simelectricity.essential.grid;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockInfo {
	public final int part;
	public final Vec3i offset;
	public BlockInfo(Vec3i offset, int part) {
		this.offset = offset;
		this.part = part;
	}
	
	public BlockPos getRealPos(BlockPos origin) {
		return origin.add(offset);
	}
}
