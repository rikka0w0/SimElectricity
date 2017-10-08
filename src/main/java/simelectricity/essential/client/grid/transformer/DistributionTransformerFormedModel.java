package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.codebased.CodeBasedModel;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;

@SideOnly(Side.CLIENT)
public class DistributionTransformerFormedModel extends CodeBasedModel {
	private final LinkedList<BakedQuad> quads = new LinkedList();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
	
    private final EnumDistributionTransformerBlockType blockType;
    private final int rotation;
    private final boolean mirrored;
    private final ResourceLocation textureMetalLoc, textureInsulatorLoc, textureConcreteLoc;
    private TextureAtlasSprite textureMetal, textureInsulator, textureConcrete;
	
	public DistributionTransformerFormedModel(EnumDistributionTransformerBlockType blockType, int facing, boolean mirrored) {
		this.blockType = blockType;
		this.rotation = facing * 90 - 90;
        this.mirrored = mirrored;
		
        //Custom texture
        textureMetalLoc = this.registerTexture("sime_essential:render/transmission/metal");
        textureInsulatorLoc = this.registerTexture("sime_essential:render/transmission/glass_insulator");
        textureConcreteLoc = this.registerTexture("sime_essential:render/transmission/concrete");
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return quads;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return textureConcrete;
	}

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> registry) {
		this.textureMetal = registry.apply(textureMetalLoc);
		this.textureInsulator = registry.apply(textureInsulatorLoc);
		this.textureConcrete = registry.apply(textureConcreteLoc);
		
		quads.clear();
		
		RawQuadGroup model = new RawQuadGroup();
		switch (blockType) {
		case PlaceHolder:
			break;
		case Pole10kV:
			break;
		case Pole10kVAux:
			break;
		case Pole10kVNormal:
			break;
		case Pole10kVSpec:
			break;
		case Pole415V:
			model.add(new RawQuadCube(0.15F, 0.08F, 1.6F, textureMetal).translateCoord(0, 0.05F, 0));
			break;
		case Pole415VNormal:
			break;
		case Primary10kV:
			break;
		case Secondary415V:
			break;
		case Transformer:
			break;
		default:
			break;
		}
		
        model.rotateAroundY(rotation);
        model.translateCoord(0.5F, 0, 0.5F);
        model.bake(this.quads);
	}
}
