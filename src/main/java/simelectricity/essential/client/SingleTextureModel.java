package simelectricity.essential.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class SingleTextureModel implements IModel {
    private final List<ResourceLocation> locations = new ArrayList<ResourceLocation>();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
    private final IModel model;
    private final IModelState defaultState;

    public SingleTextureModel(String domain, String texture, boolean isBlock) throws Exception {
        String resPath = domain + ":" + (isBlock ? "blocks/" : "items/") + texture;

        Variant variant = new SimpleTextureVariant(resPath, isBlock);
        ResourceLocation loc = variant.getModelLocation();
        this.locations.add(loc);

        IModel preModel = ModelLoaderRegistry.getModel(loc);
        this.model = variant.process(preModel);
        for (ResourceLocation location : this.model.getDependencies()) {
            ModelLoaderRegistry.getModelOrMissing(location);
        }

        this.textures.addAll(this.model.getTextures()); // Kick this, just in case.

        Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
        builder.add(Pair.of(this.model, variant.getState()));
        this.defaultState = new MultiModelState(builder.build());
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.copyOf(this.locations);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.copyOf(this.textures);
    }

    @Override
    public IModelState getDefaultState() {
        return this.defaultState;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IBakedModel bakedModel = this.model.bake(MultiModelState.getPartState(state, this.model, 0), format, bakedTextureGetter);
        return bakedModel;
    }
}
