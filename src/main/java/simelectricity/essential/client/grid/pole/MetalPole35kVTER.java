package simelectricity.essential.client.grid.pole;

import java.util.List;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleMetal35kV;

public class MetalPole35kVTER extends FastTESRPowerPole<TilePoleMetal35kV>{
	public MetalPole35kVTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
		
	@Override
	protected void bake(TilePoleMetal35kV te, PowerPoleRenderHelper helper) {
		List<BakedQuad> quads = helper.quadBuffer;
		
		if (te.isType0()) {
			quads.addAll(MetalPole35kVModel.bakedModelType0[helper.orientation]);
			
	        if (helper.connectionList.size() > 1)
            quads.addAll(MetalPole35kVModel.insulator35Kv[helper.orientation]);
			
	        renderInsulator(helper, MetalPole35kVModel.modelInsulator);
		} else {
			quads.addAll(MetalPole35kVModel.bakedModelType1[helper.orientation]);
		}
		
        super.bake(te, helper);
	}
}
