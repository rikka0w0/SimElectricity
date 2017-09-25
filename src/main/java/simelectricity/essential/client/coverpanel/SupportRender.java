package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.LinkedList;

public class SupportRender {
    private static TextureAtlasSprite[] textures;
    private final static LinkedList<BakedQuad>[] bakedQuads = new LinkedList[6];    //BakedQuads for all 6 directions

    public static void stitchTexture(TextureMap map) {
        TextureAtlasSprite texture = map.registerSprite(new ResourceLocation(Essential.MODID + ":blocks/coverpanel/support"));
        textures = new TextureAtlasSprite[]{null, null, texture, texture, texture, texture};
    }

    public static LinkedList<BakedQuad> forSide(EnumFacing side) {
        int i = side.ordinal();
        if (SupportRender.bakedQuads[i] == null) {
            SupportRender.bakedQuads[i] = new LinkedList();
            RawQuadCube cube = new RawQuadCube(0.1F, 0.5F - ISECoverPanel.thickness, 0.1F, SupportRender.textures);
            cube.rotateToDirection(side);
            cube.translateCoord(0.5F, 0.5F, 0.5F);
            cube.bake(SupportRender.bakedQuads[i]);
        }

        return SupportRender.bakedQuads[i];
    }
}
