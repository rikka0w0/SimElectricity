package simelectricity.essential.client.grid.pole;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleConcrete35kV;

public class ConcretePole35kVTER extends FastTESRPowerPole<TilePoleConcrete35kV>{
	public ConcretePole35kVTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	private RawQuadGroup modelInsulator = null;
	
	@Override
	protected void bake(TilePoleConcrete35kV te, PowerPoleRenderHelper helper) {
		List<BakedQuad> quads = helper.quadBuffer;
		
		if (modelInsulator == null) {
			ModelResourceLocation modelResLoc = BlockModelShapes.getModelLocation(te.getBlockState());
			IBakedModel model = Minecraft.getInstance().getModelManager().getModel(modelResLoc);
			if (model instanceof ConcretePole35kVModel) {
				this.modelInsulator = ((ConcretePole35kVModel)model).modelInsulator;
			}
		}
		
		if (te.isType0() && modelInsulator != null)
			renderInsulator(helper, modelInsulator);
		
		super.bake(te, helper);
	}
}
