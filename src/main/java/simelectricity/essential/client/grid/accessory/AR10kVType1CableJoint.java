package simelectricity.essential.client.grid.accessory;

import rikka.librikka.math.Vec3f;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.Group;

public class AR10kVType1CableJoint implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVType1CableJoint();
	private AR10kVType1CableJoint() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper current, PowerPoleRenderHelper neighbor) {
    	if (current.connectionInfo.isEmpty()) {
    		Group target = neighbor.groups[0].closest(current.groups[0], current.groups[1]);
			Vec3f from0 = target.insulators[0].realPos;
			Vec3f from1 = target.insulators[1].realPos;
			Vec3f from2 = target.insulators[2].realPos;
			
			Vec3f to0 = neighbor.groups[0].insulators[0].realPos;
			Vec3f to1 = neighbor.groups[0].insulators[1].realPos;
			Vec3f to2 = neighbor.groups[0].insulators[2].realPos;
			
			current.addExtraWire(from1, to1, 0.25F);
			if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
				current.addExtraWire(from0, to2, 0.1F);
				current.addExtraWire(from2, to0, 0.1F);
			} else {
				current.addExtraWire(from0, to0, 0.1F);
				current.addExtraWire(from2, to2, 0.1F);
			}
    	} if (current.connectionInfo.size() == 1) {
    		PowerPoleRenderHelper.ConnectionInfo[] connection1 = current.connectionInfo.getFirst();
			Vec3f from0 = connection1[0].fixedFrom;
			Vec3f from1 = connection1[1].fixedFrom;
			Vec3f from2 = connection1[2].fixedFrom;
			
			Vec3f to0 = neighbor.groups[0].insulators[0].realPos;
			Vec3f to1 = neighbor.groups[0].insulators[1].realPos;
			Vec3f to2 = neighbor.groups[0].insulators[2].realPos;
			
			current.addExtraWire(from1, to1, 0.25F);
			if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
				current.addExtraWire(from0, to2, 0.1F);
				current.addExtraWire(from2, to0, 0.1F);
			} else {
				current.addExtraWire(from0, to0, 0.1F);
				current.addExtraWire(from2, to2, 0.1F);
			}
    	} else if (current.connectionInfo.size() == 2) {
            PowerPoleRenderHelper.ConnectionInfo[] connection1 = current.connectionInfo.getFirst();
            PowerPoleRenderHelper.ConnectionInfo[] connection2 = current.connectionInfo.getLast();
            
            Vec3f to0 = neighbor.groups[0].insulators[0].realPos;
			Vec3f to1 = neighbor.groups[0].insulators[1].realPos;
			Vec3f to2 = neighbor.groups[0].insulators[2].realPos;

			ConnectionInfo[] target = neighbor.groups[0].closest(connection1, connection2);
			Vec3f from0 = target[0].fixedFrom;
			Vec3f from1 = target[1].fixedFrom;
			Vec3f from2 = target[2].fixedFrom;
			
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
