package simelectricity.essential.client.grid.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PowerTransformerRawModel implements IModel{
	private final List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
    private final IModel[] models;
    private final IModelState defaultState;
    
    public PowerTransformerRawModel() throws Exception {
    	String modelName = "sime_essential:powertransformer.obj";	//Sketch Up --*.dae--> Blender --> *.obj & *.mtl
		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
		LinkedList<Variant> variants = new LinkedList<Variant>();
		boolean uvLock = false;
		
		variants.add(new Variant(new ResourceLocation(modelName), ModelRotation.X0_Y270, uvLock, 1));	//North
		variants.add(new Variant(new ResourceLocation(modelName), ModelRotation.X0_Y90, uvLock, 1));	//South
		variants.add(new Variant(new ResourceLocation(modelName), ModelRotation.X0_Y180, uvLock, 1));	//West
		variants.add(new Variant(new ResourceLocation(modelName), ModelRotation.X0_Y0, uvLock, 1));		//East
		
		this.models = new IModel[4];
		int i = 0;
		for (Variant variant: variants){
			ResourceLocation loc = variant.getModelLocation();
			if (!this.dependencies.contains(loc))
				this.dependencies.add(loc);
			
			IModel preModel = ModelLoaderRegistry.getModel(loc);
			IModel model = variant.process(preModel);
			
	        for(ResourceLocation location : model.getDependencies())
	        {
	            ModelLoaderRegistry.getModelOrMissing(location);
	        }
	        
	        this.textures.addAll(model.getTextures()); // Kick this, just in case.
			
	        this.models[i] = model;
	        i++;
	        builder.add(Pair.of(model, variant.getState()));
		}
		
		this.defaultState = new MultiModelState(builder.build());
    }
    
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.copyOf(dependencies);
	}
	
	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.copyOf(textures);
	}

	@Override
	public IModelState getDefaultState() {
		return defaultState;
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		IBakedModel[] bakedModelList = new IBakedModel[4];
		for (int i=0; i<4; i++){
			IModel model = this.models[i];
			IModelState actualState = MultiModelState.getPartState(state, model, i);
			IBakedModel bakedModel = model.bake(actualState, format, bakedTextureGetter);
			bakedModelList[i] = bakedModel;
		}
		
		return new PowerTransformerModel(bakedModelList);
	}
}
