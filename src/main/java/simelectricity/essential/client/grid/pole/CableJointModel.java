package simelectricity.essential.client.grid.pole;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class CableJointModel extends CodeBasedModel {
	private final int facing;
    private final List<BakedQuad> quads = new ArrayList();

    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_updown")
    private final TextureAtlasSprite texture_updown = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_metal")
    private final TextureAtlasSprite texture_metal = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_side")
    private final TextureAtlasSprite texture_side = null;
    
    private TextureAtlasSprite[] textures = new TextureAtlasSprite[3];
    
    public CableJointModel(int facing) {
        this.facing = facing;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
    	if (side != null)
            return ImmutableList.of();
    	
        return this.quads;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return texture_side;
    }

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
		textures[0] = texture_updown;
		textures[1] = texture_metal;
		textures[2] = texture_side;
		
		this.quads.clear();
		Models.renderCableJoint(textures).rotateAroundY(facing * 45 - 90).transform(0.5, 0, 0.5).bake(this.quads);
	}
}
