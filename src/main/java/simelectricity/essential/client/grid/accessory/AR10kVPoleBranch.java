package simelectricity.essential.client.grid.accessory;

import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.client.grid.pole.Models;

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
			
			float angleFrom = connection[0].fixedFrom.calcAngleFromXInDegree(connection[0].fixedTo);
			
			connection = accessory.connectionInfo.getFirst();
			Vec3f to0 = connection[0].fixedFrom;
			Vec3f to1 = connection[1].fixedFrom;
			Vec3f to2 = connection[2].fixedFrom;
			
			
			float angleTo = connection[0].fixedFrom.calcAngleFromXInDegree(connection[0].fixedTo);
			float angle = angleTo>angleFrom ? angleTo-angleFrom : angleTo-angleFrom + 360;
		
			this.sort(pole.connectionInfo.getFirst(), accessory.connectionInfo.getFirst(), (from, to) ->{
				if ((0<=angle && angle<=67.5F) || (292.5F<=angle && angle<=360F)) {		
					pole.addExtraWire(from[0].fixedFrom, to[0].fixedFrom, 0.1F, true);
					pole.addExtraWire(from[2].fixedFrom, to[2].fixedFrom, 0.6F, true);
				} else if (angle >= 112.5F && angle <= 247.5F) {
					pole.addExtraWire(from[0].pointOnCable(0.4F), to[0].fixedFrom, 0.5F, true);
					pole.addExtraWire(from[2].fixedFrom, to[2].fixedFrom, 0.5F, true);
					
					RawQuadGroup insulator = Models.render10kVInsulator(EasyTextureLoader.getTexture(ResourcePaths.metal), EasyTextureLoader.getTexture(ResourcePaths.glass_insulator));
					insulator.translateCoord(0.5F, 1, 0.5F).bake(pole.quadBuffer);
					Vec3f pt = (new Vec3f(0.5F,1.5F,0.5F)).add(pole.pos);
					pole.addExtraWire(from1, pt, -0.3F);
					pole.addExtraWire(pt, to1, -0.4F);
				} else {
					float gg = (angleFrom+angleTo)/2;
					if (angle<180)
						gg += 180;
					
					RawQuadGroup insulator = Models.render10kVInsulator(EasyTextureLoader.getTexture(ResourcePaths.metal), EasyTextureLoader.getTexture(ResourcePaths.glass_insulator));
					insulator.rotateAroundX(90).translateCoord(0, -1.2F, 0.125F).rotateAroundY(90+gg).translateCoord(0.5F, 0, 0.5F).bake(pole.quadBuffer);
					
					Vec3f pt = new Vec3f(0.65F, -1.2F, 0).rotateAroundY(gg).add(0.5F, 0, 0.5F).add(pole.pos);
/*					Vec3f pt = new Vec3f(	0.65F *MathAssitant.cosAngle(gg) + 0.5F + pole.pos.getX()
											, -1.2F + pole.pos.getY(), 
											-0.65F *MathAssitant.sinAngle(gg) + 0.5F + pole.pos.getZ());*/
					
					pole.addExtraWire(from[0].fixedFrom, to[0].fixedFrom, 0.1F, true);
					pole.addExtraWire(from[2].fixedFrom, pt, 0.4F, true);
					pole.addExtraWire(pt, to[2].fixedFrom, 0.4F, true);
				}
			});
			
			
/*			
			if ((0<=angle && angle<=67.5F) || (292.5F<=angle && angle<=360F)) {			
				this.sort(pole.connectionInfo.getFirst(), accessory.connectionInfo.getFirst(), (from, to) ->{
					pole.addExtraWire(from[0].fixedFrom, to[0].fixedFrom, 0.1F, true);
					pole.addExtraWire(from[2].fixedFrom, to[2].fixedFrom, 0.6F, true);
				});
				
				float tension1 = 0.6F;
				float tension2 = 0.1F;
				
				if (f0t0 < f0t2) {
					boolean flg = f0t0 < f2t2;
					pole.addExtraWire(from0, to0, flg?tension2:tension1, true);
					pole.addExtraWire(from2, to2, flg?tension1:tension2, true);
				} else {
					boolean flg = f0t2 < f2t0;
					pole.addExtraWire(from0, to2, flg?tension2:tension1, true);
					pole.addExtraWire(from2, to0, flg?tension1:tension2, true);
				}
				
				pole.addExtraWire(pole.connectionInfo.getFirst()[1].pointOnCable(0.1F), to1, 0);
			} else if (angle >= 112.5F && angle <= 247.5F) {
				
				this.process(pole.connectionInfo.getFirst()[0], connection[0], pole.connectionInfo.getFirst()[2], connection[2], (sf,st,lf,lt) -> {
					Vec3f fx = sf.getFromDirVec(0.4F);
					pole.addExtraWire(sf.fixedFrom.add(fx), st.fixedFrom, 0.5F, true);
					pole.addExtraWire(lf.fixedFrom, lt.fixedFrom, 0.5F, true);
				});
				
				this.sort(pole.connectionInfo.getFirst(), accessory.connectionInfo.getFirst(), (from, to) ->{
					pole.addExtraWire(from[0].pointOnCable(0.4F), to[0].fixedFrom, 0.5F, true);
					pole.addExtraWire(from[2].fixedFrom, to[2].fixedFrom, 0.5F, true);
				});
				
				if (f0t0 < f0t2) {
					if (f0t0 < f2t2) {
						Vec3f fx = pole.connectionInfo.getFirst()[0].getFromDirVec(0.4F);
						pole.addExtraWire(from0.add(fx), to0, 0.5F, true);
						pole.addExtraWire(from2, to2, 0.5F, true);
					} else {
						Vec3f fx = pole.connectionInfo.getFirst()[2].getFromDirVec(0.4F);
						pole.addExtraWire(from0, to0, 0.5F, true);
						pole.addExtraWire(from2.add(fx), to2, 0.5F, true);
					}
				} else {
					if (f0t2 < f2t0) {
						Vec3f fx = pole.connectionInfo.getFirst()[0].getFromDirVec(0.4F);
						pole.addExtraWire(from0.add(fx), to2, 0.5F, true);
						pole.addExtraWire(from2, to0, 0.5F, true);
					} else {
						Vec3f fx = pole.connectionInfo.getFirst()[2].getFromDirVec(0.4F);
						pole.addExtraWire(from0, to2, 0.5F, true);
						pole.addExtraWire(from2.add(fx), to0, 0.5F, true);
					}
				}
				
				RawQuadGroup insulator = Models.render10kVInsulator(ModelLoader.defaultTextureGetter().apply(new ResourceLocation(ResourcePaths.metal)), ModelLoader.defaultTextureGetter().apply(new ResourceLocation(ResourcePaths.glass_insulator)));
				insulator.translateCoord(0.5F, 1, 0.5F).bake(pole.quadBuffer);
				Vec3f pt = (new Vec3f(0.5F,1.5F,0.5F)).add(pole.pos);
				pole.addExtraWire(from1, pt, -0.3F);
				pole.addExtraWire(pt, to1, -0.4F);
			} else {
				float gg = (angleFrom+angleTo)/2;
				if (angle<180)
					gg += 180;
				
				RawQuadGroup insulator = Models.render10kVInsulator(ModelLoader.defaultTextureGetter().apply(new ResourceLocation(ResourcePaths.metal)), ModelLoader.defaultTextureGetter().apply(new ResourceLocation(ResourcePaths.glass_insulator)));
				insulator.rotateAroundX(90).translateCoord(0, -1.2F, 0.125F).rotateAroundY(90+gg).translateCoord(0.5F, 0, 0.5F).bake(pole.quadBuffer);
				
				Vec3f pt = new Vec3f(	0.65F *MathAssitant.cosAngle(gg) + 0.5F + pole.pos.getX()
										, -1.2F + pole.pos.getY(), 
										-0.65F *MathAssitant.sinAngle(gg) + 0.5F + pole.pos.getZ());
				
				this.sort(pole.connectionInfo.getFirst(), accessory.connectionInfo.getFirst(), (from, to) ->{
					pole.addExtraWire(from[0].fixedFrom, to[0].fixedFrom, 0.1F, true);
					pole.addExtraWire(from[2].fixedFrom, pt, 0.4F, true);
					pole.addExtraWire(pt, to[2].fixedFrom, 0.4F, true);
				});
				
				this.process(pole.connectionInfo.getFirst()[0], connection[0], pole.connectionInfo.getFirst()[2], connection[2], (sf,st,lf,lt) -> {
					pole.addExtraWire(sf.fixedFrom, st.fixedFrom, 0.1F, true);
					pole.addExtraWire(lf.fixedFrom, pt, 0.4F, true);
					pole.addExtraWire(pt, lt.fixedFrom, 0.4F, true);
				});
				
				if (isMin(f0t0, f0t2, f2t0, f2t2)) {
					pole.addExtraWire(from0, to0, 0.1F, true);
					pole.addExtraWire(from2, pt, 0.4F, true);
					pole.addExtraWire(pt, to2, 0.4F, true);
				} else if (isMin(f0t2, f0t0, f2t0, f2t2)) {
					pole.addExtraWire(from0, to2, 0.1F, true);
					pole.addExtraWire(from2, pt, 0.4F, true);
					pole.addExtraWire(pt, to0, 0.4F, true);
				} else if (isMin(f2t0, f0t2, f0t0, f2t2)) {
					pole.addExtraWire(from2, to0, 0.1F, true);
					pole.addExtraWire(from0, pt, 0.4F, true);
					pole.addExtraWire(pt, to2, 0.4F, true);
				} else if (isMin(f2t2, f0t2, f2t0, f0t0)) {
					pole.addExtraWire(from2, to2, 0.1F, true);
					pole.addExtraWire(from0, pt, 0.4F, true);
					pole.addExtraWire(pt, to0, 0.4F, true);
				}
			}
*/
    	}
	}
	
	public static void sort(PowerPoleRenderHelper.ConnectionInfo[] from, PowerPoleRenderHelper.ConnectionInfo[] to, Action p) {
		float f0t0 = from[0].fixedFrom.distanceXZ(to[0].fixedFrom);
		float f2t2 = from[from.length-1].fixedFrom.distanceXZ(to[to.length-1].fixedFrom);
		float f0t2 = from[0].fixedFrom.distanceXZ(to[to.length-1].fixedFrom);
		float f2t0 = from[from.length-1].fixedFrom.distanceXZ(to[0].fixedFrom);
		
		if (MathAssitant.isMin(f0t0, f0t2, f2t0, f2t2)) {
			p.action(from, to);
		} else if (MathAssitant.isMin(f0t2, f0t0, f2t0, f2t2)) {
			p.action(from, reverse(to));
		} else if (MathAssitant.isMin(f2t0, f0t2, f0t0, f2t2)) {
			p.action(reverse(from), to);
		} else if (MathAssitant.isMin(f2t2, f0t2, f2t0, f0t0)) {
			p.action(reverse(from), reverse(to));
		}
	}
	
	public static PowerPoleRenderHelper.ConnectionInfo[] reverse(PowerPoleRenderHelper.ConnectionInfo[] in){
		PowerPoleRenderHelper.ConnectionInfo[] ret = new PowerPoleRenderHelper.ConnectionInfo[in.length];
		for (int i=0; i<in.length; i++) {
			ret[i] = in[in.length-1-i];
		}
		return ret;
	}
	
	public static interface Action {
		public void action(PowerPoleRenderHelper.ConnectionInfo[] from, PowerPoleRenderHelper.ConnectionInfo[] to);
	}
	
/*	public static void process(PowerPoleRenderHelper.ConnectionInfo from0, PowerPoleRenderHelper.ConnectionInfo to0, PowerPoleRenderHelper.ConnectionInfo from2, PowerPoleRenderHelper.ConnectionInfo to2, Processor p) {
		float f0t0 = from0.fixedFrom.distanceXZ(to0.fixedFrom);
		float f2t2 = from2.fixedFrom.distanceXZ(to2.fixedFrom);
		float f0t2 = from0.fixedFrom.distanceXZ(to2.fixedFrom);
		float f2t0 = from2.fixedFrom.distanceXZ(to0.fixedFrom);
		
		if (isMin(f0t0, f0t2, f2t0, f2t2)) {
			p.process(from0, to0, from2, to2);
		} else if (isMin(f0t2, f0t0, f2t0, f2t2)) {
			p.process(from0, to2, from2, to0);
		} else if (isMin(f2t0, f0t2, f0t0, f2t2)) {
			p.process(from2, to0, from0, to2);
		} else if (isMin(f2t2, f0t2, f2t0, f0t0)) {
			p.process(from2, to2, from0, to0);
		}
	}
	
	public static interface Processor {
		public void process(PowerPoleRenderHelper.ConnectionInfo from0, PowerPoleRenderHelper.ConnectionInfo to0, PowerPoleRenderHelper.ConnectionInfo from2, PowerPoleRenderHelper.ConnectionInfo to2);
	}*/
}
