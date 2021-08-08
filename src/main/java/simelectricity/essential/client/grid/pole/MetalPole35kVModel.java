package simelectricity.essential.client.grid.pole;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.utils.client.SERenderHeap;

public class MetalPole35kVModel extends CodeBasedModel {	
	@EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
	@EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;
	
	// [(8-DirHorizontal8.ordinal)&7]
	public final List<BakedQuad>[] insulator35Kv;
	@SuppressWarnings("unchecked")
	public final List<BakedQuad>[] bakedModelTop = new List[8];
	// [(8-DirHorizontal8.ordinal)&7][partId]
	@SuppressWarnings("unchecked")
	public final List<BakedQuad>[][] bakedModelBasePart = new List[8][4];
	public RawQuadGroup modelInsulator = null;

	public final boolean terminals;
	@SuppressWarnings("unchecked")
	public MetalPole35kVModel(boolean terminals) {
		this.terminals = terminals;
		if (terminals) {
			this.insulator35Kv = null;
		} else {
			this.insulator35Kv = new List[8];
		}
	}
	
	@Override
    protected boolean skipLegacyTextureRegistration() {
    	return true;
    }

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		return emptyQuadList;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return textureMetal;
	}
	
	@Override
    public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		SERenderHeap model = Models.renderTower0Top(textureMetal);
		
		RawQuadGroup modelInsulator = Models.render35KvInsulator(textureMetal, textureInsulator);		
        if (!this.terminals)
        	this.modelInsulator = modelInsulator;
		
    	for (int facing=0; facing<8; facing++) {
    		List<BakedQuad> modelTop = new LinkedList<>();
        	List<BakedQuad> modelHangingInsulator = new LinkedList<>();

    		@SuppressWarnings("unchecked")
			List<BakedQuad>[] baseParts = new LinkedList[] {
    				new LinkedList<>(), new LinkedList<>(), 
    				new LinkedList<>(), new LinkedList<>()};

            // Top
    		int rotation = facing * 45;
            model.clone().rotateAroundY(rotation+90).transform(0.5, -18, 0.5).bake(modelTop);
            
            // Insulators
            if (this.terminals) {
                //Type 1
                RawQuadGroup insulators = new RawQuadGroup();
                insulators.merge(modelInsulator.clone().translateCoord(0, -2F, -4.9F));
                insulators.merge(modelInsulator.clone().translateCoord(0, -2F, 4.9F));
                insulators.merge(modelInsulator.clone().translateCoord(0, 5F, 3.95F));
                insulators.rotateAroundY(rotation+90).translateCoord(0.5F, 0, 0.5F).bake(modelTop);
            } else {
            	//Type 0
                RawQuadGroup insulator = modelInsulator.clone();
                insulator.rotateAroundZ(180);
                insulator.translateCoord(-3.95F, 7F, 0);
                insulator.rotateAroundVector(facing*45, 0, 1, 0);
                insulator.translateCoord(0.5F, 0, 0.5F);
                insulator.bake(modelHangingInsulator);
                this.insulator35Kv[facing] = modelHangingInsulator;
            }
            
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

            this.bakedModelTop[facing] = modelTop;
            this.bakedModelBasePart[facing] = baseParts;
    	}
    }
}
