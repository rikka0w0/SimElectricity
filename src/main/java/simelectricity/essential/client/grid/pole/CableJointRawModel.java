package simelectricity.essential.client.grid.pole;

import com.google.common.base.Function;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import simelectricity.essential.client.TextureLoaderModel;

public class CableJointRawModel extends TextureLoaderModel{
	private final ResourceLocation textures[] = new ResourceLocation[3];
	private final int facing;
	
	public CableJointRawModel(int facing) {
		this.textures[0] = registerTexture("sime_essential:render/transmission/essential_cable_joint_texture_updown");
    	this.textures[1] = registerTexture("sime_essential:render/transmission/essential_cable_joint_texture_metal");
    	this.textures[2] = registerTexture("sime_essential:render/transmission/essential_cable_joint_texture_side");
    	
    	this.facing = facing;
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new CableJointModel(facing, new TextureAtlasSprite[] {
				bakedTextureGetter.apply(textures[0]),
				bakedTextureGetter.apply(textures[1]),
				bakedTextureGetter.apply(textures[2])
		});
	}	

}
