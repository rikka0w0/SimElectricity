package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
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
    public final static FastTESRPowerTransformer instance = new FastTESRPowerTransformer();
	private FastTESRPowerTransformer() {}
	
    public final static LinkedList<BakedQuad>[] bakedModelUnmirrored = new LinkedList[4];
    public final static LinkedList<BakedQuad>[] bakedModelMirrored = new LinkedList[4];
	
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
		List<BakedQuad> quads = mirrored ? bakedModelMirrored[facing] : bakedModelUnmirrored[facing];
		
		int i = 15728640;	//TODO: Fix light calculation
		for (BakedQuad quad: quads) {
			
			buffer.addVertexData(quad.getVertexData());
			buffer.putBrightness4(i, i, i, i);
			
            float diffuse = LightUtil.diffuseLight(mirrored ? quad.getFace() : quad.getFace().getOpposite());
            
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
			buffer.putPosition(pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
