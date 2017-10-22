package simelectricity.essential.client.grid.accessory;

import rikka.librikka.math.Vec3f;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public class AR10kVPoleBranch implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVPoleBranch();
	private AR10kVPoleBranch() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper current, PowerPoleRenderHelper neighbor) {
		if (current.connectionInfo.size() == 1) {
    		PowerPoleRenderHelper.ConnectionInfo[] connection = current.connectionInfo.getFirst();
			Vec3f from0 = connection[0].fixedFrom;
			Vec3f from1 = connection[1].fixedFrom;
			Vec3f from2 = connection[2].fixedFrom;
			
			if (neighbor.connectionInfo.isEmpty())
				return;
			
			connection = neighbor.connectionInfo.getFirst();
			Vec3f to0 = connection[0].fixedFrom;
			Vec3f to1 = connection[1].fixedFrom;
			Vec3f to2 = connection[2].fixedFrom;
			
			current.addExtraWire(from1, to1, 0.25F);
			if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
				current.addExtraWire(from0, to2, 0.1F);
				current.addExtraWire(from2, to0, 0.1F);
			} else {
				current.addExtraWire(from0, to0, 0.1F);
				current.addExtraWire(from2, to2, 0.1F);
			}
    	}
	}

}
