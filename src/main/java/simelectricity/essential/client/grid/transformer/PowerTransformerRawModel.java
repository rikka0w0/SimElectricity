package simelectricity.essential.client.grid.transformer;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.*;

@SideOnly(Side.CLIENT)
public class PowerTransformerRawModel implements IModel {
    private static final ModelRotation[] rotationMatrix = {
            ModelRotation.X0_Y270,
            ModelRotation.X0_Y90,
            ModelRotation.X0_Y180,
            ModelRotation.X0_Y0
    };
    private final List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
    private final IModel model;
    private final IModelState defaultState;
    private final int facing;
    private final boolean mirrored;
    private final ResourceLocation textureMetal, textureInsulator;

    public PowerTransformerRawModel(int facing, boolean mirrored) throws Exception {
        String modelName = "sime_essential:powertransformer.obj";    //Sketch Up --*.dae--> Blender --> *.obj & *.mtl
        Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
        LinkedList<Variant> variants = new LinkedList<Variant>();
        boolean uvLock = false;

        Variant variant = new Variant(new ResourceLocation(modelName), PowerTransformerRawModel.rotationMatrix[facing], uvLock, 1);

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

        this.facing = facing;
        this.mirrored = mirrored;

        //Custom texture
        textureMetal = new ResourceLocation("sime_essential:render/transmission/metal");
        textureInsulator = new ResourceLocation("sime_essential:render/transmission/glass_insulator");
        textures.add(textureMetal);
        textures.add(textureInsulator);
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
        //IModelState actualState = MultiModelState.getPartState(state, model, 0);
        //IBakedModel bakedModel = model.bake(rotationMatrix[facing], format, bakedTextureGetter);
        ModelRotation rotationState = PowerTransformerRawModel.rotationMatrix[this.facing];
        IModelState transformation;

        //Handle mirror
        if (this.mirrored) {
            Matrix4f offsetMatrix1 = new Matrix4f();
            offsetMatrix1.setIdentity();
            offsetMatrix1.m03 = 0.5F;
            offsetMatrix1.m23 = 0.5F;

            Matrix4f refXMatrix = new Matrix4f();
            refXMatrix.setIdentity();
            refXMatrix.m22 = -1F;

            Matrix4f offsetMatrix2 = new Matrix4f();
            offsetMatrix2.setIdentity();
            offsetMatrix2.m03 = -0.5F;
            offsetMatrix2.m23 = -0.5F;

            Matrix4f ret = new Matrix4f();
            ret.mul(rotationState.getMatrix(), offsetMatrix1);
            ret.mul(ret, refXMatrix);
            ret.mul(ret, offsetMatrix2);

            transformation = new TRSRTransformation(ret);
        } else {
            transformation = rotationState;
        }

        IBakedModel bakedModel = this.model.bake(transformation, format, bakedTextureGetter);

        return new PowerTransformerModel(this.facing, this.mirrored, bakedModel,
                bakedTextureGetter.apply(this.textureMetal),
                bakedTextureGetter.apply(this.textureInsulator));
    }
}
