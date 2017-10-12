package simelectricity.essential.client.grid.pole;

import java.util.LinkedList;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;
import simelectricity.essential.grid.TilePowerPole;
import simelectricity.essential.utils.client.SERenderHeap;

public class FastTESRPowerPoleTop extends FastTESRPowerPole<TilePowerPole>{
	public final static FastTESRPowerPoleTop instance = new FastTESRPowerPoleTop();
	private FastTESRPowerPoleTop() {}
	
	public final static LinkedList<BakedQuad>[] insulator35Kv = new LinkedList[8];
	public final static LinkedList<BakedQuad>[] bakedModelType0 = new LinkedList[8];
	public final static LinkedList<BakedQuad>[] bakedModelType1 = new LinkedList[8];
	public static SERenderHeap modelInsulator = null;
	
	@Override
	public void renderTileEntityFast(TilePowerPole te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer) {
		super.renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, buffer);
	}
	
	@Override
	protected void bake(TilePowerPole te, PowerPoleRenderHelper helper) {
		LinkedList<BakedQuad> quads = helper.quadBuffer;
		
		if (te.isType0()) {
			quads.addAll(bakedModelType0[helper.rotationMC]);
			
	        if (helper.connectionInfo.size() > 1)
            quads.addAll(this.insulator35Kv[helper.rotationMC]);
			
	        for (ConnectionInfo[] connections : helper.connectionInfo) {
	            for (ConnectionInfo connection : connections) {
	                Models.renderInsulators(helper.pos, connection.from, connection.fixedTo, connection.insulatorAngle, modelInsulator, quads);
	            }
	        }
		} else {
			quads.addAll(bakedModelType1[helper.rotationMC]);
		}
		
        super.bake(te, helper);
	}
}
