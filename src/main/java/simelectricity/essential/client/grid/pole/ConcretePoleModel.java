package simelectricity.essential.client.grid.pole;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.ModelPerspectives;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;

import net.minecraft.client.renderer.Vector3f;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.grid.BlockPoleConcrete;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ConcretePoleModel extends CodeBasedModel {
    private final BlockPoleConcrete.Type blockType;
    private final int rotation;
    private final List<BakedQuad> quads = new ArrayList();

    @EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
    @EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite glassInsulator = null;
    @EasyTextureLoader.Mark(ResourcePaths.concrete)
    private final TextureAtlasSprite textureConcrete = null;
    
    public ConcretePoleModel(BlockPoleConcrete.Type blockType, DirHorizontal8 facing8) {
        this.rotation = ((8-facing8.ordinal())&7) * 45- 90;
        this.blockType = blockType;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.textureMetal;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
    	if (side != null)
            return ImmutableList.of();
    	
        return this.quads;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

	@Override
	public void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry){
		this.quads.clear();
		
        ConcretePoleTER.modelInsulator10kV = Models.render10kVInsulator(textureMetal, glassInsulator);
        ConcretePoleTER.modelInsulator415V = Models.render415VInsulator(textureMetal, glassInsulator);

        RawQuadGroup insulator = null;
        //Build the insulator model
        switch (blockType) {
            case pole:
                break;
            case crossarm10kvt0:
            case crossarm10kvt1:
                insulator = Models.render10kVInsulator(textureMetal, glassInsulator);
                break;
            case crossarm415vt0:
                insulator = Models.render415VInsulator(textureMetal, glassInsulator);
                break;
			case branching10kv:
				break;
			case branching415v:
				break;
			default:
				break;
        }

        RawQuadGroup model = new RawQuadGroup();
        
        switch (blockType) {
            case pole:
                break;
            case crossarm10kvt0:
                model.add(new RawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
                model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
                model.merge(insulator.clone().translateCoord(0, 1F, 0));
                break;
            case crossarm10kvt1:
                model.add(new RawQuadCube(0.15F, 0.08F, 1.6F, textureMetal).translateCoord(0, 0.05F, 0));
                break;
            case crossarm415vt0:
                model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
                model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
                model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
                break;
            case branching10kv:
            	model.add(new RawQuadCube(0.15F, 0.08F, 1.6F, textureMetal).translateCoord(0.2F, 0.05F, 0));
            	break;
			case branching415v:
				model.add(new RawQuadCube(0.15F, 0.1F, 1.94F, textureMetal).translateCoord(0.2F, 0.45F, 0));
				break;
			default:
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
    		new ItemTransformVec3f(new Vector3f(45, 30, 0), new Vector3f(0.025F, 0, 0), new Vector3f(0.5F, 0.5F, 0.5F))	//gui
    		, null, null);
}
