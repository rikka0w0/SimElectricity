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
    
    private final IModel model;
    private final IModelState defaultState;
	
    private static ModelRotation[] rotationMatrix = new ModelRotation[] {
    		 ModelRotation.X270_Y0, //Down
    		 ModelRotation.X90_Y0,	//Up
    		 ModelRotation.X0_Y180,	//North
    		 ModelRotation.X0_Y0,	//South
    		 ModelRotation.X0_Y90,	//West
    		 ModelRotation.X0_Y270	//East
    };
    
	public SEMachineRawModel(String domain, String modelName, EnumFacing facing, boolean is2State) throws Exception {
		String firstStateModelName = domain + ":block/" + modelName + (is2State? "_2" : "");
		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
		LinkedList<Variant> variants = new LinkedList<Variant>();
		boolean uvLock = false;
		
		Variant var1 = new Variant(new ResourceLocation(firstStateModelName), rotationMatrix[facing.ordinal()], uvLock, 1);
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
		IModelState actualState = MultiModelState.getPartState(state, model, 0);
		IBakedModel bakedModel = model.bake(actualState, format, bakedTextureGetter);
		
		return new SEMachineModel(bakedModel);
	}
}
