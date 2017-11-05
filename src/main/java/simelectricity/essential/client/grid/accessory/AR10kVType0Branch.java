package simelectricity.essential.client.grid.accessory;

import net.minecraft.util.math.MathHelper;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.loader.EasyTextureLoader;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.client.grid.Sorter;
import simelectricity.essential.client.grid.pole.Models;

public class AR10kVType0Branch implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR10kVType0Branch();
	private AR10kVType0Branch() {}
	
	@Override
	public void renderConnection(PowerPoleRenderHelper pole, PowerPoleRenderHelper accessory) {
		if (pole.connectionList.isEmpty()) {
			if (accessory.connectionList.isEmpty())
				return;
			
			PowerPoleRenderHelper.ConnectionInfo[] accessoryConnection = accessory.connectionList.getFirst();
			
			pole.addExtraWire(accessoryConnection[0].fixedFrom, accessoryConnection[0].fixedFrom.add(0, 1, 0), 0);
			pole.addExtraWire(accessoryConnection[1].fixedFrom, accessoryConnection[1].fixedFrom.add(0, 1, 0), 0);
			pole.addExtraWire(accessoryConnection[2].fixedFrom, accessoryConnection[2].fixedFrom.add(0, 1, 0), 0);
		} else {
			PowerPoleRenderHelper.ConnectionInfo[] poleConnection = pole.connectionList.getFirst();
			
			if (accessory.connectionList.isEmpty()) {
				pole.addExtraWire(poleConnection[0].fixedFrom, poleConnection[0].fixedFrom.add(0, -1, 0), 0);
				pole.addExtraWire(poleConnection[1].fixedFrom, poleConnection[1].fixedFrom.add(0, -1.5F, 0), 0);
				pole.addExtraWire(poleConnection[2].fixedFrom, poleConnection[2].fixedFrom.add(0, -1, 0), 0);
				return;
			}
			
			PowerPoleRenderHelper.ConnectionInfo[] accessoryConnection = accessory.connectionList.getFirst();
			float angleTo = accessoryConnection[1].calcAngleFromXInDegree();
			
			boolean has2PoleCon = pole.connectionList.size() > 1;
			float angleFrom = poleConnection[1].calcAngleFromXInDegree();			
			float angleDiff = angleTo-angleFrom;
			
			if (has2PoleCon) {
				PowerPoleRenderHelper.ConnectionInfo[] secondPoleConnection = pole.connectionList.getLast();
				float angleFrom2 = secondPoleConnection[1].calcAngleFromXInDegree();		
				float angleDiff2 = angleTo-angleFrom2;
				
				float cute1 = MathHelper.abs(angleDiff);
				float cute2 = MathHelper.abs(angleDiff2);
				
				cute1 = cute1>180 ? 360-cute1 : cute1;
				cute2 = cute2>180? 360-cute2: cute2;
				
				if (cute1 > cute2) {
					poleConnection = secondPoleConnection;
					angleFrom = angleFrom2;
					angleDiff = angleDiff2;
				}
			}
			
			final float angle = angleDiff < 0 ? angleDiff + 360 : angleDiff;
			final float middle = angle<180 ? 180+(angleFrom+angleTo)/2 : (angleFrom+angleTo)/2;
			
			Sorter.minDist(poleConnection, accessoryConnection, (from, to) ->{
				if ((0<=angle && angle<=67.5F) || (292.5F<=angle && angle<=360F)) {		
					pole.addExtraWire(from[0].pointOnCable(0.5F), to[0].fixedFrom, 0.1F, true);
					pole.addExtraWire(from[1].pointOnCable(0.5F), to[1].fixedFrom, 0);
					pole.addExtraWire(from[2].pointOnCable(0.5F), to[2].fixedFrom, 0.6F, true);
				} else if (angle >= 112.5F && angle <= 247.5F) {
					pole.addExtraWire(from[0].pointOnCable(0.5F), to[0].fixedFrom, 0.5F, true);
					pole.addExtraWire(from[2].pointOnCable(0.5F), to[2].fixedFrom, 0.5F, true);
					
					Vec3f pt = (new Vec3f(0.5F,1.5F,0.5F)).add(pole.pos);
					
					if (!has2PoleCon) {
						pole.addExtraWire(from[1].fixedFrom, pt, -0.3F);
					}
					
					pole.addExtraWire(pt, to[1].pointOnCable(0.3F), -0.5F);
				} else {					
					Vec3f pt = new Vec3f(0.65F, -1.2F, 0).rotateAroundY(middle).add(0.5F, 0, 0.5F);
					
					Models.render10kVInsulator(EasyTextureLoader.getTexture(ResourcePaths.metal), EasyTextureLoader.getTexture(ResourcePaths.glass_insulator))
					.translateCoord(0, 0.125F, 0).rotateToVec(0.5F, 0, 0.5F, pt.x, 0, pt.z).translateCoord(0.5F, pt.y, 0.5F).bake(pole.quadBuffer);
					
					pt = pt.add(pole.pos);
					pole.addExtraWire(from[0].pointOnCable(0.5F), to[0].fixedFrom, 0.1F, true);
					pole.addExtraWire(from[2].pointOnCable(0.5F), pt, 0.4F, true);
					pole.addExtraWire(pt, to[2].fixedFrom, 0.4F, true);
					
					
					pt = (new Vec3f(0.5F,1.5F,0.5F)).add(pole.pos);
					if (!has2PoleCon) {
						pole.addExtraWire(from[1].fixedFrom, pt, -0.3F);
					}
					
					pole.addExtraWire(pt, to[1].pointOnCable(0.8F), -0.5F);
				}
			});
		}
	}
}
