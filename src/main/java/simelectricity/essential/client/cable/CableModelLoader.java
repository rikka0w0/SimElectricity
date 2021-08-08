package simelectricity.essential.client.cable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import rikka.librikka.model.loader.ModelGeometryWrapper;
import simelectricity.essential.Essential;

public class CableModelLoader implements IModelLoader<ModelGeometryWrapper> {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "cable");
	public final static CableModelLoader instance = new CableModelLoader();
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		
	}

	@Override
	public ModelGeometryWrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		final String type = GsonHelper.getAsString(modelContents, "type");
		final float thickness = GsonHelper.getAsFloat(modelContents, "thickness");
		JsonObject textures = GsonHelper.getAsJsonObject(modelContents, "textures");

		return new ModelGeometryWrapper(textures, null, (context)->{
			TextureAtlasSprite insulator = context.getTextureByKey("insulator");
			TextureAtlasSprite conductor = context.getTextureByKey("conductor");
			
			if (type.toLowerCase().equals("cable"))
				return new CableModel(insulator, conductor, thickness);
			else if (type.toLowerCase().equals("wire"))
				return new WireModel(insulator, conductor, thickness);
			
			throw new RuntimeException("\"" + type + "\" is not implemented by " + id.toString());
		});
	}
	
	public static JsonObject serialize(String type, ResourceLocation insulator, ResourceLocation conductor, float thickness) {
		JsonObject root = new JsonObject();
		
		root.addProperty("loader", id.toString());
		root.addProperty("type", type);
		root.addProperty("thickness", thickness);

		JsonObject textures = new JsonObject();
		textures.addProperty("insulator", insulator.toString());
		textures.addProperty("conductor", conductor.toString());
		root.add("textures", textures);

		return root;
	}
}
