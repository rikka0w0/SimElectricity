package simelectricity.essential.client.grid.pole;

import java.util.LinkedList;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.client.model.pipeline.LightUtil;
import simelectricity.essential.grid.BlockPowerPoleBottom;

public class FastTESRPowerPoleBottom extends FastTESR<BlockPowerPoleBottom.Tile>{
	public final static TileEntitySpecialRenderer instance = new FastTESRPowerPoleBottom();
	private FastTESRPowerPoleBottom () {}
	
	public final static LinkedList<BakedQuad>[] bakedModel = new LinkedList[8];
	
    @Override
    public boolean isGlobalRenderer(BlockPowerPoleBottom.Tile te) {
        return true;
    }
	
	@Override
	public void renderTileEntityFast(BlockPowerPoleBottom.Tile te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer) {
		int facing = te.getFacing();
		BlockPos pos = te.getPos();
		
		buffer.setTranslation(x, y, z);
		
		int i = 15728640;
		for (BakedQuad quad: bakedModel[facing]) {
			
			buffer.addVertexData(quad.getVertexData());
			buffer.putBrightness4(i, i, i, i);
			
            float diffuse = LightUtil.diffuseLight(quad.getFace());
            
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
            buffer.putPosition(0, 0, 0);
		}
	}

}
