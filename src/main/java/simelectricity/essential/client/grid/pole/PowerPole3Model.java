package simelectricity.essential.client.grid.pole;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.ModelPerspectives;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.grid.EnumBlockTypePole3;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PowerPole3Model extends CodeBasedModel {
    private final EnumBlockTypePole3 blockType;
    private final int rotation;
    private final List<BakedQuad> quads = new ArrayList();

    @EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
    @EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;
    @EasyTextureLoader.Mark(ResourcePaths.concrete)
    private final TextureAtlasSprite textureConcrete = null;
    
    public PowerPole3Model(EnumBlockTypePole3 blockType, int facing) {
        this.rotation = facing * 45 - 90;
        this.blockType = blockType;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.textureMetal;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
    	if (side != null)
            return ImmutableList.of();
        
    	if (blockType == EnumBlockTypePole3.Crossarm10kVT2) {
    		this.quads.clear();
    		
            RawQuadGroup model = new RawQuadGroup();
            
            model.add(new RawQuadCube(0.15F, 0.08F, 1.6F, textureMetal).translateCoord(0.2F, 0.05F, 0));
            
            model.rotateAroundY(rotation);
            model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
            model.translateCoord(0.5F, 0, 0.5F);
            model.bake(this.quads);
    	}
    	
        return this.quads;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
		this.quads.clear();
		
        FastTESRPowerPole3.modelInsulator10kV = Models.render10kVInsulator(textureMetal, textureInsulator);

        RawQuadGroup insulator = null;
        //Build the insulator model
        switch (blockType) {
            case Pole:
                break;
            case Crossarm10kVT0:
            case Crossarm10kVT1:
                insulator = Models.render10kVInsulator(textureMetal, textureInsulator);
                break;
            case Crossarm415VT0:
                insulator = Models.render415VInsulator(textureMetal, textureInsulator);
                break;
			case Crossarm10kVT2:
				break;
        }

        RawQuadGroup model = new RawQuadGroup();
        
        switch (blockType) {
            case Pole:
                break;
            case Crossarm10kVT0:
                model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
                model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
                model.merge(insulator.clone().translateCoord(0, 1F, 0));
                break;
            case Crossarm10kVT1:
                model.add(new RawQuadCube(0.15F, 0.08F, 1.6F, textureMetal).translateCoord(0, 0.05F, 0));
                break;
            case Crossarm415VT0:
                model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
                model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
                break;
            case Crossarm10kVT2:
            	break;
        }

        model.rotateAroundY(rotation);
        model.add((new RawQuadCube(0.25F, 1, 0.25F, textureConcrete)));
        model.translateCoord(0.5F, 0, 0.5F);
        model.bake(this.quads);
	}
    
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return itemCameraTransforms;
    }
    
    public final static ItemCameraTransforms itemCameraTransforms = ModelPerspectives.create(ModelPerspectives.ItemBlock,
    		null, null,
    		new ItemTransformVec3f(new Vector3f(0, 225, 0), 	new Vector3f(0,0.15F,0), 				new Vector3f(0.4F, 0.4F, 0.4F)),		//firstperson_leftIn
    		new ItemTransformVec3f(new Vector3f(0, 45, 0), 		new Vector3f(0,0.15F,0), 				new Vector3f(0.4F, 0.4F, 0.4F)),		//firstperson_rightIn
    		null, 
    		new ItemTransformVec3f(new Vector3f(45, 210, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F))	//gui
    		, null, null);
}
