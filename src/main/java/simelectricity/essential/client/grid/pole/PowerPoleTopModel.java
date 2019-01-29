package simelectricity.essential.client.grid.pole;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.utils.client.SERenderHeap;

@SideOnly(Side.CLIENT)
public class PowerPoleTopModel extends CodeBasedModel {
	@EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
	@EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;

	@Override
    public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    	SERenderHeap model = Models.renderTower0Top(textureMetal);
		
        FastTESRPowerPoleTop.modelInsulator = Models.render35KvInsulator(textureMetal, textureInsulator);		
        
    	for (int facing=0; facing< 8; facing ++) {
    		List<BakedQuad> type0 = new ArrayList();
        	List<BakedQuad> insulator35Kv = new ArrayList();
    		List<BakedQuad> type1 = new ArrayList();
    		

    		//Type 0
            RawQuadGroup insulator = FastTESRPowerPoleTop.modelInsulator.clone();
            insulator.rotateAroundZ(180);
            insulator.translateCoord(-3.95F, 7F, 0);
            insulator.rotateAroundVector(facing*45, 0, 1, 0);
            insulator.translateCoord(0.5F, 0, 0.5F);
            insulator.bake(insulator35Kv);    		

            //Legacy Rotation
    		int rotation = facing * 45 + 90;
            model.clone().rotateAroundY(rotation).transform(0.5, -18, 0.5).bake(type0);
            
            //Type 1
            RawQuadGroup insulators = new RawQuadGroup();
            insulators.merge(FastTESRPowerPoleTop.modelInsulator.clone().translateCoord(0, -2F, -4.9F));
            insulators.merge(FastTESRPowerPoleTop.modelInsulator.clone().translateCoord(0, -2F, 4.9F));
            insulators.merge(FastTESRPowerPoleTop.modelInsulator.clone().translateCoord(0, 5F, 3.95F));
            insulators.rotateAroundY(rotation).translateCoord(0.5F, 0, 0.5F).bake(type1);
            type1.addAll(type0);
            
            FastTESRPowerPoleTop.bakedModelType0[facing] = type0;
            FastTESRPowerPoleTop.insulator35Kv[facing] = insulator35Kv;
            FastTESRPowerPoleTop.bakedModelType1[facing] = type1;
    	}
    }

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return ImmutableList.of();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return textureMetal;
	}
}
