package simelectricity.essential.client.semachine;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry2;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import rikka.librikka.LazyValue;
import simelectricity.essential.Essential;

public class SEMachineModelLoader implements IModelLoader<SEMachineModelLoader.Wrapper> {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "machine");
	public final static IModelLoader<?> instance = new SEMachineModelLoader();
	public final static LazyValue<Gson> vanillaParser = new LazyValue<>(
			()->ObfuscationReflectionHelper.getPrivateValue(BlockModel.class, null, "field_178319_a")
		);
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// We didn't cache anything so do nothing here
	}
	
	@Override
	public Wrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        if (modelContents.has("loader2")) {
            String loader2 = JSONUtils.getString(modelContents, "loader2");
            final IModelGeometry<?> secondaryGeometry = ModelLoaderRegistry2.getModel(
            		new ResourceLocation(loader2), 
            		deserializationContext, 
            		modelContents);
            
            if (secondaryGeometry == null)
            	return null;
            
            return new ForgeWrapper(secondaryGeometry);
        } else {
            return new VanillaWrapper();
        }
	}
	
	public static IModelGeometry<?> getModelGeometry(BlockModel model) {
		IModelGeometry<?> geometry = null;
		try {
			Field cg = BlockModelConfiguration.class.getDeclaredField("customGeometry");
			cg.setAccessible(true);
			geometry = (IModelGeometry<?>) cg.get(((BlockModel) model).customData);
			cg.setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return geometry;
	}
	
	public static interface Wrapper extends IModelGeometry<Wrapper> {
		
	}
	
	public static class VanillaWrapper implements Wrapper {
		@Override
		public IBakedModel bake(
				IModelConfiguration owner, 
				ModelBakery bakery, 
				Function<ResourceLocation, TextureAtlasSprite> spriteGetter, 
				ISprite sprite, 
				VertexFormat format, 
				ItemOverrideList overrides) {
			
			IUnbakedModel unbakedOwner = owner.getOwnerModel();
			if (!(unbakedOwner instanceof BlockModel))
				return unbakedOwner.bake(bakery, spriteGetter, sprite, format);
			
			BlockModel ownerBlockModel = (BlockModel) unbakedOwner;
			for (BlockModel blockModel=ownerBlockModel; blockModel!=null; blockModel=blockModel.parent) {
				if (getModelGeometry(blockModel) == this) {
					IBakedModel vanillaModel;
					synchronized(blockModel.customData) {
						blockModel.customData.setCustomGeometry(null);
						vanillaModel = ownerBlockModel.bake(bakery, ownerBlockModel, spriteGetter, sprite, format);
						blockModel.customData.setCustomGeometry(this);
					}
					
					return new SEMachineModel(vanillaModel);
				}
			}
			
			throw new RuntimeException("Unable to locate the ModelGeometry in the model dependency tree!");
		}

		@Override
		public Collection<ResourceLocation> getTextureDependencies(
				IModelConfiguration owner, 
				Function<ResourceLocation, IUnbakedModel> modelGetter, 
				Set<String> missingTextureErrors) {
			
			IUnbakedModel unbakedOwner = owner.getOwnerModel();
			if (!(unbakedOwner instanceof BlockModel))
				return unbakedOwner.getTextures(modelGetter, missingTextureErrors);
			
			BlockModel ownerBlockModel = (BlockModel) unbakedOwner;
			for (BlockModel blockModel=ownerBlockModel; blockModel!=null; blockModel=blockModel.parent) {
				if (getModelGeometry(blockModel) == this) {
					Collection<ResourceLocation> materials;
					synchronized(blockModel.customData) {
						blockModel.customData.setCustomGeometry(null);
						materials = ownerBlockModel.getTextures(modelGetter, missingTextureErrors);
						blockModel.customData.setCustomGeometry(this);
					}
					
					return materials;
				}
			}
			
			throw new RuntimeException("Unable to locate the ModelGeometry in the model dependency tree!");
		}
	}

	public static class ForgeWrapper implements Wrapper {
		public final IModelGeometry<?> modelGeometry;
		
		public ForgeWrapper(IModelGeometry<?> modelGeometry) {
			this.modelGeometry = modelGeometry;
		}
		
		@Override
		public IBakedModel bake(
				IModelConfiguration owner, 
				ModelBakery bakery, 
				Function<ResourceLocation, TextureAtlasSprite> spriteGetter, 
				ISprite sprite, 
				VertexFormat format, 
				ItemOverrideList overrides) {
			IBakedModel bakedModel = 
				modelGeometry.bake(owner, bakery, spriteGetter, sprite, format, overrides);
			return new SEMachineModel(bakedModel);
		}

		@Override
		public Collection<ResourceLocation> getTextureDependencies(
				IModelConfiguration owner, 
				Function<ResourceLocation, IUnbakedModel> modelGetter, 
				Set<String> missingTextureErrors) {
			return modelGeometry.getTextureDependencies(owner, modelGetter, missingTextureErrors);
		}
	}
}
