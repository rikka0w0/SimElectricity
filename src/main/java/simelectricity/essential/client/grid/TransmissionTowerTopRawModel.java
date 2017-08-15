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
public class TransmissionTowerTopRawModel extends TextureLoaderModel{
	private final ResourceLocation textureMetal, textureInsulator;
	private final int facing;
	private final int type;
	
	public TransmissionTowerTopRawModel(int facing, int type) {
		textureMetal = registerTexture("sime_essential:render/transmission/metal");
		textureInsulator = registerTexture("sime_essential:render/transmission/glass_insulator");
		this.facing = facing;
		this.type = type;
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new TransmissionTowerTopModel(facing, type, bakedTextureGetter.apply(textureMetal), bakedTextureGetter.apply(textureInsulator));
	}
}
