package simelectricity.essential.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

/**
 * A block variant uses the same texture for all 6 sides / An item with single texture
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class SimpleTextureVariant extends Variant {
    private static final ResourceLocation generated = new ResourceLocation("minecraft:item/generated");
    private static final ResourceLocation cube_all = new ResourceLocation("minecraft:block/cube_all");

    private final ImmutableMap<String, String> textures;
    private final ImmutableMap<String, String> customData;
    private final IModelState state;
    private final boolean isGui3d;

    /**
     * @param texture the REAL texture path, e.g. sime:tool_multimeter
     * @param isBlock
     */
    public SimpleTextureVariant(String texture, boolean isBlock) {
        this(TRSRTransformation.identity(), texture, isBlock);
    }

    private SimpleTextureVariant(IModelState state, String texture, boolean isBlock) {
        super(isBlock ? SimpleTextureVariant.cube_all : SimpleTextureVariant.generated
                , state instanceof ModelRotation ? (ModelRotation) state : ModelRotation.X0_Y0
                , false, 1);    //uvLock = false, weight always 1


        Builder<String, String> builder = ImmutableMap.builder();
        builder.put(isBlock ? "all" : "layer0", texture);

        textures = builder.build();


        customData = ImmutableMap.copyOf(new HashMap<String, String>());
        this.state = state;
        isGui3d = isBlock;
    }

    @Override
    public IModelState getState() {
        return this.state;
    }

    private IModel runModelHooks(IModel base, boolean smooth, boolean gui3d, boolean uvlock, ImmutableMap<String, String> textureMap, ImmutableMap<String, String> customData) {
        base = ModelProcessingHelper.customData(base, customData);
        base = ModelProcessingHelper.retexture(base, textureMap);
        base = ModelProcessingHelper.smoothLighting(base, smooth);
        base = ModelProcessingHelper.gui3d(base, gui3d);
        base = ModelProcessingHelper.uvlock(base, uvlock);
        return base;
    }

    @Override
    public IModel process(IModel base) {
        //base must be cube_all
        //texture string,string {all=minecraft:blocks/diamond_block}
        //ImmutableMap<String, String> customData

        //						smooth=gui3d=true
        return this.runModelHooks(base, true, this.isGui3d, isUvLock(), this.textures, this.customData);
    }
}
