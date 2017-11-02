package simelectricity.essential.client.grid.accessory;

import rikka.librikka.math.Vec3f;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.Group;

public class AR10kVType1CableJoint implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVType1CableJoint();
	private AR10kVType1CableJoint() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper pole, PowerPoleRenderHelper accessory) {
    	if (pole.connectionList.isEmpty()) {
    		Group target = accessory.groups[0].closest(pole.groups[0], pole.groups[1]);
			Vec3f from0 = target.insulators[0].realPos;
			Vec3f from1 = target.insulators[1].realPos;
			Vec3f from2 = target.insulators[2].realPos;
			
			Vec3f to0 = accessory.groups[0].insulators[0].realPos;
			Vec3f to1 = accessory.groups[0].insulators[1].realPos;
			Vec3f to2 = accessory.groups[0].insulators[2].realPos;
			
			pole.addExtraWire(from1, to1, 0.25F);
			if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
				pole.addExtraWire(from0, to2, 0.1F);
				pole.addExtraWire(from2, to0, 0.1F);
			} else {
				pole.addExtraWire(from0, to0, 0.1F);
				pole.addExtraWire(from2, to2, 0.1F);
			}
    	} if (pole.connectionList.size() == 1) {
    		PowerPoleRenderHelper.ConnectionInfo[] connection1 = pole.connectionList.getFirst();
			Vec3f from0 = connection1[0].fixedFrom;
			Vec3f from1 = connection1[1].fixedFrom;
			Vec3f from2 = connection1[2].fixedFrom;
			
			Vec3f to0 = accessory.groups[0].insulators[0].realPos;
			Vec3f to1 = accessory.groups[0].insulators[1].realPos;
			Vec3f to2 = accessory.groups[0].insulators[2].realPos;
			
			pole.addExtraWire(from1, to1, 0.25F);
			if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
				pole.addExtraWire(from0, to2, 0.1F);
				pole.addExtraWire(from2, to0, 0.1F);
			} else {
				pole.addExtraWire(from0, to0, 0.1F);
				pole.addExtraWire(from2, to2, 0.1F);
			}
    	} else if (pole.connectionList.size() == 2) {
            PowerPoleRenderHelper.ConnectionInfo[] connection1 = pole.connectionList.getFirst();
            PowerPoleRenderHelper.ConnectionInfo[] connection2 = pole.connectionList.getLast();
            
            Vec3f to0 = accessory.groups[0].insulators[0].realPos;
			Vec3f to1 = accessory.groups[0].insulators[1].realPos;
			Vec3f to2 = accessory.groups[0].insulators[2].realPos;

			ConnectionInfo[] target = accessory.groups[0].closest(connection1, connection2);
			Vec3f from0 = target[0].fixedFrom;
			Vec3f from1 = target[1].fixedFrom;
			Vec3f from2 = target[2].fixedFrom;
			
			pole.addExtraWire(from1, to1, 0.25F);
			if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
				pole.addExtraWire(from0, to2, 0.1F);
				pole.addExtraWire(from2, to0, 0.1F);
			} else {
				pole.addExtraWire(from0, to0, 0.1F);
				pole.addExtraWire(from2, to2, 0.1F);
			}
        }
	}

}
