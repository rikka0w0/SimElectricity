package simelectricity.essential.client.grid.pole;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;
import rikka.librikka.DirHorizontal8;
import simelectricity.essential.grid.TilePoleMetal35kV;

public class MetalPole35kVBottomTER extends TileEntityRendererFast<TilePoleMetal35kV.Bottom>{
	public MetalPole35kVBottomTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super();
	}
	
	public void renderTileEntityFast(TilePoleMetal35kV.Bottom te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
		DirHorizontal8 facing8 = te.facing();
		int facing = (8-facing8.ordinal()) & 7;
		int part = te.getPartId();	

		BlockState blockState = te.getBlockState();
		if (blockState == null)
			return;
		IBakedModel bakedmodel = Minecraft.getInstance().getModelManager()
				.getBlockModelShapes().getModel(blockState);
		if (!(bakedmodel instanceof MetalPole35kVModel))
			return;
		MetalPole35kVModel model = (MetalPole35kVModel) bakedmodel;
		
		buffer.setTranslation(x, y, z);
		
		int i = 15728640;
		for (BakedQuad quad: model.bakedModelBasePart[facing][part]) {
			buffer.addVertexData(quad.getVertexData());
			buffer.putBrightness4(i, i, i, i);
			
			float diffuse = 1;
            if(quad.shouldApplyDiffuseLighting())
                diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quad.getFace());

            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
			
			buffer.putPosition(0, 0, 0);
		}
	}
}
