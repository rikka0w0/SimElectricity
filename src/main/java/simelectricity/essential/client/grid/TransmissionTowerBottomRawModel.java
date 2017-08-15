package simelectricity.essential.client.grid;

import com.google.common.base.Function;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.TextureLoaderModel;

@SideOnly(Side.CLIENT)
public class TransmissionTowerBottomRawModel extends TextureLoaderModel{
	private final ResourceLocation texture;
	private final int facing;
	
	public TransmissionTowerBottomRawModel(int facing) {
		texture = registerTexture("sime_essential:render/transmission/metal");
		this.facing = facing;
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new TransmissionTowerBottomModel(facing, bakedTextureGetter.apply(texture));
	}

}
