package simelectricity.essential.client.grid.transformer;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.client.BlockRenderModel;
import simelectricity.essential.grid.Properties;

public class PowerTransformerModel extends BlockRenderModel {
	private final IBakedModel[] unmirrored;
	
	public PowerTransformerModel(IBakedModel[] unmirrored) {
		this.unmirrored = unmirrored;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return unmirrored[0].getParticleTexture();
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		int facing = state.getValue(Properties.propertyFacing2);
		return unmirrored[facing].getQuads(state, side, rand);
	}
}
