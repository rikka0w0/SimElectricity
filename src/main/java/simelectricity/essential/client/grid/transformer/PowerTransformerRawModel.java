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
    private final IModel model;
    private final IModelState defaultState;
    
	private final int facing;
	private final boolean mirrored;
	
	private final ResourceLocation textureMetal, textureInsulator;
    
    private final static ModelRotation[] rotationMatrix  = new ModelRotation[]{
    		ModelRotation.X0_Y270,
    		ModelRotation.X0_Y90,
    		ModelRotation.X0_Y180,
    		ModelRotation.X0_Y0
    };
    
    public PowerTransformerRawModel(int facing, boolean mirrored) throws Exception {
    	String modelName = "sime_essential:powertransformer.obj";	//Sketch Up --*.dae--> Blender --> *.obj & *.mtl
		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
		LinkedList<Variant> variants = new LinkedList<Variant>();
		boolean uvLock = false;
		
		Variant variant = new Variant(new ResourceLocation(modelName), rotationMatrix[facing], uvLock, 1);
		
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
        this.model = model;
        builder.add(Pair.of(model, variant.getState()));
		
		this.defaultState = new MultiModelState(builder.build());
		
		this.facing = facing;
		this.mirrored = mirrored;
		
		//Custom texture
		this.textureMetal = new ResourceLocation("sime_essential:render/transmission/metal");
		this.textureInsulator = new ResourceLocation("sime_essential:render/transmission/glass_insulator");
		this.textures.add(this.textureMetal);
		this.textures.add(this.textureInsulator);
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
		IModelState actualState = MultiModelState.getPartState(state, model, 0);
		IBakedModel bakedModel = model.bake(actualState, format, bakedTextureGetter);
		
		return new PowerTransformerModel(facing, mirrored, bakedModel,
				bakedTextureGetter.apply(textureMetal),
				bakedTextureGetter.apply(textureInsulator));
	}
}
