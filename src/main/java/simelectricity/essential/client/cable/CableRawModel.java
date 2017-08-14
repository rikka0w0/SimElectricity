package simelectricity.essential.client.cable;

import com.google.common.base.Function;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import simelectricity.essential.client.TextureLoaderModel;

public class CableRawModel extends TextureLoaderModel{	
    private final ResourceLocation insulatorTexture, conductorTexture;
    private final float thickness;
    
	public CableRawModel(String domain, String name, float thickness) throws Exception{
		super();
		String insulatorTexture = domain + ":blocks/cable/" + name + "_insulator";
		String conductorTexture = domain + ":blocks/cable/" + name + "_copper";

        this.insulatorTexture = new ResourceLocation(insulatorTexture);
        this.conductorTexture = new ResourceLocation(conductorTexture); 
        
        textures.add(this.insulatorTexture); // We just want to bypass the ModelBakery
        textures.add(this.conductorTexture); // and load our texture
        
        this.thickness = thickness;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		TextureAtlasSprite insulatorTexture = bakedTextureGetter.apply(this.insulatorTexture);
		TextureAtlasSprite conductorTexture = bakedTextureGetter.apply(this.conductorTexture);
		return new CableModel(insulatorTexture, conductorTexture, thickness);
	}
}
