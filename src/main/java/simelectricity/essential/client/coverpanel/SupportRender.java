package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.ArrayList;
import java.util.List;

public class SupportRender {
    private static TextureAtlasSprite[] textures;
    private final static List<BakedQuad>[] bakedQuads = new List[6];    //BakedQuads for all 6 directions

    public static void stitchTexture(TextureMap map) {
        TextureAtlasSprite texture = map.registerSprite(new ResourceLocation(Essential.MODID + ":blocks/coverpanel/support"));
        textures = new TextureAtlasSprite[]{null, null, texture, texture, texture, texture};
    }

    public static List<BakedQuad> forSide(EnumFacing side) {
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
