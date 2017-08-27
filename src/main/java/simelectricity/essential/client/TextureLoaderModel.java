package simelectricity.essential.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Set;

/**
 * Load nothing from JSON model files, only tell MineCraft to load custom textures, for dynamic quad generating
 */
@SideOnly(Side.CLIENT)
public abstract class TextureLoaderModel implements IModel {
    private final Set<ResourceLocation> textures = Sets.newHashSet();

    protected ResourceLocation registerTexture(String texture) {
        ResourceLocation resLoc = new ResourceLocation(texture);
        this.textures.add(resLoc);
        return resLoc;
    }

    @Override
    public final Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public final Collection<ResourceLocation> getTextures() {
        return ImmutableSet.copyOf(this.textures);
    }

    @Override
    public final IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
