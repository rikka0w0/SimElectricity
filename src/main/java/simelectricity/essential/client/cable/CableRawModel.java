package simelectricity.essential.client.cable;

import com.google.common.base.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import rikka.librikka.model.codebased.TextureLoaderModel;

public class CableRawModel extends TextureLoaderModel {
    private final ResourceLocation insulatorTexture, conductorTexture;
    private final float thickness;

    public CableRawModel(String domain, String name, float thickness) throws Exception {
        String insulatorTexture = domain + ":blocks/cable/" + name + "_insulator";
        String conductorTexture = domain + ":blocks/cable/" + name + "_conductor";

        this.insulatorTexture = this.registerTexture(insulatorTexture);    // We just want to bypass the ModelBakery
        this.conductorTexture = this.registerTexture(conductorTexture);    // and load our texture
        this.thickness = thickness;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        TextureAtlasSprite insulatorTexture = bakedTextureGetter.apply(this.insulatorTexture);
        TextureAtlasSprite conductorTexture = bakedTextureGetter.apply(this.conductorTexture);
        return new CableModel(insulatorTexture, conductorTexture, this.thickness);
    }
}
