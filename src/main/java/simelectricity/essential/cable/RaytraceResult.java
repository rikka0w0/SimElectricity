package simelectricity.essential.cable;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

public class RaytraceResult {
	public final boolean hitCenter;
	public final MovingObjectPosition movingObjectPosition;
	public final AxisAlignedBB boundingBox;
	public final ForgeDirection sideHit;

	public RaytraceResult(boolean hitCenter, MovingObjectPosition movingObjectPosition, AxisAlignedBB boundingBox, ForgeDirection side) {
		this.hitCenter = hitCenter;
		this.movingObjectPosition = movingObjectPosition;
		this.boundingBox = boundingBox;
		this.sideHit = side;
	}

	@Override
	public String toString() {
		return String.format("RayTraceResult: %s, %s", hitCenter ? "center" : "side", boundingBox == null ? "null" : boundingBox.toString());
	}
}
