package simelectricity.essential.client.grid.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.client.grid.pole.PowerPole3Model;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;

@SideOnly(Side.CLIENT)
public class DistributionTransformerComponentModel extends CodeBasedModel {
	private final EnumDistributionTransformerBlockType blockType;
	private final boolean rotated;

	private final List<BakedQuad> quads = new ArrayList();
	
    @EasyTextureLoader.Mark("sime_essential:render/distribution/transformer_front_back")
    private final TextureAtlasSprite textureTransformerFrontBack = null;
    @EasyTextureLoader.Mark("sime_essential:render/distribution/transformer_side")
    private final TextureAtlasSprite textureTransformerSide = null;
	
    @EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
    @EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;
    @EasyTextureLoader.Mark(ResourcePaths.concrete)
    private final TextureAtlasSprite textureConcrete = null;
    @EasyTextureLoader.Mark(ResourcePaths.ceramic_insulator)
    private final TextureAtlasSprite textureCeramic = null;   
	
	public DistributionTransformerComponentModel(EnumDistributionTransformerBlockType blockType, boolean rotated) {
		this.blockType = blockType;
		this.rotated = rotated;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
    	if (side != null)
            return ImmutableList.of();
    	
		return quads;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return textureConcrete;
	}

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
		quads.clear();
		
		
		RawQuadGroup insulator = null;
		RawQuadGroup model = new RawQuadGroup();
		switch (blockType) {
		case Pole10kVAux:
			model.add((new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal)).translateCoord(0.2F, 0.8F, 0));
			model.add((new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal)).translateCoord(-0.2F, 0.8F, 0));
			model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
			break;
		case Pole10kVNormal:
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, -0.74F), new Vec3f(-0.4F, 0.4F, -0.74F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 1.45F, 0), new Vec3f(-0.4F, 1.4F, 0), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, 0.74F), new Vec3f(-0.4F, 0.4F, 0.74F), false, -0.1F, 0.03F));
			
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
			break;
		case Pole10kVSpec:
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, -0.74F), new Vec3f(-0.4F, 0.4F, -0.74F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 1.45F, 0), new Vec3f(-0.4F, 1.4F, 0), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, 0.74F), new Vec3f(-0.4F, 0.4F, 0.74F), false, -0.1F, 0.03F));
			
			model.add((new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal)).translateCoord(0.2F, -0.3F, 0));
			model.add((new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal)).translateCoord(-0.2F, -0.3F, 0));
			
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
			break;
		case Pole415VNormal:
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.2F, -0.9F), new Vec3f(-0.4F, 0.2F, -0.9F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.2F, -0.45F), new Vec3f(-0.4F, 0.2F, -0.45F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.2F, 0.45F), new Vec3f(-0.4F, 0.2F, 0.45F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.2F, 0.9F), new Vec3f(-0.4F, 0.2F, 0.9F), false, -0.1F, 0.03F));
			
			
            model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
            insulator = Models.render415VInsulator(textureMetal, textureInsulator);
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
            model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
			break;
		case Transformer:
			//Body
			model.add((new RawQuadCube(1F, 1F, 1F, new TextureAtlasSprite[] {
					textureTransformerFrontBack, textureTransformerFrontBack, textureTransformerSide, textureTransformerSide, textureTransformerFrontBack, textureTransformerFrontBack
					})));
			
			//Insulators
			model.merge(Models.render415VInsulatorTall(textureMetal, textureCeramic).translateCoord(0.25F, 1, 0.25F));
			model.merge(Models.render415VInsulatorTall(textureMetal, textureCeramic).translateCoord(0.25F, 1, -0.25F));
			
			model.merge(Models.render10kVInsulatorTall(textureMetal, textureCeramic).translateCoord(-0.25F, 1, 0));
			break;
		default:
			break;
		
		}
		
		if (!rotated)
			model.rotateAroundY(270);
        model.translateCoord(0.5F, 0, 0.5F);
        model.bake(this.quads);
	}

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return PowerPole3Model.itemCameraTransforms;
    }
}
