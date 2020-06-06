package simelectricity.essential.client.cable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import simelectricity.essential.Essential;

public class CableModelLoader implements IModelLoader {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "cable");
	public final static CableModelLoader instance = new CableModelLoader();
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		
	}

	@Override
	public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		String type = JSONUtils.getString(modelContents, "type");
		ResourceLocation insulator = new ResourceLocation(JSONUtils.getString(modelContents, "side"));
		ResourceLocation conductor = new ResourceLocation(JSONUtils.getString(modelContents, "core"));
		float thickness = JSONUtils.getFloat(modelContents, "thickness");

		if (type.toLowerCase().equals("cable"))
			return new CableModel(insulator, conductor, thickness);
		else if (type.toLowerCase().equals("wire"))
			return new WireModel(insulator, conductor, thickness);
		
		throw new RuntimeException("\"" + type + "\" is not implemented by " + id.toString());
	}
	
	public JsonObject serialize(String type, ResourceLocation insulator, ResourceLocation conductor, float thickness) {
		JsonObject root = new JsonObject();
		
		root.addProperty("loader", id.toString());
		root.addProperty("type", type);
		root.addProperty("side", insulator.toString());
		root.addProperty("core", conductor.toString());
		root.addProperty("thickness", thickness);
		
		return root;
	}
}
