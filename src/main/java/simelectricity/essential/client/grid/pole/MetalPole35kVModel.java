package simelectricity.essential.client.grid.pole;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.utils.client.SERenderHeap;

public class MetalPole35kVModel extends CodeBasedModel {
	public final static MetalPole35kVModel instance = new MetalPole35kVModel();
	
	@EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
	@EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;
	
	// [(8-DirHorizontal8.ordinal)&7]
	@SuppressWarnings("unchecked")
	public final static List<BakedQuad>[] insulator35Kv = new List[8];
	@SuppressWarnings("unchecked")
	public final static List<BakedQuad>[] bakedModelType0 = new List[8];
	@SuppressWarnings("unchecked")
	public final static List<BakedQuad>[] bakedModelType1 = new List[8];
	// [(8-DirHorizontal8.ordinal)&7][partId]
	@SuppressWarnings("unchecked")
	public final static List<BakedQuad>[][] bakedModelBasePart = new List[8][4];
	public static RawQuadGroup modelInsulator = null;
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		return ImmutableList.of();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return textureMetal;
	}
	
	@Override
    public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		SERenderHeap model = Models.renderTower0Top(textureMetal);
		
        modelInsulator = Models.render35KvInsulator(textureMetal, textureInsulator);		
        
    	for (int facing=0; facing<8; facing++) {
    		List<BakedQuad> type0 = new LinkedList<>();
        	List<BakedQuad> insulator35Kv = new LinkedList<>();
    		List<BakedQuad> type1 = new LinkedList<>();

    		@SuppressWarnings("unchecked")
			List<BakedQuad>[] baseParts = new LinkedList[] {
    				new LinkedList<>(), new LinkedList<>(), 
    				new LinkedList<>(), new LinkedList<>()};

    		//Type 0
            RawQuadGroup insulator = modelInsulator.clone();
            insulator.rotateAroundZ(180);
            insulator.translateCoord(-3.95F, 7F, 0);
            insulator.rotateAroundVector(facing*45, 0, 1, 0);
            insulator.translateCoord(0.5F, 0, 0.5F);
            insulator.bake(insulator35Kv);    		

            //Legacy Rotation
    		int rotation = facing * 45;
            model.clone().rotateAroundY(rotation+90).transform(0.5, -18, 0.5).bake(type0);
            
            //Type 1
            RawQuadGroup insulators = new RawQuadGroup();
            insulators.merge(modelInsulator.clone().translateCoord(0, -2F, -4.9F));
            insulators.merge(modelInsulator.clone().translateCoord(0, -2F, 4.9F));
            insulators.merge(modelInsulator.clone().translateCoord(0, 5F, 3.95F));
            insulators.rotateAroundY(rotation+90).translateCoord(0.5F, 0, 0.5F).bake(type1);
            type1.addAll(type0);
            
            // Base
            if ((facing>>1)<<1 == facing) {
				Models.renderTower0Bottom(textureMetal).transform(2, 0, 2).rotateAroundY(rotation)
						.transform(0.5F, 0, 0.5F).bake(baseParts[3]);
				Models.renderTower0Bottom(textureMetal).transform(2, 0, 2).rotateAroundY(rotation + 90)
						.transform(0.5F, 0, 0.5F).bake(baseParts[2]);
				Models.renderTower0Bottom(textureMetal).transform(2, 0, 2).rotateAroundY(rotation + 180)
						.transform(0.5F, 0, 0.5F).bake(baseParts[1]);
				Models.renderTower0Bottom(textureMetal).transform(2, 0, 2).rotateAroundY(rotation + 270)
						.transform(0.5F, 0, 0.5F).bake(baseParts[0]);
            } else {
				Models.renderTower0Bottom(textureMetal).transform(2.12, 0, 2.12).rotateAroundY(rotation)
						.transform(0.5F, 0, 0.5F).bake(baseParts[3]);
				Models.renderTower0Bottom(textureMetal).transform(2.12, 0, 2.12).rotateAroundY(rotation + 90)
						.transform(0.5F, 0, 0.5F).bake(baseParts[2]);
				Models.renderTower0Bottom(textureMetal).transform(2.12, 0, 2.12).rotateAroundY(rotation + 180)
						.transform(0.5F, 0, 0.5F).bake(baseParts[1]);
				Models.renderTower0Bottom(textureMetal).transform(2.12, 0, 2.12).rotateAroundY(rotation + 270)
						.transform(0.5F, 0, 0.5F).bake(baseParts[0]);          	
            }

            
            MetalPole35kVModel.bakedModelType0[facing] = type0;
            MetalPole35kVModel.insulator35Kv[facing] = insulator35Kv;
            MetalPole35kVModel.bakedModelType1[facing] = type1;
            MetalPole35kVModel.bakedModelBasePart[facing] = baseParts;
    	}
    }
}
