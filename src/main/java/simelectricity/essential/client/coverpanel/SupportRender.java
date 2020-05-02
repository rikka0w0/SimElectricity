package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.IModelBakeHandler;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public enum SupportRender implements IModelBakeHandler {
	INSTANCE;
	
	private final static ResourceLocation resLoc = new ResourceLocation(Essential.MODID, "block/coverpanel/support");
    private static TextureAtlasSprite[] textures;
    private final static List<BakedQuad>[] bakedQuads = new List[6];    //BakedQuads for all 6 directions

    @Override
    public void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
		if (!EasyTextureLoader.isBlockAtlas(event))
			return;
		
        event.addSprite(resLoc);
    }
    
	@Override
	public IBakedModel onModelBakeEvent() {
		TextureAtlasSprite texture = EasyTextureLoader.blockTextureGetter().apply(resLoc);
		textures = new TextureAtlasSprite[]{null, null, texture, texture, texture, texture};
		return null;
	}

    public static List<BakedQuad> forSide(Direction side) {
        int i = side.ordinal();
        if (SupportRender.bakedQuads[i] == null) {
            SupportRender.bakedQuads[i] = new ArrayList();
            RawQuadCube cube = new RawQuadCube(0.1F, 0.5F - ISECoverPanel.thickness, 0.1F, SupportRender.textures);
            cube.rotateToDirection(side);
            cube.translateCoord(0.5F, 0.5F, 0.5F);
            cube.bake(SupportRender.bakedQuads[i]);
        }

        return SupportRender.bakedQuads[i];
    }
}
