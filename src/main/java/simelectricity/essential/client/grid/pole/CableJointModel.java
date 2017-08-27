package simelectricity.essential.client.grid.pole;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.BlockRenderModel;

import java.util.LinkedList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class CableJointModel extends BlockRenderModel {
    private final TextureAtlasSprite[] textures;
    private final LinkedList<BakedQuad> quads;

    public CableJointModel(int facing, TextureAtlasSprite[] textures) {
        this.textures = textures;
        quads = new LinkedList();
        Models.renderCableJoint(textures).rotateAroundY(facing * 45 - 90).transform(0.5, 0, 0.5).bake(this.quads);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.SOLID) {
            return ImmutableList.copyOf(this.quads);
        }
        return null;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.textures[2];
    }
}
