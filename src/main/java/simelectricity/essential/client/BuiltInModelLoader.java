package simelectricity.essential.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.model.loader.ModelGeometryWrapper;
import simelectricity.essential.Essential;
import simelectricity.essential.client.grid.pole.CableJointModel;

public class BuiltInModelLoader implements IModelLoader<ModelGeometryWrapper> {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "builtin");
	public final static IModelLoader<?> instance = new BuiltInModelLoader();
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		
	}

	@Override
	public ModelGeometryWrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {	
		final String type = JSONUtils.getString(modelContents, "type");
		final boolean offAxis = JSONUtils.getBoolean(modelContents, "offaxis");

		return new ModelGeometryWrapper(null, CableJointModel.class, (context)->{
			DirHorizontal8 facing = context.getFacing8(offAxis);

			if (type.equals("cable_joint_10kv"))
				return new CableJointModel.Type10kV(facing);
			if (type.equals("cable_joint_415v"))
				return new CableJointModel.Type415V(facing);
			
			throw new RuntimeException("\"" + type + "\" is not implemented by " + id.toString());
		});
	}
}
