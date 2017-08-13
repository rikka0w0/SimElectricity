package simelectricity.essential.client.coverpanel;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import simelectricity.essential.Essential;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.utils.client.SERawQuadCube;

@SideOnly(Side.CLIENT)
public abstract class GenericCoverPanelRender<T extends ISECoverPanel> implements ISECoverPanelRender<T>{
	private final ResourceLocation textureRes;
	protected TextureAtlasSprite texture;
	protected LinkedList<BakedQuad>[] bakedQuads= new LinkedList[6];	//BakedQuads for all 6 directions
	
	protected GenericCoverPanelRender(String textureName) {
		MinecraftForge.EVENT_BUS.register(this);
		this.textureRes = new ResourceLocation(Essential.modID + ":blocks/coverpanel/" + textureName);
	}

	@SubscribeEvent
    public void stitcherEventPre(TextureStitchEvent.Pre event) {
		//Register textures
		TextureMap map = event.getMap();
		
		this.texture = map.registerSprite(textureRes);
	}
	
	public void bake() {
		float thickness = ISECoverPanel.thickness;
		float[][] vertexes;
		TextureAtlasSprite[] textures = new TextureAtlasSprite[]{texture,texture,texture,texture,texture,texture};

		//Down
		vertexes = new float[8][];
		vertexes[0] = new float[] {1-thickness, thickness, 1-thickness};
		vertexes[1] = new float[] {1-thickness, thickness, thickness};
		vertexes[2] = new float[] {thickness, thickness, thickness};
		vertexes[3] = new float[] {thickness, thickness, 1-thickness};
		vertexes[4] = new float[] {1, 0, 1};
		vertexes[5] = new float[] {1, 0, 0};
		vertexes[6] = new float[] {0, 0, 0};
		vertexes[7] = new float[] {0, 0, 1};
		bakedQuads[0] = new LinkedList<BakedQuad>();
		(new SERawQuadCube(vertexes, textures)).bake(bakedQuads[0]);

		
		//Up
		vertexes = new float[8][];
		vertexes[0] = new float[] {1, 1, 1};
		vertexes[1] = new float[] {1, 1, 0};
		vertexes[2] = new float[] {0, 1, 0};
		vertexes[3] = new float[] {0, 1, 1};
		vertexes[4] = new float[] {1-thickness, 1-thickness, 1-thickness};
		vertexes[5] = new float[] {1-thickness, 1-thickness, thickness};
		vertexes[6] = new float[] {thickness, 1-thickness, thickness};
		vertexes[7] = new float[] {thickness, 1-thickness, 1-thickness};
		bakedQuads[1] = new LinkedList<BakedQuad>();
		(new SERawQuadCube(vertexes, textures)).bake(bakedQuads[1]);
		
		//North
		vertexes = new float[8][];
		vertexes[0] = new float[] {1-thickness, 1-thickness, thickness};
		vertexes[1] = new float[] {1, 1, 0};
		vertexes[2] = new float[] {0, 1, 0};
		vertexes[3] = new float[] {thickness, 1-thickness, thickness};
		vertexes[4] = new float[] {1-thickness, thickness, thickness};
		vertexes[5] = new float[] {1, 0, 0};
		vertexes[6] = new float[] {0, 0, 0};
		vertexes[7] = new float[] {thickness, thickness, thickness};
		bakedQuads[2] = new LinkedList<BakedQuad>();
		(new SERawQuadCube(vertexes, textures)).bake(bakedQuads[2]);
		
		//South
		vertexes = new float[8][];
		vertexes[0] = new float[] {1, 1, 1};
		vertexes[1] = new float[] {1-thickness, 1-thickness, 1-thickness};
		vertexes[2] = new float[] {thickness, 1-thickness, 1-thickness};
		vertexes[3] = new float[] {0, 1, 1};
		vertexes[4] = new float[] {1, 0, 1};
		vertexes[5] = new float[] {1-thickness, thickness, 1-thickness};
		vertexes[6] = new float[] {thickness, thickness, 1-thickness};
		vertexes[7] = new float[] {0, 0, 1};
		bakedQuads[3] = new LinkedList<BakedQuad>();
		(new SERawQuadCube(vertexes, textures)).bake(bakedQuads[3]);
		
		//West
		vertexes = new float[8][];
		vertexes[0] = new float[] {thickness, 1-thickness, 1-thickness};
		vertexes[1] = new float[] {thickness, 1-thickness, thickness};
		vertexes[2] = new float[] {0, 1, 0};
		vertexes[3] = new float[] {0, 1, 1};
		vertexes[4] = new float[] {thickness, thickness, 1-thickness};
		vertexes[5] = new float[] {thickness, thickness, thickness};
		vertexes[6] = new float[] {0, 0, 0};
		vertexes[7] = new float[] {0, 0, 1};
		bakedQuads[4] = new LinkedList<BakedQuad>();
		(new SERawQuadCube(vertexes, textures)).bake(bakedQuads[4]);
		
		//East
		vertexes = new float[8][];
		vertexes[0] = new float[] {1, 1, 1};
		vertexes[1] = new float[] {1, 1, 0};
		vertexes[2] = new float[] {1-thickness, 1-thickness, thickness};
		vertexes[3] = new float[] {1-thickness, 1-thickness, 1-thickness};
		vertexes[4] = new float[] {1, 0, 1};
		vertexes[5] = new float[] {1, 0, 0};
		vertexes[6] = new float[] {1-thickness, thickness, thickness};
		vertexes[7] = new float[] {1-thickness, thickness, 1-thickness};
		bakedQuads[5] = new LinkedList<BakedQuad>();
		(new SERawQuadCube(vertexes, textures)).bake(bakedQuads[5]);	
	}
	
	@Override
	public void renderCoverPanel(ISECoverPanel coverPanel, EnumFacing side, List quads) {
		if (MinecraftForgeClient.getRenderLayer() != BlockRenderLayer.SOLID) 
			return;
		
		if (bakedQuads[side.ordinal()] == null) {
			bake();
		}
		
		quads.addAll(bakedQuads[side.ordinal()]);
	}
}
