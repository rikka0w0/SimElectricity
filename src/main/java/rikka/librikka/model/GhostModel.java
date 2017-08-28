package rikka.librikka.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rikka.librikka.model.codebased.CodeBasedModel;

import java.util.List;

/**
 * An invisible model
 *
 * @author Rikka0_0
 */
public class GhostModel extends CodeBasedModel {
    private final ResourceLocation texture;
    private TextureAtlasSprite loadedTexture;

    public GhostModel() {
        texture = this.registerTexture("minecraft:blocks/iron_block");
    }
    
    public GhostModel(String particleTexture) {
        texture = this.registerTexture(particleTexture);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return ImmutableList.of();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.loadedTexture;
    }

    @Override
    protected void onTextureRegistered(Function<ResourceLocation, TextureAtlasSprite> registry) {
        loadedTexture = registry.apply(this.texture);
    }
}
