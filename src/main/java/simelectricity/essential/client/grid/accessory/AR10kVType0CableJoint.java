package simelectricity.essential.client.grid.accessory;

import rikka.librikka.math.Vec3f;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public class AR10kVType0CableJoint implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVType0CableJoint();
	private AR10kVType0CableJoint() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper pole, PowerPoleRenderHelper accessory) {
		Vec3f to0 = accessory.groups[0].insulators[0].realPos;
		Vec3f to1 = accessory.groups[0].insulators[1].realPos;
		Vec3f to2 = accessory.groups[0].insulators[2].realPos;
		
		Vec3f from0 = pole.groups[0].insulators[0].realPos;
		Vec3f from1 = pole.groups[0].insulators[1].realPos;
		Vec3f from2 = pole.groups[0].insulators[2].realPos;
		
		float tension = -0.2F;
		pole.addExtraWire(from1, to1, tension);
		if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
			pole.addExtraWire(from0, to2, tension);
			pole.addExtraWire(from2, to0, tension);
		} else {
			pole.addExtraWire(from0, to0, tension);
			pole.addExtraWire(from2, to2, tension);
		}
	}
}
