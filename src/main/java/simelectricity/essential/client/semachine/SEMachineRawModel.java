package simelectricity.essential.client.semachine;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SEMachineRawModel implements IModel{
	private final List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
    
    private final boolean hasSecondState;
    private final IModel model;
    private final IModel model2;
    private final IModelState defaultState;
	
    private static ModelRotation[] rotationMatrix = new ModelRotation[] {
    		 ModelRotation.X270_Y0, //Down
    		 ModelRotation.X90_Y0,	//Up
    		 ModelRotation.X0_Y180,	//North
    		 ModelRotation.X0_Y0,	//South
    		 ModelRotation.X0_Y90,	//West
    		 ModelRotation.X0_Y270	//East
    };
    
	public SEMachineRawModel(String domain, String modelName, EnumFacing facing, boolean hasSecondState) throws Exception {
		String firstStateModelName = domain + ":block/" + modelName;
		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
		LinkedList<Variant> variants = new LinkedList<Variant>();
		boolean uvLock = false;
		
		//First state
		Variant var1 = new Variant(new ResourceLocation(firstStateModelName), rotationMatrix[facing.ordinal()], uvLock, 1);
		//First
		ResourceLocation loc = var1.getModelLocation();
		if (!this.dependencies.contains(loc))
			this.dependencies.add(loc);
		
		IModel preModel = ModelLoaderRegistry.getModel(loc);
		IModel model = var1.process(preModel);
		
        for(ResourceLocation location : model.getDependencies())
            ModelLoaderRegistry.getModelOrMissing(location);
        
        this.textures.addAll(model.getTextures()); // Kick this, just in case.
        this.model = model;
        builder.add(Pair.of(model, var1.getState()));
        
		
		if (hasSecondState){
			String secondStateModelName = firstStateModelName + "_2";
			Variant var2 = new Variant(new ResourceLocation(secondStateModelName), rotationMatrix[facing.ordinal()], uvLock, 1);	//Down
			
			loc = var2.getModelLocation();
			if (!this.dependencies.contains(loc))
				this.dependencies.add(loc);
			
			preModel = ModelLoaderRegistry.getModel(loc);
			model = var2.process(preModel);
			
	        for(ResourceLocation location : model.getDependencies())
	            ModelLoaderRegistry.getModelOrMissing(location);
	        
	        this.textures.addAll(model.getTextures()); // Kick this, just in case.
	        this.model2 = model;
	        builder.add(Pair.of(model, var2.getState()));
		}else {
			this.model2 = null;
		}
		
		this.defaultState = new MultiModelState(builder.build());
        this.hasSecondState = hasSecondState;
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
		
		IModelState actualState2 = null;
		IBakedModel bakedModel2 = null;
		
		if (this.hasSecondState){
			actualState2 = MultiModelState.getPartState(state, model, 1);
			bakedModel2 = model2.bake(actualState2, format, bakedTextureGetter);
		}

		
		return new SEMachineModel(bakedModel, bakedModel2);
	}
}
