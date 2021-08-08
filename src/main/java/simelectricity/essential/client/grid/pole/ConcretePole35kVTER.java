package simelectricity.essential.client.grid.pole;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.PowerPoleTER;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleConcrete35kV;

public class ConcretePole35kVTER extends PowerPoleTER<TilePoleConcrete35kV>{
	public ConcretePole35kVTER(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	private RawQuadGroup modelInsulator = null;

	@Override
	protected void bake(TilePoleConcrete35kV te, PowerPoleRenderHelper helper) {
//		List<BakedQuad> quads = helper.quadBuffer;

		if (modelInsulator == null) {
			ModelResourceLocation modelResLoc = BlockModelShaper.stateToModelLocation(te.getBlockState());
			BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelResLoc);
			if (model instanceof ConcretePole35kVModel) {
				this.modelInsulator = ((ConcretePole35kVModel)model).modelInsulator;
			}
		}

		if (te.isType0() && modelInsulator != null)
			renderInsulator(helper, modelInsulator);

		super.bake(te, helper);
	}
}
