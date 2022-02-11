package simelectricity.essential.client.semachine;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import simelectricity.essential.Essential;

public class SEMachineModelLoader implements IModelLoader<SEMachineModelLoader.Wrapper> {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "machine");
	public final static IModelLoader<?> instance = new SEMachineModelLoader();
/*
	public final static LazyLoadedValue<Gson> vanillaParser = new LazyLoadedValue<>(
			()->ObfuscationReflectionHelper.getPrivateValue(BlockModel.class, null, "GSON")
		);
*/

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		// We didn't cache anything so do nothing here
	}

	@Override
	public Wrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        if (modelContents.has("loader2")) {
            String loader2 = GsonHelper.getAsString(modelContents, "loader2");
            final IModelGeometry<?> secondaryGeometry = ModelLoaderRegistry.getModel(
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
		public BakedModel bake(IModelConfiguration owner,
				ModelBakery bakery,
				Function<Material, TextureAtlasSprite> spriteGetter,
				ModelState modelTransform,
				ItemOverrides overrides,
				ResourceLocation modelLocation) {

			UnbakedModel unbakedOwner = owner.getOwnerModel();
			if (!(unbakedOwner instanceof BlockModel))
				return unbakedOwner.bake(bakery, spriteGetter, modelTransform, modelLocation);

			BlockModel ownerBlockModel = (BlockModel) unbakedOwner;
			for (BlockModel blockModel=ownerBlockModel; blockModel!=null; blockModel=blockModel.parent) {
				if (getModelGeometry(blockModel) == this) {
					BakedModel vanillaModel;
					synchronized(blockModel.customData) {
						blockModel.customData.setCustomGeometry(null);
						vanillaModel = ownerBlockModel.bake(bakery, ownerBlockModel, spriteGetter, modelTransform, modelLocation, true);
						blockModel.customData.setCustomGeometry(this);
					}

					return new SEMachineModel(vanillaModel);
				}
			}

			throw new RuntimeException("Unable to locate the ModelGeometry in the model dependency tree!");
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner,
				Function<ResourceLocation, UnbakedModel> modelGetter,
				Set<Pair<String, String>> missingTextureErrors) {

			UnbakedModel unbakedOwner = owner.getOwnerModel();
			if (!(unbakedOwner instanceof BlockModel))
				return unbakedOwner.getMaterials(modelGetter, missingTextureErrors);

			BlockModel ownerBlockModel = (BlockModel) unbakedOwner;
			for (BlockModel blockModel=ownerBlockModel; blockModel!=null; blockModel=blockModel.parent) {
				if (getModelGeometry(blockModel) == this) {
					Collection<Material> materials;
					synchronized(blockModel.customData) {
						blockModel.customData.setCustomGeometry(null);
						materials = ownerBlockModel.getMaterials(modelGetter, missingTextureErrors);
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
		public BakedModel bake(IModelConfiguration owner,
				ModelBakery bakery,
				Function<Material, TextureAtlasSprite> spriteGetter,
				ModelState modelTransform,
				ItemOverrides overrides,
				ResourceLocation modelLocation) {
			BakedModel bakedModel =
				modelGeometry.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation);
			return new SEMachineModel(bakedModel);
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner,
				Function<ResourceLocation, UnbakedModel> modelGetter,
				Set<Pair<String, String>> missingTextureErrors) {
			return modelGeometry.getTextures(owner, modelGetter, missingTextureErrors);
		}
	}
}
