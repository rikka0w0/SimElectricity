package simelectricity.essential.client.grid.pole;

import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import simelectricity.essential.client.grid.PowerPoleTER;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleMetal35kV;

public class MetalPole35kVTER extends PowerPoleTER<TilePoleMetal35kV>{
	public MetalPole35kVTER(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void bake(TilePoleMetal35kV te, PowerPoleRenderHelper helper) {
		List<BakedQuad> quads = helper.quadBuffer;

		BlockState blockState = te.getBlockState();
		if (blockState == null)
			return;
		BakedModel bakedmodel = Minecraft.getInstance().getModelManager()
				.getBlockModelShaper().getBlockModel(blockState);
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
