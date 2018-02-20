package simelectricity.essential.client.grid;

import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;

public class Sorter {
	public static void minDist(PowerPoleRenderHelper.ConnectionInfo[] from, PowerPoleRenderHelper.ConnectionInfo[] to,
							   Action<PowerPoleRenderHelper.ConnectionInfo[], PowerPoleRenderHelper.ConnectionInfo[]> action) {
		float f0t0 = from[0].pointOnCable(0.1F).distanceXZ(to[0].pointOnCable(0.1F));
		float f2t2 = from[from.length-1].pointOnCable(0.1F).distanceXZ(to[to.length-1].pointOnCable(0.1F));
		float f0t2 = from[0].pointOnCable(0.1F).distanceXZ(to[to.length-1].pointOnCable(0.1F));
		float f2t0 = from[from.length-1].pointOnCable(0.1F).distanceXZ(to[0].pointOnCable(0.1F));
		
		if (MathAssitant.isMin(f0t0, f0t2, f2t0, f2t2)) {
			action.perform(from, to);
		} else if (MathAssitant.isMin(f0t2, f0t0, f2t0, f2t2)) {
			action.perform(from, reverse(to));
		} else if (MathAssitant.isMin(f2t0, f0t2, f0t0, f2t2)) {
			action.perform(reverse(from), to);
		} else if (MathAssitant.isMin(f2t2, f0t2, f2t0, f0t0)) {
			action.perform(reverse(from), reverse(to));
		}
	}

	public static void minDist(Vec3f[] from, Vec3f[] to, Action<Vec3f[], Vec3f[]> action) {
		float f0t0 = from[0].distanceXZ(to[0]);
		float f2t2 = from[from.length-1].distanceXZ(to[to.length-1]);
		float f0t2 = from[0].distanceXZ(to[to.length-1]);
		float f2t0 = from[from.length-1].distanceXZ(to[0]);

		if (MathAssitant.isMin(f0t0, f0t2, f2t0, f2t2)) {
			action.perform(from, to);
		} else if (MathAssitant.isMin(f0t2, f0t0, f2t0, f2t2)) {
			action.perform(from, reverse(to));
		} else if (MathAssitant.isMin(f2t0, f0t2, f0t0, f2t2)) {
			action.perform(reverse(from), to);
		} else if (MathAssitant.isMin(f2t2, f0t2, f2t0, f0t0)) {
			action.perform(reverse(from), reverse(to));
		}
	}

	public static PowerPoleRenderHelper.ConnectionInfo[] reverse(PowerPoleRenderHelper.ConnectionInfo[] in){
		PowerPoleRenderHelper.ConnectionInfo[] ret = new PowerPoleRenderHelper.ConnectionInfo[in.length];
		for (int i=0; i<in.length; i++) {
			ret[i] = in[in.length-1-i];
		}
		return ret;
	}

	public static Vec3f[] reverse(Vec3f[] in){
		Vec3f[] ret = new Vec3f[in.length];
		for (int i=0; i<in.length; i++) {
			ret[i] = in[in.length-1-i];
		}
		return ret;
	}
	
	public static interface Action<T1, T2> {
		public void perform(T1 from, T2 to);
	}
}
