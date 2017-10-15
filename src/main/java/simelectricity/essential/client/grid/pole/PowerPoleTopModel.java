package simelectricity.essential.client.grid.pole;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
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
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

@SideOnly(Side.CLIENT)
public class PowerPoleTopModel extends CodeBasedModel {
	@EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
	@EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;

	@Override
    public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    	SERenderHeap model = Models.renderTower0Top(textureMetal);
		
		SERenderHeap modelInsulator = Models.renderInsulatorString(1.4, textureInsulator);
        double[][] rod2 = SERenderHelper.createCubeVertexes(0.1, 2, 0.1);
        SERenderHelper.translateCoord(rod2, 0, -0.3, 0);
        modelInsulator.addCube(rod2, textureMetal);
        modelInsulator.transform(0, 0.3, 0);
        FastTESRPowerPoleTop.modelInsulator = modelInsulator;
        
    	for (int facing=0; facing< 8; facing ++) {
    		LinkedList<BakedQuad> type0 = new LinkedList();
        	LinkedList<BakedQuad> insulator35Kv = new LinkedList();
    		LinkedList<BakedQuad> type1 = new LinkedList();
    		/*
    		 * Meta facing: MC: South - 0, OpenGL: Xpos(East) - 0
    		 */
    		int rotation = facing * 45 - 90;
    		
    		//Type 0
            SERenderHeap insulatorHeap = modelInsulator.clone();
            insulatorHeap.rotateAroundZ(180);
            insulatorHeap.transform(0, 7, 3.95);
            insulatorHeap.rotateAroundVector(rotation, 0, 1, 0);
            insulatorHeap.transform(0.5, 0, 0.5);
            insulatorHeap.bake(insulator35Kv);
            model.clone().rotateAroundY(rotation).transform(0.5, -18, 0.5).bake(type0);
            
            //Type 1
            SERenderHeap type1Model = model.clone();
            SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
            double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
            SERenderHelper.translateCoord(rod, 0, -0.15, 0);
            insulator.addCube(rod, textureMetal);
            type1Model.appendHeap(insulator.clone().transform(0, 18 - 1.85, -4.9));
            type1Model.appendHeap(insulator.clone().transform(0, 18 - 1.85, 4.9));
            type1Model.appendHeap(insulator.transform(0, 23.15, 3.95));
            type1Model.rotateAroundY(rotation).transform(0.5, -18, 0.5).bake(type1);
            
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
