package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.IModelBakeHandler;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class GenericCoverPanelRender<T extends ISECoverPanel> implements ISECoverPanelRender<T>, IModelBakeHandler {
    private final ResourceLocation textureRes;
    protected TextureAtlasSprite texture;

    @SuppressWarnings("unchecked")
	protected List<BakedQuad>[] bakedQuads = new List[6];    //BakedQuads for all 6 directions

    protected GenericCoverPanelRender(String textureName) {
    	this(Essential.MODID, textureName);
    }
    
    protected GenericCoverPanelRender(String modId, String textureName) {
        textureRes = new ResourceLocation(Essential.MODID, "block/coverpanel/" + textureName);
    }

    @Override
    public void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
    	if (EasyTextureLoader.isBlockAtlas(event))
    		event.addSprite(textureRes);
    }
    
    @Override
    public IBakedModel onModelBakeEvent() {
    	this.texture = EasyTextureLoader.blockTextureGetter().apply(textureRes);
        float thickness = ISECoverPanel.thickness;
        float[][] vertexes;
        TextureAtlasSprite[] textures = {this.texture, this.texture, this.texture, this.texture, this.texture, this.texture};

        //Down
        vertexes = new float[8][];
        vertexes[0] = new float[]{1 - thickness, thickness, 1 - thickness};
        vertexes[1] = new float[]{1 - thickness, thickness, thickness};
        vertexes[2] = new float[]{thickness, thickness, thickness};
        vertexes[3] = new float[]{thickness, thickness, 1 - thickness};
        vertexes[4] = new float[]{1, 0, 1};
        vertexes[5] = new float[]{1, 0, 0};
        vertexes[6] = new float[]{0, 0, 0};
        vertexes[7] = new float[]{0, 0, 1};
        this.bakedQuads[0] = new ArrayList<BakedQuad>();
        new RawQuadCube(vertexes, textures).bake(this.bakedQuads[0]);


        //Up
        vertexes = new float[8][];
        vertexes[0] = new float[]{1, 1, 1};
        vertexes[1] = new float[]{1, 1, 0};
        vertexes[2] = new float[]{0, 1, 0};
        vertexes[3] = new float[]{0, 1, 1};
        vertexes[4] = new float[]{1 - thickness, 1 - thickness, 1 - thickness};
        vertexes[5] = new float[]{1 - thickness, 1 - thickness, thickness};
        vertexes[6] = new float[]{thickness, 1 - thickness, thickness};
        vertexes[7] = new float[]{thickness, 1 - thickness, 1 - thickness};
        this.bakedQuads[1] = new ArrayList<BakedQuad>();
        new RawQuadCube(vertexes, textures).bake(this.bakedQuads[1]);

        //North
        vertexes = new float[8][];
        vertexes[0] = new float[]{1 - thickness, 1 - thickness, thickness};
        vertexes[1] = new float[]{1, 1, 0};
        vertexes[2] = new float[]{0, 1, 0};
        vertexes[3] = new float[]{thickness, 1 - thickness, thickness};
        vertexes[4] = new float[]{1 - thickness, thickness, thickness};
        vertexes[5] = new float[]{1, 0, 0};
        vertexes[6] = new float[]{0, 0, 0};
        vertexes[7] = new float[]{thickness, thickness, thickness};
        this.bakedQuads[2] = new ArrayList<BakedQuad>();
        new RawQuadCube(vertexes, textures).bake(this.bakedQuads[2]);

        //South
        vertexes = new float[8][];
        vertexes[0] = new float[]{1, 1, 1};
        vertexes[1] = new float[]{1 - thickness, 1 - thickness, 1 - thickness};
        vertexes[2] = new float[]{thickness, 1 - thickness, 1 - thickness};
        vertexes[3] = new float[]{0, 1, 1};
        vertexes[4] = new float[]{1, 0, 1};
        vertexes[5] = new float[]{1 - thickness, thickness, 1 - thickness};
        vertexes[6] = new float[]{thickness, thickness, 1 - thickness};
        vertexes[7] = new float[]{0, 0, 1};
        this.bakedQuads[3] = new ArrayList<BakedQuad>();
        new RawQuadCube(vertexes, textures).bake(this.bakedQuads[3]);

        //West
        vertexes = new float[8][];
        vertexes[0] = new float[]{thickness, 1 - thickness, 1 - thickness};
        vertexes[1] = new float[]{thickness, 1 - thickness, thickness};
        vertexes[2] = new float[]{0, 1, 0};
        vertexes[3] = new float[]{0, 1, 1};
        vertexes[4] = new float[]{thickness, thickness, 1 - thickness};
        vertexes[5] = new float[]{thickness, thickness, thickness};
        vertexes[6] = new float[]{0, 0, 0};
        vertexes[7] = new float[]{0, 0, 1};
        this.bakedQuads[4] = new ArrayList<BakedQuad>();
        new RawQuadCube(vertexes, textures).bake(this.bakedQuads[4]);

        //East
        vertexes = new float[8][];
        vertexes[0] = new float[]{1, 1, 1};
        vertexes[1] = new float[]{1, 1, 0};
        vertexes[2] = new float[]{1 - thickness, 1 - thickness, thickness};
        vertexes[3] = new float[]{1 - thickness, 1 - thickness, 1 - thickness};
        vertexes[4] = new float[]{1, 0, 1};
        vertexes[5] = new float[]{1, 0, 0};
        vertexes[6] = new float[]{1 - thickness, thickness, thickness};
        vertexes[7] = new float[]{1 - thickness, thickness, 1 - thickness};
        this.bakedQuads[5] = new ArrayList<BakedQuad>();
        new RawQuadCube(vertexes, textures).bake(this.bakedQuads[5]);
        
    	return null;
    }

    @Override
    public void renderCoverPanel(T coverPanel, Direction side, Random rand, List<BakedQuad> quads) {
        if (MinecraftForgeClient.getRenderLayer() != BlockRenderLayer.SOLID)
            return;

        quads.addAll(this.bakedQuads[side.ordinal()]);

        quads.addAll(SupportRender.forSide(side));
    }
}
