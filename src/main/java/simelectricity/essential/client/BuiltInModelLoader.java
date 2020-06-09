package simelectricity.essential.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.model.loader.ModelGeometryWrapper;
import simelectricity.essential.Essential;
import simelectricity.essential.client.grid.pole.CableJointModel;
import simelectricity.essential.client.grid.pole.ConcretePole35kVModel;

public class BuiltInModelLoader implements IModelLoader<ModelGeometryWrapper> {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "builtin");
	public final static IModelLoader<?> instance = new BuiltInModelLoader();
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		
	}

	@Override
	public ModelGeometryWrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {	
		final String type = JSONUtils.getString(modelContents, "type");

		if (type.equals("cable_joint_10kv")) {
			final boolean offAxis = JSONUtils.getBoolean(modelContents, "offaxis");
			return new ModelGeometryWrapper(null, CableJointModel.class, (context)->{
				DirHorizontal8 facing = context.getFacing8(offAxis);
				return new CableJointModel.Type10kV(facing);
			});
		}

		else if (type.equals("cable_joint_415v")) {
			final boolean offAxis = JSONUtils.getBoolean(modelContents, "offaxis");
			return new ModelGeometryWrapper(null, CableJointModel.class, (context)->{
				DirHorizontal8 facing = context.getFacing8(offAxis);
				return new CableJointModel.Type415V(facing);
			});
		}
		
		else if (type.equals("concrete_pole_35kv")) {
			final boolean isRod = JSONUtils.getBoolean(modelContents, "isrod");
			final boolean terminals = JSONUtils.getBoolean(modelContents, "terminals");
			return new ModelGeometryWrapper(null, ConcretePole35kVModel.class, (context)->{
				Direction facing = context.getFacing();
				return new ConcretePole35kVModel(facing, terminals, isRod);
			});	
		}

		
		throw new RuntimeException("\"" + type + "\" is not implemented by " + id.toString());
	}
}
