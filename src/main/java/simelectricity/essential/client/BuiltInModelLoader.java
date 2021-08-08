package simelectricity.essential.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.model.GeneratedModelLoader;
import rikka.librikka.model.loader.ModelGeometryWrapper;
import simelectricity.essential.Essential;
import simelectricity.essential.client.grid.GridRenderMonitor;
import simelectricity.essential.client.grid.pole.CableJointModel;
import simelectricity.essential.client.grid.pole.ConcretePole35kVModel;
import simelectricity.essential.client.grid.pole.ConcretePoleModel;
import simelectricity.essential.client.grid.pole.MetalPole35kVModel;
import simelectricity.essential.client.grid.transformer.DistributionTransformerComponentModel;
import simelectricity.essential.client.grid.transformer.DistributionTransformerFormedModel;
import simelectricity.essential.grid.BlockPoleConcrete;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;

public class BuiltInModelLoader implements IModelLoader<ModelGeometryWrapper> {
	public final static ResourceLocation id = new ResourceLocation(Essential.MODID, "builtin");
	public final static IModelLoader<?> instance = new BuiltInModelLoader();
	public final static String dir = "block/builtin/";
	
	public static JsonObject serialize(String type) {
    	JsonObject json = new JsonObject();
    	GeneratedModelLoader.commentDoNotModify(json);
		json.addProperty("loader", id.toString());
		json.addProperty("type", type);
		return json;
	}

	@Override
	public ModelGeometryWrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {	
		final String type = GsonHelper.getAsString(modelContents, "type");

		if (type.equals("cable_joint_10kv")) {
			final boolean offAxis = GsonHelper.getAsBoolean(modelContents, "offaxis");
			return new ModelGeometryWrapper(null, CableJointModel.class, (context)->{
				DirHorizontal8 facing = context.getFacing8(offAxis);
				return new CableJointModel.Type10kV(facing);
			});
		}

		else if (type.equals("cable_joint_415v")) {
			final boolean offAxis = GsonHelper.getAsBoolean(modelContents, "offaxis");
			return new ModelGeometryWrapper(null, CableJointModel.class, (context)->{
				DirHorizontal8 facing = context.getFacing8(offAxis);
				return new CableJointModel.Type415V(facing);
			});
		}

		else if (type.equals("concrete_pole_35kv")) {
			final boolean isRod = GsonHelper.getAsBoolean(modelContents, "isrod");
			final boolean terminals = GsonHelper.getAsBoolean(modelContents, "terminals");
			return new ModelGeometryWrapper(null, ConcretePole35kVModel.class, (context)->{
				Direction facing = context.getFacing();
				return new ConcretePole35kVModel(facing, terminals, isRod);
			});	
		}

		else if (type.equals("metal_pole_35kv")) {
			final boolean terminals = GsonHelper.getAsBoolean(modelContents, "terminals");
			return new ModelGeometryWrapper(null, MetalPole35kVModel.class, (context)->{
				return new MetalPole35kVModel(terminals);
			});	
		}

		else if (type.equals("concrete_pole")) {
			final boolean offAxis = GsonHelper.getAsBoolean(modelContents, "offaxis");
			final BlockPoleConcrete.Type blockType = BlockPoleConcrete.Type.forName(
					GsonHelper.getAsString(modelContents, "part"));
			return new ModelGeometryWrapper(null, ConcretePoleModel.class, (context)->{
				DirHorizontal8 facing = context.getFacing8(offAxis);
				return new ConcretePoleModel(blockType, facing);
			});	
		}

		else if (type.equals("distribution_transformer")) {
			final EnumDistributionTransformerBlockType blockType = 
					EnumDistributionTransformerBlockType.forName(
						GsonHelper.getAsString(modelContents, "part"));
			
			if (blockType == EnumDistributionTransformerBlockType.PlaceHolder) {
				return new ModelGeometryWrapper(null, DistributionTransformerFormedModel.class, (context)->{
					return DistributionTransformerFormedModel.instance;
				});
			} else if (blockType.formed) {
				return new ModelGeometryWrapper(null, null, (context)->{
					return DistributionTransformerFormedModel.instanceNoBaking;
				});
			} else {
				return new ModelGeometryWrapper(null, DistributionTransformerComponentModel.class, (context)->{
					Direction facing = context.getFacing();
					return new DistributionTransformerComponentModel(blockType, facing);
				});	
			}
		}


		throw new RuntimeException("\"" + type + "\" is not implemented by " + id.toString());
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		GridRenderMonitor.instance.markLoadedPowerPoleForRenderingUpdate();
	}
}
