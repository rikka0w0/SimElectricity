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
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
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
    
    @EasyTextureLoader.Mark("sime_essential:render/transmission/metal")
    private final TextureAtlasSprite textureMetal = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/glass_insulator")
    private final TextureAtlasSprite textureInsulator = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/concrete")
    private final TextureAtlasSprite textureConcrete = null;
    
	public DistributionTransformerFormedModel(EnumDistributionTransformerRenderPart part, int facing, boolean mirrored) {
		this.part = part;
		this.rotation = rotationMatrix[facing] * 45 - 90;
        this.mirrored = mirrored;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		LinkedList<BakedQuad> list = new LinkedList(quads);
		

		if (state != null) {
			if (state.getValue(EnumDistributionTransformerRenderPart.property) == EnumDistributionTransformerRenderPart.TransformerLeft) {
				RawQuadGroup model = new RawQuadGroup();

				//Insulators
				model.merge(Models.render415VInsulatorTall(textureMetal, textureConcrete).translateCoord(0.25F, 1, 0));
				model.merge(Models.render415VInsulatorTall(textureMetal, textureConcrete).translateCoord(0.25F, 1, 0.333F));
				model.merge(Models.render415VInsulatorTall(textureMetal, textureConcrete).translateCoord(0.25F, 1, 0.666F));
				model.merge(Models.render415VInsulatorTall(textureMetal, textureConcrete).translateCoord(0.25F, 1, 1F));
				
				model.merge(Models.render10kVInsulatorTall(textureMetal, textureConcrete).translateCoord(-0.25F, 1, 0));
				model.merge(Models.render10kVInsulatorTall(textureMetal, textureConcrete).translateCoord(-0.25F, 1, 0.5F));
				model.merge(Models.render10kVInsulatorTall(textureMetal, textureConcrete).translateCoord(-0.25F, 1, 1F));
				
		        model.rotateAroundY(rotation);
		        model.translateCoord(0.5F, 0, 0.5F);
		        model.bake(list);
			}
		}

		return list;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return textureConcrete;
	}

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> registry) {		
		quads.clear();
		
		RawQuadGroup insulator = null;
		RawQuadGroup model = new RawQuadGroup();
		switch (part) {
		case AuxLeft:
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, 0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.425F,0.85F, 0.15F), new Vec3f(-0.425F,0.85F,2.85F), false, 0.1F, 0.05F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, 0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.775F,0.85F, 0.15F), new Vec3f(-0.775F,0.85F,2.85F), false, 0.1F, 0.05F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, 0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.125F,0.85F, 0.15F), new Vec3f(-1.125F,0.85F,2.85F), false, 0.1F, 0.05F));
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, 0.5F, 0.15F));
            
            model.add((new RawQuadCube(0.05F, 0.1F, 1.625F, textureMetal)).translateCoord(0.15F, 0.75F, 0.6875F));

			insulator = Models.render415VInsulator(textureMetal, textureInsulator);
			insulator.rotateAroundZ(-90);
			model.merge(insulator.clone().translateCoord(0.15F, 0.76F, 1.175F));
			model.merge(insulator.clone().translateCoord(0.15F, 0.76F, 0.675F));
            
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
		case AuxMiddle:
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, -0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.425F,0.85F, -0.15F), new Vec3f(-0.425F,0.85F, 1.85F), false, 0.1F, 0.05F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, -0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.775F,0.85F, -0.15F), new Vec3f(-0.775F,0.85F, 1.85F), false, 0.1F, 0.05F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, -0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.125F,0.85F, -0.15F), new Vec3f(-1.125F,0.85F, 1.85F), false, 0.1F, 0.05F));
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
            
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.74F, 0.55F, 0), new Vec3f(-0.74F, 0.55F, 5F), false, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0 , 1.5F, 0), new Vec3f(0F, 1.5F, 5F), false, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.74F, 0.55F, 0), new Vec3f(0.74F, 0.55F, 5F), false, 0.2F, 0.05F));
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
            
            //Cable
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.74F, 0.55F, 0), new Vec3f(-0.74F, 0.55F, -5F), false, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0 , 1.5F, 0), new Vec3f(0F, 1.5F, -5F), false, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.74F, 0.55F, 0), new Vec3f(0.74F, 0.55F, -5F), false, 0.2F, 0.05F));
			break;
			
		case Pole415VLeft:
            insulator = Models.render415VInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
            model.rotateAroundY(90);
            
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.9F, 0.3F, 0), new Vec3f(-0.9F, 0.3F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.45F, 0.3F, 0), new Vec3f(-0.45F, 0.3F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.45F, 0.3F, 0), new Vec3f(0.45F, 0.3F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.9F, 0.3F, 0), new Vec3f(0.9F, 0.3F, 5F), true, 0.2F, 0.05F));
			break;
		case Pole415VRight:
            insulator = Models.render415VInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
            model.rotateAroundY(90);
            
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.9F, 0.3F, 0), new Vec3f(-0.9F, 0.3F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.45F, 0.3F, 0), new Vec3f(-0.45F, 0.3F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.45F, 0.3F, 0), new Vec3f(0.45F, 0.3F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.9F, 0.3F, 0), new Vec3f(0.9F, 0.3F, -5F), true, 0.2F, 0.05F));
			break;
			
		case TransformerLeft:
			model.add((new RawQuadCube(0.05F, 0.1F, 1.5F, textureMetal)).translateCoord(0.15F, 0, -0.25F));
			model.add((new RawQuadCube(0.05F, 0.1F, 1.5F, textureMetal)).translateCoord(-0.15F, 0, -0.25F));
			
			
			//Body
			model.add((new RawQuadCube(0.8F, 0.9F, 1.8F, textureMetal)).translateCoord(0, 0.1F, 0.5F));
			//Heatsinker(Front)
			model.add((new RawQuadCube(0.2F, 0.7F, 1.4F, textureMetal)).translateCoord(-0.5F, 0.15F, 0.5F));
			//Heatsinker(Back)
			model.add((new RawQuadCube(0.2F, 0.7F, 1.4F, textureMetal)).translateCoord(0.5F, 0.15F, 0.5F));
			break;
		case TransformerRight:
			model.add((new RawQuadCube(0.05F, 0.1F, 1.5F, textureMetal)).translateCoord(0.15F, 0, 0.25F));
			model.add((new RawQuadCube(0.05F, 0.1F, 1.5F, textureMetal)).translateCoord(-0.15F, 0, 0.25F));
			break;
			
		default:
			break;
		}
		
        model.rotateAroundY(rotation);
        model.translateCoord(0.5F, 0, 0.5F);
        model.bake(this.quads);
	}
}
