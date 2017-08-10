package simelectricity.essential.client.cable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class CableRawModel implements IModel {
	private final List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
	private final Set<ResourceLocation> textures = Sets.newHashSet();
	
    private final ResourceLocation insulatorTexture, conductorTexture;
    private final float thickness;
    
	public CableRawModel(String domain, String name, float thickness) throws Exception{
		String insulatorTexture = domain + ":blocks/cable/" + name + "_insulator";
		String conductorTexture = domain + ":blocks/cable/" + name + "_copper";

        this.insulatorTexture = new ResourceLocation(insulatorTexture);
        this.conductorTexture = new ResourceLocation(conductorTexture); 
        
        textures.add(this.insulatorTexture); // We just want to bypass the ModelBakery
        textures.add(this.conductorTexture); // and load our texture
        
        this.thickness = thickness;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		return ImmutableList.copyOf(dependencies);
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.copyOf(textures);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		TextureAtlasSprite insulatorTexture = bakedTextureGetter.apply(this.insulatorTexture);
		TextureAtlasSprite conductorTexture = bakedTextureGetter.apply(this.conductorTexture);
		return new CableModel(insulatorTexture, conductorTexture, thickness);
	}


}
