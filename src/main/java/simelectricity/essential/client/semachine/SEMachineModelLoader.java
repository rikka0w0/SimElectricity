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
			JsonObject cleanedContents = modelContents.deepCopy();
			cleanedContents.remove("loader");
			cleanedContents.remove("loader2");
			BlockModel vanillaBlockModel = deserializationContext.deserialize(cleanedContents, BlockModel.class);
			return new VanillaWrapper(vanillaBlockModel);
		}
	}

	public static interface Wrapper extends IUnbakedGeometry<Wrapper> {

	}

	public static class VanillaWrapper implements Wrapper {
		public final BlockModel vanillaBlockModel;

		public VanillaWrapper(BlockModel vanillaBlockModel) {
			this.vanillaBlockModel = vanillaBlockModel;
		}

		@Override
		public BakedModel bake(IGeometryBakingContext owner,
				ModelBaker bakery,
				Function<Material, TextureAtlasSprite> spriteGetter,
				ModelState modelTransform,
				ItemOverrides overrides) {

			vanillaBlockModel.resolveParents(loc -> {
				net.minecraft.client.resources.model.UnbakedModel model = bakery.getModel(loc);

				if (model != null) {
					// 使用广度优先搜索 (BFS) 暴力破解，在 NeoForge 的虚拟 BlockModel 外壳中挖出真正的 VanillaWrapper
					java.util.Queue<Object> queue = new java.util.LinkedList<>();
					java.util.Set<Object> visited = new java.util.HashSet<>();
					queue.add(model);
					visited.add(model);

					while (!queue.isEmpty()) {
						Object current = queue.poll();
						if (current instanceof VanillaWrapper) {
							// 成功找到脱壳的本体！直接返回内部纯净的原生 BlockModel
							return ((VanillaWrapper) current).vanillaBlockModel;
						}

						try {
							Class<?> clazz = current.getClass();
							// 向上遍历父类的变量
							while (clazz != null && clazz != Object.class) {
								for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
									// 跳过静态变量和基本数据类型（如 int, float 等）
									if (java.lang.reflect.Modifier.isStatic(f.getModifiers())
											|| f.getType().isPrimitive())
										continue;

									f.setAccessible(true);
									Object val = f.get(current);
									if (val != null && !visited.contains(val)) {
										String name = val.getClass().getName();
										// 限制搜索范围在游戏引擎和常用集合内，防止陷入底层的死循环
										if (name.startsWith("net.minecraft") || name.startsWith("net.neoforged")
												|| name.startsWith("simelectricity") || name.startsWith("rikka")
												|| name.startsWith("java.util") || name.startsWith("com.google")) {
											visited.add(val);
											queue.add(val);
										}
									}
								}
								clazz = clazz.getSuperclass();
							}
						} catch (Exception e) {
						}
					}
				}
				// 兜底返回（如果没有找到，或者本来就是普通的方块模型）
				return model;
			});

			BakedModel vanillaModel = vanillaBlockModel.bake(bakery, vanillaBlockModel, spriteGetter, modelTransform,
					true);
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
			BakedModel bakedModel = modelGeometry.bake(owner, bakery, spriteGetter, modelTransform, overrides);
			return new SEMachineModel(bakedModel);
		}
	}
}
