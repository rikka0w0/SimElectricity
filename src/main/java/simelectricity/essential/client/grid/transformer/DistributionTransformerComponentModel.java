package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.PowerCableBakery;
import simelectricity.essential.client.grid.pole.ConcretePoleModel;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class DistributionTransformerComponentModel extends CodeBasedModel {
	private final EnumDistributionTransformerBlockType blockType;
	private final Direction facing;

	private final List<BakedQuad> quads = new LinkedList<>();
	
	@EasyTextureLoader.Mark(ResourcePaths.hv_cable)
	private final TextureAtlasSprite textureCable = null;
	
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
	
	public DistributionTransformerComponentModel(EnumDistributionTransformerBlockType blockType, Direction facing) {
		this.blockType = blockType;
		this.facing = facing;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    	if (side != null)
            return emptyQuadList;
        
		return this.quads;
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
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.45F, -0.74F), new Vec3f(-0.4F, 0.4F, -0.74F), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 1.45F, 0), new Vec3f(-0.4F, 1.4F, 0), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.45F, 0.74F), new Vec3f(-0.4F, 0.4F, 0.74F), false, -0.1F, 0.03F, textureCable));
			
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
			break;
		case Pole10kVSpec:
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.45F, -0.74F), new Vec3f(-0.4F, 0.4F, -0.74F), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 1.45F, 0), new Vec3f(-0.4F, 1.4F, 0), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.45F, 0.74F), new Vec3f(-0.4F, 0.4F, 0.74F), false, -0.1F, 0.03F, textureCable));
			
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
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.2F, -0.9F), new Vec3f(-0.4F, 0.2F, -0.9F), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.2F, -0.45F), new Vec3f(-0.4F, 0.2F, -0.45F), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.2F, 0.45F), new Vec3f(-0.4F, 0.2F, 0.45F), false, -0.1F, 0.03F, textureCable));
			model.merge(PowerCableBakery.renderParabolicCable(new Vec3f(0.4F, 0.2F, 0.9F), new Vec3f(-0.4F, 0.2F, 0.9F), false, -0.1F, 0.03F, textureCable));
			
			
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
		
		if (this.facing != null)
			model.rotateAroundY(90-facing.getHorizontalAngle());
        model.translateCoord(0.5F, 0, 0.5F);
        model.bake(this.quads);
	}

	@Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ConcretePoleModel.itemCameraTransforms;
    }
}
