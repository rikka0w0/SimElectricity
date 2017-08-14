package simelectricity.essential.client;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Load nothing from JSON model files, only tell MineCraft to load custom textures, for dynamic quad generating
 *
 */
public abstract class TextureLoaderModel implements IModel {
	protected final Set<ResourceLocation> textures = Sets.newHashSet();
		
	@Override
	public final Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of();
	}

	@Override
	public final Collection<ResourceLocation> getTextures() {
		return ImmutableSet.copyOf(textures);
	}

	@Override
	public final IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
}
