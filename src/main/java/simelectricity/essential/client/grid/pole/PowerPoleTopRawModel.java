package simelectricity.essential.client.grid.pole;

import com.google.common.base.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.TextureLoaderModel;

@SideOnly(Side.CLIENT)
public class PowerPoleTopRawModel extends TextureLoaderModel {
    private final ResourceLocation textureMetal, textureInsulator;
    private final int facing;
    private final int type;

    public PowerPoleTopRawModel(int facing, int type) {
        this.textureMetal = this.registerTexture("sime_essential:render/transmission/metal");
        this.textureInsulator = this.registerTexture("sime_essential:render/transmission/glass_insulator");
        this.facing = facing;
        this.type = type;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new PowerPoleTopModel(this.facing, this.type, bakedTextureGetter.apply(this.textureMetal), bakedTextureGetter.apply(this.textureInsulator));
    }
}
