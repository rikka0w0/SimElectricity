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
public class CableJointRawModel extends TextureLoaderModel {
    private final ResourceLocation textures[] = new ResourceLocation[3];
    private final int facing;

    public CableJointRawModel(int facing) {
        textures[0] = this.registerTexture("sime_essential:render/transmission/essential_cable_joint_texture_updown");
        textures[1] = this.registerTexture("sime_essential:render/transmission/essential_cable_joint_texture_metal");
        textures[2] = this.registerTexture("sime_essential:render/transmission/essential_cable_joint_texture_side");

        this.facing = facing;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new CableJointModel(this.facing, new TextureAtlasSprite[]{
                bakedTextureGetter.apply(this.textures[0]),
                bakedTextureGetter.apply(this.textures[1]),
                bakedTextureGetter.apply(this.textures[2])
        });
    }

}
