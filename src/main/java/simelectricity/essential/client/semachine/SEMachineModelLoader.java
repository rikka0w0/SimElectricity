package simelectricity.essential.client.semachine;

import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import simelectricity.essential.Essential;

public class SEMachineModelLoader implements IGeometryLoader<SEMachineModelLoader.Wrapper> {
	public final static ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Essential.MODID, "machine");
	public final static SEMachineModelLoader instance = new SEMachineModelLoader();

	@Override
	public Wrapper read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
        if (modelContents.has("loader2")) {
            String loader2 = GsonHelper.getAsString(modelContents, "loader2");
            // Check if it's the OBJ loader
            if (loader2.contains("obj")) {
                IUnbakedGeometry<?> secondaryGeometry = ObjLoader.INSTANCE.read(modelContents, deserializationContext);
                return new ForgeWrapper(secondaryGeometry);
            }
            return null;
        } else {
            BlockModel vanillaBlockModel = deserializationContext.deserialize(modelContents, BlockModel.class);
            return new VanillaWrapper(vanillaBlockModel);
        }
	}

	public static interface Wrapper extends IUnbakedGeometry<Wrapper> {

	}

	public static class VanillaWrapper implements Wrapper {
		private final BlockModel vanillaBlockModel;

		public VanillaWrapper(BlockModel vanillaBlockModel) {
			this.vanillaBlockModel = vanillaBlockModel;
		}

		@Override
		public BakedModel bake(IGeometryBakingContext owner,
				ModelBaker bakery,
				Function<Material, TextureAtlasSprite> spriteGetter,
				ModelState modelTransform,
				ItemOverrides overrides) {
			// In 1.21, BlockModel's bake method is resolved by bakery and handles rendering.
			// Let's call the BlockModel's bake method. Depending on mapping, we need correct parameters.
			BakedModel vanillaModel = vanillaBlockModel.bake(bakery, vanillaBlockModel, spriteGetter, modelTransform, true);
			return new SEMachineModel(vanillaModel);
		}
	}

	public static class ForgeWrapper implements Wrapper {
		public final IUnbakedGeometry<?> modelGeometry;

		public ForgeWrapper(IUnbakedGeometry<?> modelGeometry) {
			this.modelGeometry = modelGeometry;
		}

		@Override
		public BakedModel bake(IGeometryBakingContext owner,
				ModelBaker bakery,
				Function<Material, TextureAtlasSprite> spriteGetter,
				ModelState modelTransform,
				ItemOverrides overrides) {
			BakedModel bakedModel =
				modelGeometry.bake(owner, bakery, spriteGetter, modelTransform, overrides);
			return new SEMachineModel(bakedModel);
		}
	}
}
