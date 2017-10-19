package simelectricity.essential.client.grid.accessory;

import rikka.librikka.math.Vec3f;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.Group;

public class AR10kVType1CableJoint implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVType1CableJoint();
	
	private AR10kVType1CableJoint() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper helper, PowerPoleRenderHelper neighbor) {
    	if (helper.connectionInfo.isEmpty()) {
    		if (neighbor != null){
    			Group target = neighbor.groups[0].closest(helper.groups[0], helper.groups[1]);
 				Vec3f from0 = target.insulators[0].realPos;
				Vec3f from1 = target.insulators[1].realPos;
				Vec3f from2 = target.insulators[2].realPos;
				
				Vec3f to0 = neighbor.groups[0].insulators[0].realPos;
				Vec3f to1 = neighbor.groups[0].insulators[1].realPos;
				Vec3f to2 = neighbor.groups[0].insulators[2].realPos;
				
				helper.addExtraWire(from1, to1, 0.25F);
				if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
					helper.addExtraWire(from0, to2, 0.1F);
					helper.addExtraWire(from2, to0, 0.1F);
				} else {
					helper.addExtraWire(from0, to0, 0.1F);
					helper.addExtraWire(from2, to2, 0.1F);
				}
    		}
    	} if (helper.connectionInfo.size() == 1) {
			if (neighbor != null) {
        		PowerPoleRenderHelper.ConnectionInfo[] connection1 = helper.connectionInfo.getFirst();
				Vec3f from0 = connection1[0].fixedFrom;
				Vec3f from1 = connection1[1].fixedFrom;
				Vec3f from2 = connection1[2].fixedFrom;
				
				Vec3f to0 = neighbor.groups[0].insulators[0].realPos;
				Vec3f to1 = neighbor.groups[0].insulators[1].realPos;
				Vec3f to2 = neighbor.groups[0].insulators[2].realPos;
				
				helper.addExtraWire(from1, to1, 0.25F);
				if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
					helper.addExtraWire(from0, to2, 0.1F);
					helper.addExtraWire(from2, to0, 0.1F);
				} else {
					helper.addExtraWire(from0, to0, 0.1F);
					helper.addExtraWire(from2, to2, 0.1F);
				}
			}
    	} else if (helper.connectionInfo.size() == 2) {
            PowerPoleRenderHelper.ConnectionInfo[] connection1 = helper.connectionInfo.getFirst();
            PowerPoleRenderHelper.ConnectionInfo[] connection2 = helper.connectionInfo.getLast();
            
            if (neighbor != null) {
				Vec3f to0 = neighbor.groups[0].insulators[0].realPos;
				Vec3f to1 = neighbor.groups[0].insulators[1].realPos;
				Vec3f to2 = neighbor.groups[0].insulators[2].realPos;

				ConnectionInfo[] target = neighbor.groups[0].closest(connection1, connection2);
				Vec3f from0 = target[0].fixedFrom;
				Vec3f from1 = target[1].fixedFrom;
				Vec3f from2 = target[2].fixedFrom;
				
				helper.addExtraWire(from1, to1, 0.25F);
				if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
					helper.addExtraWire(from0, to2, 0.1F);
					helper.addExtraWire(from2, to0, 0.1F);
				} else {
					helper.addExtraWire(from0, to0, 0.1F);
					helper.addExtraWire(from2, to2, 0.1F);
				}
            }
        }
	}

}
