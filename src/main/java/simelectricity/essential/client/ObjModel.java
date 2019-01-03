package simelectricity.essential.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;
import rikka.librikka.model.ModelPerspectives;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;

public class ObjModel implements IModel, IBakedModel {
    private static final ModelRotation[] rotationMatrix = {
            ModelRotation.X90_Y0,
            ModelRotation.X270_Y0,
            ModelRotation.X0_Y0,
            ModelRotation.X0_Y180,
            ModelRotation.X0_Y270,
            ModelRotation.X0_Y90
    };

    private final EnumFacing facing;
    private final List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
    private final IModel model;
    private final IModelState defaultState;
    private final List<BakedQuad> quads = new ArrayList();

    public ObjModel(String domain, String modelName, EnumFacing facing, boolean is2State) throws Exception {
        //Sketch Up --*.dae--> Blender --> *.obj & *.mtl
        modelName = domain + ":" + modelName + (is2State ? "_2.obj" : ".obj");
        this.facing = facing;

        ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();

        Variant variant = new Variant(new ResourceLocation(modelName), ModelRotation.X0_Y0, false, 1);

        ResourceLocation loc = variant.getModelLocation();
        if (!dependencies.contains(loc))
            dependencies.add(loc);

        IModel preModel = ModelLoaderRegistry.getModel(loc);
        IModel model = variant.process(preModel);

        for (ResourceLocation location : model.getDependencies()) {
            ModelLoaderRegistry.getModelOrMissing(location);
        }

        textures.addAll(model.getTextures()); // Kick this, just in case.
        this.model = model;
        builder.add(Pair.of(model, variant.getState()));

        defaultState = new MultiModelState(builder.build());
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.copyOf(this.dependencies);
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
        ModelRotation rotationState = rotationMatrix[facing.ordinal()];

        Matrix4f offsetMatrix1 = new Matrix4f();
        offsetMatrix1.setIdentity();
        //offsetMatrix1.m03 = -0.5F;  // 1 -> 0.5
        //offsetMatrix1.m23 = -0.5F;  // 1 -> 0.5
        Matrix4f ret = new Matrix4f();
        ret.mul(rotationState.getMatrix(), offsetMatrix1);

        IModelState transformation = new TRSRTransformation(ret);
        //Bake the Obj Model
        IBakedModel bakedModel = this.model.bake(transformation, format, bakedTextureGetter);

        this.quads.addAll(bakedModel.getQuads(null, null, 0));
        this.particle = bakedModel.getParticleTexture();

        return this;
    }

    /////////////////
    /// IBakedModel
    /////////////////
    private TextureAtlasSprite particle;
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null)
            return ImmutableList.of();

        return this.quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return itemCameraTransforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return particle;
    }

    public final static ItemCameraTransforms itemCameraTransforms = ModelPerspectives.create(ModelPerspectives.ItemBlock,
            null, null,
            null, null,
            null,
            new ItemTransformVec3f(new Vector3f(30, 225, 0), new Vector3f(0.025F, 0, 0), new Vector3f(0.75F, 0.75F, 0.75F))	//gui
            , null, null);

}
