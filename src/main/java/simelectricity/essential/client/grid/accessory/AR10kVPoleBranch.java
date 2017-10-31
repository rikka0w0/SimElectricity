package simelectricity.essential.client.grid.accessory;

import net.minecraft.util.math.MathHelper;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public class AR10kVPoleBranch implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVPoleBranch();
	private AR10kVPoleBranch() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper pole, PowerPoleRenderHelper accessory) {
		if (pole.connectionInfo.size() == 1) {
    		PowerPoleRenderHelper.ConnectionInfo[] connection = pole.connectionInfo.getFirst();
			Vec3f from0 = connection[0].fixedFrom;
			Vec3f from1 = connection[1].fixedFrom;
			Vec3f from2 = connection[2].fixedFrom;
			
			if (accessory.connectionInfo.isEmpty())
				return;
			
			float a = calcAngleFromXInDegree(connection[0].fixedFrom,connection[0].fixedTo);
			
			connection = accessory.connectionInfo.getFirst();
			Vec3f to0 = connection[0].fixedFrom;
			Vec3f to1 = connection[1].fixedFrom;
			Vec3f to2 = connection[2].fixedFrom;
			
			
			float b = calcAngleFromXInDegree(connection[0].fixedFrom,connection[0].fixedTo);
			float c = b-a;
			pole.addExtraWire(from1, to1, 0.1F);
			boolean flag1 = PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2);
			boolean flag2 = PowerPoleRenderHelper.hasIntersection(from0, to2, from2, to0);
			
			if (flag1 == flag2) {
				float f0t0 = dist(from0, to0);
				float f2t2 = dist(from2, to2);
				float f0t2 = dist(from0, to2);
				float f2t0 = dist(from2, to0);
				
				float tension1 = 0.6F;
				float tension2 = 0.1F;
				
				if (f0t0+f2t2 > f0t2+f2t0) {
					boolean flg = f0t2>f2t0;
					pole.addExtraWire(from0, to2, flg?tension1:tension2, true);
					pole.addExtraWire(from2, to0, flg?tension2:tension1, true);
				} else {
					boolean flg = f0t0>f2t2;
					pole.addExtraWire(from0, to0, flg?tension1:tension2, true);
					pole.addExtraWire(from2, to2, flg?tension2:tension1, true);
				}
			} else {
				if (flag1) {
					pole.addExtraWire(from0, to2, 0.5F);
					pole.addExtraWire(from2, to0, 0.5F);
				} else {
					pole.addExtraWire(from0, to0, 0.5F);
					pole.addExtraWire(from2, to2, 0.5F);
				}
			}
			
			
    	}
	}
	
	public static float dist(Vec3f from, Vec3f to) {
		return (from.x-to.x)*(from.x-to.x) + (from.z-to.z)*(from.z-to.z);
	}
	
	public static float calcAngleFromXInDegree (Vec3f from, Vec3f to) {
		float x = to.x - from.x;
		float z = to.z - from.z;
		
		float l = MathHelper.sqrt(x*x + z*z);
		float theta = (float) Math.acos(x/l) * 180F / MathAssitant.PI;
		return z>0 ? -theta : theta;
	}

}
