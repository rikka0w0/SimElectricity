package simelectricity.essential.client.grid.pole;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import simelectricity.essential.client.grid.PowerPoleTER;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleMetal35kV;

public class MetalPole35kVTER extends PowerPoleTER<TilePoleMetal35kV>{
	public MetalPole35kVTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
		
	@Override
	protected void bake(TilePoleMetal35kV te, PowerPoleRenderHelper helper) {
		List<BakedQuad> quads = helper.quadBuffer;

		BlockState blockState = te.getBlockState();
		if (blockState == null)
			return;
		IBakedModel bakedmodel = Minecraft.getInstance().getModelManager()
				.getBlockModelShapes().getModel(blockState);
		if (!(bakedmodel instanceof MetalPole35kVModel))
			return;
		MetalPole35kVModel model = (MetalPole35kVModel) bakedmodel;

		quads.addAll(model.bakedModelTop[helper.orientation]);
		if (!model.terminals) {
	        if (helper.connectionList.size() > 1)
	        	quads.addAll(model.insulator35Kv[helper.orientation]);

	        renderInsulator(helper, model.modelInsulator);
		}

        super.bake(te, helper);
	}
}
