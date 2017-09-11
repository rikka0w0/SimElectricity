package simelectricity.essential.client.grid.pole;

import com.google.common.base.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.codebased.TextureLoaderModel;

@SideOnly(Side.CLIENT)
public class PowerPole2RawModel extends TextureLoaderModel {
    private final ResourceLocation textureMetal, textureInsulator, textureConcrete;
    private final int facing;
    private final int type;
    private final boolean isRod;

    public PowerPole2RawModel(int facing, int type, boolean isRod) {
        this.textureMetal = this.registerTexture("sime_essential:render/transmission/metal");
        this.textureInsulator = this.registerTexture("sime_essential:render/transmission/glass_insulator");
        this.textureConcrete = this.registerTexture("sime_essential:render/transmission/concrete");
        this.facing = facing;
        this.type = type;
        this.isRod = isRod;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new PowerPole2Model(this.facing, this.type, this.isRod,
        		bakedTextureGetter.apply(this.textureMetal),
        		bakedTextureGetter.apply(this.textureInsulator),
        		bakedTextureGetter.apply(this.textureConcrete));
    }
}
