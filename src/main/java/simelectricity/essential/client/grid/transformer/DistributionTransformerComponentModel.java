package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;

@SideOnly(Side.CLIENT)
public class DistributionTransformerComponentModel extends CodeBasedModel implements IPerspectiveAwareModel {
	private final EnumDistributionTransformerBlockType blockType;
	private final boolean rotated;

	private final LinkedList<BakedQuad> quads = new LinkedList();
	
    @EasyTextureLoader.Mark("sime_essential:render/distribution/transformer_front_back")
    private final TextureAtlasSprite textureTransformerFrontBack = null;
    @EasyTextureLoader.Mark("sime_essential:render/distribution/transformer_side")
    private final TextureAtlasSprite textureTransformerSide = null;
	
    @EasyTextureLoader.Mark("sime_essential:render/transmission/metal")
    private final TextureAtlasSprite textureMetal = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/glass_insulator")
    private final TextureAtlasSprite textureInsulator = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/concrete")
    private final TextureAtlasSprite textureConcrete = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/concrete")
    private final TextureAtlasSprite textureCeramic = null;   
	
	public DistributionTransformerComponentModel(EnumDistributionTransformerBlockType blockType, boolean rotated) {
		this.blockType = blockType;
		this.rotated = rotated;
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
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, -0.74F), new Vec3f(-0.4F, 0.45F, -0.74F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 1.45F, 0), new Vec3f(-0.4F, 1.45F, 0), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, 0.74F), new Vec3f(-0.4F, 0.45F, 0.74F), false, -0.1F, 0.03F));
			
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
			break;
		case Pole10kVSpec:
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, -0.74F), new Vec3f(-0.4F, 0.45F, -0.74F), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 1.45F, 0), new Vec3f(-0.4F, 1.45F, 0), false, -0.1F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.4F, 0.45F, 0.74F), new Vec3f(-0.4F, 0.45F, 0.74F), false, -0.1F, 0.03F));
			
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
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        ItemTransformVec3f half = new ItemTransformVec3f(new Vector3f(15, 255, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F));

        ItemCameraTransforms itemCameraTransforms = new ItemCameraTransforms(
                half,
                half,
                new ItemTransformVec3f(new Vector3f(-15, 255, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)),
                new ItemTransformVec3f(new Vector3f(-15, 255, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)),
                half,
                new ItemTransformVec3f(new Vector3f(45, 255 - 45, 0), new Vector3f(), new Vector3f(0.5F, 0.65F, 0.5F)),
                half,
                half);
        ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
        TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);

        return Pair.of(this, tr.getMatrix());
    }
}
