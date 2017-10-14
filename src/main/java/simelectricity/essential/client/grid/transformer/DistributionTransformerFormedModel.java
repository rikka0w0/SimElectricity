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
    @EasyTextureLoader.Mark("sime_essential:render/transmission/concrete")
    private final TextureAtlasSprite textureCeramic = null;   
    
    
	public DistributionTransformerFormedModel(EnumDistributionTransformerRenderPart part, int facing, boolean mirrored) {
		this.part = part;
		this.rotation = rotationMatrix[facing] * 45 - 90;
        this.mirrored = mirrored;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		LinkedList<BakedQuad> list = new LinkedList(quads);
		

		if (state != null) {
			if (state.getValue(EnumDistributionTransformerRenderPart.property) == EnumDistributionTransformerRenderPart.Pole10kVRight) {
				RawQuadGroup model = new RawQuadGroup();


				

				
		        model.rotateAroundY(rotation);
		        model.translateCoord(0.5F, 0, 0.5F);
		        model.bake(list);
			}
			
			if (state.getValue(EnumDistributionTransformerRenderPart.property) == EnumDistributionTransformerRenderPart.AuxRight) {
				RawQuadGroup model = new RawQuadGroup();
				

				model.merge(Models.render10kVInsulatorSmall(textureMetal, textureInsulator).rotateAroundX(180).translateCoord(-0.45F, 0.55F, 0.15F));
				model.merge(Models.render10kVInsulatorSmall(textureMetal, textureInsulator).rotateAroundX(180).translateCoord(-0.85F, 0.55F, -0.15F));
				
				model.merge(FastTESRPowerPole.renderParabolicCable(new Object[] {
						new Vec3f(-1.5F, 0.275F, -0.65F),
						0.15F, new Vec3f(-1.125F, 0.775F, -0.65F)}, 0.03F));
				
				model.merge(FastTESRPowerPole.renderParabolicCable(new Object[] {
						new Vec3f(-1.5F, 0.275F, 0),
						0.1F, new Vec3f(-0.85F, 0.3F, -0.15F),
						0.2F, new Vec3f(-0.775F, 0.775F, -0.65F)}, 0.03F));
				
				model.merge(FastTESRPowerPole.renderParabolicCable(new Object[] {
						new Vec3f(-1.5F, 0.275F, 0.65F),
						0.1F, new Vec3f(-0.45F, 0.3F, 0.15F),
						0.2F, new Vec3f(-0.425F, 0.775F, -0.65F)}, 0.03F));
				

				
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
			//10Kv insulator and cable
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, 0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.425F,0.85F, 0.15F), new Vec3f(-0.425F,0.85F,2.85F), false, 0.1F, 0.03F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, 0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.775F,0.85F, 0.15F), new Vec3f(-0.775F,0.85F,2.85F), false, 0.1F, 0.03F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, 0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.125F,0.85F, 0.15F), new Vec3f(-1.125F,0.85F,2.85F), false, 0.1F, 0.03F));
            
            model.add((new RawQuadCube(2.3F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.275F, 0.5F, 0.15F));
            
            model.add((new RawQuadCube(0.1F, 0.05F, 1.625F, textureMetal)).translateCoord(0.5F, 0.6F, 0.6875F));
            model.add((new RawQuadCube(0.1F, 0.05F, 1.625F, textureMetal)).translateCoord(0.8F, 0.6F, 0.6875F));

            //Switch
			model.merge(Models.render415VSwitch(textureMetal, textureCeramic).translateCoord(0.5F, 0.4F, 1.25F));
			model.merge(Models.render415VSwitch(textureMetal, textureCeramic).translateCoord(0.5F, 0.4F, 0.75F));
			
			//Transformer cable
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[]{
					new Vec3f(0.8F, 0.375F, 0.75F),
					0.15F, new Vec3f(1.1F, 0.5F, 0.75F),
					-0.1F, new Vec3f(0.9F, 2.2F, 0.75F)}, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[]{
					new Vec3f(0.8F, 0.375F, 1.25F),
					0.15F, new Vec3f(1.1F, 0.5F, 1.25F),
					-0.1F, new Vec3f(0.8F, 1.25F, 1.25F),
					0.1F, new Vec3f(0.45F, 2.15F, 1.25F) }, 0.03F));
            
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
		case AuxMiddle:
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			//10Kv insulator and cable
			model.merge(insulator.clone().translateCoord(-0.425F, 0.6F, -0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.425F,0.85F, -0.15F), new Vec3f(-0.425F,0.85F, 1.85F), false, 0.1F, 0.03F));
			model.merge(insulator.clone().translateCoord(-0.775F, 0.6F, -0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.775F,0.85F, -0.15F), new Vec3f(-0.775F,0.85F, 1.85F), false, 0.1F, 0.03F));
			model.merge(insulator.clone().translateCoord(-1.125F, 0.6F, -0.15F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.125F,0.85F, -0.15F), new Vec3f(-1.125F,0.85F, 1.85F), false, 0.1F, 0.03F));
            
            model.add((new RawQuadCube(2.3F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.275F, 0.5F, -0.15F));
            
            model.add((new RawQuadCube(0.1F, 0.05F, 1.625F, textureMetal)).translateCoord(0.5F, 0.6F, -0.6875F));
            model.add((new RawQuadCube(0.1F, 0.05F, 1.625F, textureMetal)).translateCoord(0.8F, 0.6F, -0.6875F));
            
            //Switch
			model.merge(Models.render415VSwitch(textureMetal, textureCeramic).translateCoord(0.5F, 0.4F, -0.75F));
			model.merge(Models.render415VSwitch(textureMetal, textureCeramic).translateCoord(0.5F, 0.4F, -1.25F));
			
			//Transformer cable
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[]{
					new Vec3f(0.8F, 0.375F, -0.75F),
					0.08F, new Vec3f(1.1F, 0.5F, -0.75F),
					-0.15F, new Vec3f(0.8F, 1.25F, -0.75F),
					-0.05F, new Vec3f(0.2F, 1.4F, -0.75F),
					0.2F, new Vec3f(-0.9F, 2.1F, -0.75F)}, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[]{
					new Vec3f(0.8F, 0.375F, -1.25F),
					0.08F, new Vec3f(1.1F, 0.5F, -1.25F),
					-0.15F, new Vec3f(0.8F, 1.25F, -1.25F),
					-0.05F, new Vec3f(0.2F, 1.4F, -1.25F),
					0.25F, new Vec3f(-0.445F, 2.1F, -1.25F)}, 0.03F));
            
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
			
			//Cable(upwards)
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.725F, 0.7F, 0.65F), new Vec3f(-1.65F,3.8F,0.65F), false, 0, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.725F, 0.7F, 0), new Vec3f(-1.65F,3.8F,0), false, 0, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.725F, 0.7F, -0.65F), new Vec3f(-1.65F,3.8F,-0.65F), false, 0, 0.03F));
			
			//Fuse
			RawQuadGroup fuse = Models.render10kVSwitch(textureMetal, textureCeramic);
			fuse.rotateAroundZ(25).translateCoord(0, -0.2F, 0);
			model.merge(fuse.clone().translateCoord(-1.4F, 0.55F, 0.65F));
			model.merge(fuse.clone().translateCoord(-1.4F, 0.55F, 0));
			model.merge(fuse.clone().translateCoord(-1.4F, 0.55F, -0.65F));
            
			//Pole
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
			
		case Pole10kVLeft:
			insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.rotateAroundY(90);
            
            //Cable
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.74F, 0.55F, 0), new Vec3f(-0.74F, 0.55F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0 , 1.5F, 0), new Vec3f(0F, 1.5F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.74F, 0.55F, 0), new Vec3f(0.74F, 0.55F, 5F), true, 0.2F, 0.05F));

            //Pole
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
			
		case Pole10kVRight:		
            insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
            model.merge(insulator.clone().translateCoord(0, 1F, 0));
            model.rotateAroundY(90);
            
            //Insulator
			insulator = Models.render10kVInsulatorSmall(textureMetal, textureInsulator);
			insulator.rotateAroundZ(90);
			model.merge(insulator.clone().translateCoord(-1.4F, -0.2F, 0.65F));
			model.merge(insulator.clone().translateCoord(-1.4F, -0.2F, 0));
			model.merge(insulator.clone().translateCoord(-1.4F, -0.2F, -0.65F));
            
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, -0.25F, 0.15F));
            model.add((new RawQuadCube(1.5F, 0.1F, 0.05F, textureMetal)).translateCoord(-0.625F, -0.25F, -0.15F));
            model.add((new RawQuadCube(0.05F, 0.1F, 1.4F, textureMetal)).translateCoord(-1.4F, -0.25F, 0));
            
            //Cable
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.74F, 0.55F, 0), new Vec3f(-0.74F, 0.55F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0 , 1.5F, 0), new Vec3f(0F, 1.5F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.74F, 0.55F, 0), new Vec3f(0.74F, 0.55F, -5F), true, 0.2F, 0.05F));
            
			//Transformer Connection
			model.merge(Models.render10kVInsulatorSmall(textureMetal, textureInsulator).rotateAroundX(180).translateCoord(-1, -0.2F, -0.15F));
			model.merge(Models.render10kVInsulatorSmall(textureMetal, textureInsulator).rotateAroundX(270).translateCoord(0, -0.2F, -0.125F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.65F,-0.2F,0.65F), new Vec3f(-0.74F, 0.55F, 0), false, -0.3F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[] {
					new Vec3f(-1.65F,-0.4F, 0),
					0.025F, new Vec3f(-1F,-0.45F, -0.15F),
					0.1F, new Vec3f(0, -0.2F, -0.375F),
					0.1F, new Vec3f(0, 1.4F, -0.6F)}, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-1.65F,-0.4F, -0.65F), new Vec3f(0.74F, 0.45F, -0.8F), false, 0.4F, 0.03F));
            
            //Pole
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
			
		case Pole415VLeft:
            insulator = Models.render415VInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
            model.rotateAroundY(90);
            
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.9F, 0.3F, 0), new Vec3f(-0.9F, 0.3F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.45F, 0.3F, 0), new Vec3f(-0.45F, 0.3F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.45F, 0.3F, 0), new Vec3f(0.45F, 0.3F, 5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.9F, 0.3F, 0), new Vec3f(0.9F, 0.3F, 5F), true, 0.2F, 0.05F));
            
            //Pole
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
		case Pole415VRight:
            insulator = Models.render415VInsulator(textureMetal, textureInsulator);
            model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
            model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
            model.rotateAroundY(90);
            
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.9F, 0.3F, 0), new Vec3f(-0.9F, 0.3F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.45F, 0.3F, 0), new Vec3f(-0.45F, 0.3F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.45F, 0.3F, 0), new Vec3f(0.45F, 0.3F, -5F), true, 0.2F, 0.05F));
            model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.9F, 0.3F, 0), new Vec3f(0.9F, 0.3F, -5F), true, 0.2F, 0.05F));
            
            //Pole
            model.add(new RawQuadCube(0.25F, 1, 0.25F, textureConcrete));
			break;
			
		case TransformerLeft:
			//Support
			model.add((new RawQuadCube(0.05F, 0.1F, 1.5F, textureMetal)).translateCoord(0.15F, 0, -0.25F));
			model.add((new RawQuadCube(0.05F, 0.1F, 1.5F, textureMetal)).translateCoord(-0.15F, 0, -0.25F));
			
			//Body
			model.add((new RawQuadCube(0.8F, 0.9F, 1.8F, textureMetal)).translateCoord(0, 0.1F, 0.5F));
			//Heatsinker(Front)
			model.add((new RawQuadCube(0.2F, 0.7F, 1.4F, textureMetal)).translateCoord(-0.5F, 0.15F, 0.5F));
			//Heatsinker(Back)
			model.add((new RawQuadCube(0.2F, 0.7F, 1.4F, textureMetal)).translateCoord(0.5F, 0.15F, 0.5F));
			
			//Insulators
			model.merge(Models.render415VInsulatorTall(textureMetal, textureCeramic).translateCoord(0.25F, 1, 0));
			model.merge(Models.render415VInsulatorTall(textureMetal, textureCeramic).translateCoord(0.25F, 1, 0.333F));
			model.merge(Models.render415VInsulatorTall(textureMetal, textureCeramic).translateCoord(0.25F, 1, 0.666F));
			model.merge(Models.render415VInsulatorTall(textureMetal, textureCeramic).translateCoord(0.25F, 1, 1F));
			
			model.merge(Models.render10kVInsulatorTall(textureMetal, textureCeramic).translateCoord(-0.25F, 1, 0));
			model.merge(Models.render10kVInsulatorTall(textureMetal, textureCeramic).translateCoord(-0.25F, 1, 0.5F));
			model.merge(Models.render10kVInsulatorTall(textureMetal, textureCeramic).translateCoord(-0.25F, 1, 1F));
			
			//Cable
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.25F, 1.4F, 0), new Vec3f(-1.125F, 2.75F, 0), false, 0.3F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.25F, 1.4F, 0.5F), new Vec3f(-0.775F, 2.75F, 0.5F), false, 0.15F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(-0.25F, 1.4F, 1), new Vec3f(-0.425F, 2.75F, 1), false, 0.05F, 0.03F));
			
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[]{new Vec3f(0.25F, 1.325F, 0), 0.05F,new Vec3f(0.3F, 1.8F, -0.2F), -0.05F, new Vec3f(0.5F, 2.4F, -0.25F)}, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.25F, 1.325F, 0.333F), new Vec3f(0.5F, 2.4F, 0.25F), false, -0.15F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Vec3f(0.25F, 1.325F, 0.666F), new Vec3f(0.5F, 2.4F, 0.75F), false, -0.15F, 0.03F));
			model.merge(FastTESRPowerPole.renderParabolicCable(new Object[]{new Vec3f(0.25F, 1.325F, 1F), 0.05F,new Vec3f(0.3F, 1.8F, 1.2F), -0.05F, new Vec3f(0.5F, 2.4F, 1.25F)}, 0.03F));
			
			break;
		case TransformerRight:
			//Support
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
