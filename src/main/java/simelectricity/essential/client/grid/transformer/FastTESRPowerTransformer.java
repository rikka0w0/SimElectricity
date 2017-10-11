package simelectricity.essential.client.grid.transformer;

import java.util.List;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;

@SideOnly(Side.CLIENT)
public class FastTESRPowerTransformer extends FastTESR<TilePowerTransformerPlaceHolder.Render>{
    @Override
    public boolean isGlobalRenderer(TilePowerTransformerPlaceHolder.Render te) {
        return true;
    }
	
	@Override
	public void renderTileEntityFast(TilePowerTransformerPlaceHolder.Render te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer) {
		BlockPos pos = te.getPos();    
		buffer.setTranslation(x-pos.getX(), y-pos.getY(), z-pos.getZ());
		
		int facing = te.getFacingInt();
		boolean mirrored = te.isMirrored();
		List<BakedQuad> quads = mirrored ? PowerTransformerRawModel.bakedModelMirrored[facing] : PowerTransformerRawModel.bakedModelUnmirrored[facing];
		
		int i = 15728640;	//TODO: Fix light calculation
		for (BakedQuad quad: quads) {
			
			buffer.addVertexData(quad.getVertexData());
			buffer.putBrightness4(i, i, i, i);
			
			int k = -1;
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            if(quad.shouldApplyDiffuseLighting()) {
            	//Fix lighting problem with mirrored models.
                float diffuse = LightUtil.diffuseLight(mirrored ? quad.getFace() : quad.getFace().getOpposite());
                f *= diffuse;
                f1 *= diffuse;
                f2 *= diffuse;
            }
            buffer.putColorMultiplier(f, f1, f2, 4);
            buffer.putColorMultiplier(f, f1, f2, 3);
            buffer.putColorMultiplier(f, f1, f2, 2);
            buffer.putColorMultiplier(f, f1, f2, 1);
			buffer.putPosition(pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
