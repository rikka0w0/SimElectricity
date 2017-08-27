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
import simelectricity.essential.grid.EnumBlockTypePole3;

@SideOnly(Side.CLIENT)
public class PowerPole3RawModel extends TextureLoaderModel {
    private final ResourceLocation textureMetal, textureInsulator, textureConcrete;
    private final EnumBlockTypePole3 blockType;
    private final int facing;

    public PowerPole3RawModel(EnumBlockTypePole3 blockType, int facing) {
        this.blockType = blockType;
        this.facing = facing;

        this.textureMetal = this.registerTexture("sime_essential:render/transmission/metal");
        this.textureInsulator = this.registerTexture("sime_essential:render/transmission/glass_insulator");
        this.textureConcrete = this.registerTexture("sime_essential:render/transmission/metal");
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new PowerPole3Model(this.blockType, this.facing * 45 - 90, //Convert facing to rotation
                bakedTextureGetter.apply(this.textureMetal),
                bakedTextureGetter.apply(this.textureInsulator),
                bakedTextureGetter.apply(this.textureConcrete));
    }

}
