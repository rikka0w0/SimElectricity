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
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerRenderPart;

@SideOnly(Side.CLIENT)
public class DistributionTransformerFormedModel extends CodeBasedModel {
    public static final int[] rotationMatrix = {4, 0, 6, 2};    //NSWE
	
	private final LinkedList<BakedQuad> quads = new LinkedList();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
	
    private final EnumDistributionTransformerRenderPart part;
    private final int rotation;
    private final boolean mirrored;
    private final ResourceLocation textureMetalLoc, textureInsulatorLoc, textureConcreteLoc;
    private TextureAtlasSprite textureMetal, textureInsulator, textureConcrete;
	
	public DistributionTransformerFormedModel(EnumDistributionTransformerRenderPart part, int facing, boolean mirrored) {
		this.part = part;
		this.rotation = rotationMatrix[facing] * 45 - 90;
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
		
		RawQuadGroup insulator = null;
		RawQuadGroup model = new RawQuadGroup();
		switch (part) {
		case AuxLeft:
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, 0.15F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, 0.15F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, 0.15F));
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, 0.5F, 0.15F));
            
            model.add((new RawQuadCube(0.05F, 0.1F, 1.625F, textureMetal)).translateCoord(0.15F, 0.75F, 0.6875F));

			insulator = Models.render415VInsulator(textureMetal, textureInsulator);
			insulator.rotateAroundZ(-90);
			model.merge(insulator.clone().translateCoord(0.15F, 0.76F, 1.175F));
			model.merge(insulator.clone().translateCoord(0.15F, 0.76F, 0.675F));
            
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
            //model.merge(TransmissionLineGLRender.renderParabolicCable(new Vec3f(0,0,0), new Vec3f(5,5,5), false, 1, 0.05F, textureConcrete));
			break;
		case AuxMiddle:
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, -0.15F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, -0.15F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, -0.15F));
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, 0.5F, -0.15F));
            
            model.add((new RawQuadCube(0.05F, 0.1F, 1.625F, textureMetal)).translateCoord(0.15F, 0.75F, -0.6875F));
            
			insulator = Models.render415VInsulator(textureMetal, textureInsulator);
			insulator.rotateAroundZ(-90);
			model.merge(insulator.clone().translateCoord(0.15F, 0.76F, -1.175F));
			model.merge(insulator.clone().translateCoord(0.15F, 0.76F, -0.675F));
            
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
		case AuxRight:
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, -0.15F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, -0.15F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, -0.15F));
			
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, 0.5F, 0.15F));
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, 0.5F, -0.15F));
            model.add((new RawQuadCube(0.05F, 0.1F, 1.4F, textureMetal)).translateCoord(-1.4F, 0.5F, 0));

			insulator.rotateAroundZ(90);
			model.merge(insulator.clone().translateCoord(-1.4F, 0.55F, 0.65F));
			model.merge(insulator.clone().translateCoord(-1.4F, 0.55F, 0));
			model.merge(insulator.clone().translateCoord(-1.4F, 0.55F, -0.65F));
			
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
			
		case Pole10kVLeft:
			insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
            model.rotateAroundY(90);
			break;
			
		case Pole10kVRight:		
            insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
            model.rotateAroundY(90);
            
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			insulator.rotateAroundZ(90);
			model.merge(insulator.clone().translateCoord(-1.4F, -0.2F, 0.65F));
			model.merge(insulator.clone().translateCoord(-1.4F, -0.2F, 0));
			model.merge(insulator.clone().translateCoord(-1.4F, -0.2F, -0.65F));
            
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, -0.25F, 0.15F));
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, -0.25F, -0.15F));
            model.add((new RawQuadCube(0.05F, 0.1F, 1.4F, textureMetal)).translateCoord(-1.4F, -0.25F, 0));
			break;
			
		case Pole415VLeft:
		case Pole415VRight:
            insulator = Models.render415VInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
            model.rotateAroundY(90);
			break;
			
		case TransformerLeft:
			break;
		case TransformerRight:
			break;
			
		default:
			break;
		}
		
        model.rotateAroundY(rotation);
        model.translateCoord(0.5F, 0, 0.5F);
        model.bake(this.quads);
	}
}
