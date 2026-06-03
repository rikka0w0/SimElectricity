package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.model.loader.EasyTextureLoader;
import simelectricity.essential.client.IModelBakeHandler;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public enum SupportRender implements IModelBakeHandler {
	INSTANCE;
	
	public final static ResourceLocation resLoc = ResourceLocation.fromNamespaceAndPath(Essential.MODID, "block/coverpanel/support");
    private static TextureAtlasSprite[] textures;
    @SuppressWarnings("unchecked")
	private final static List<BakedQuad>[] bakedQuads = new List[6];    //BakedQuads for all 6 directions

	@Override
	public BakedModel onModelBakeEvent() {
		TextureAtlasSprite texture = EasyTextureLoader.blockTextureGetter().apply(resLoc);
		textures = new TextureAtlasSprite[]{null, null, texture, texture, texture, texture};
		// Invalidate cache on bake
		for (int i = 0; i < 6; i++) {
			bakedQuads[i] = null;
		}
		return null;
	}

    public static List<BakedQuad> forSide(Direction side) {
        int i = side.ordinal();
        if (SupportRender.bakedQuads[i] == null) {
            SupportRender.bakedQuads[i] = new LinkedList<>();
            RawQuadCube cube = new RawQuadCube(0.1F, 0.5F - ISECoverPanel.thickness, 0.1F, SupportRender.textures);
            cube.rotateToDirection(side);
            cube.translateCoord(0.5F, 0.5F, 0.5F);
            cube.bake(SupportRender.bakedQuads[i]);
        }

        return SupportRender.bakedQuads[i];
    }
}
