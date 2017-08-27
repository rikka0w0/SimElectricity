package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import simelectricity.essential.Essential;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.utils.client.SERawQuadCube;

import java.util.LinkedList;

public class SupportRender {
    private static final SupportRender instance = new SupportRender();
    private TextureAtlasSprite[] textures;
    private final LinkedList<BakedQuad>[] bakedQuads = new LinkedList[6];    //BakedQuads for all 6 directions

    public SupportRender() {
        SupportRender.SupportRender.instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static LinkedList<BakedQuad> forSide(EnumFacing side) {
        int i = side.ordinal();
        if (SupportRender.instance.bakedQuads[i] == null) {
            SupportRender.instance.bakedQuads[i] = new LinkedList();
            SERawQuadCube cube = new SERawQuadCube(0.1F, 0.5F - ISECoverPanel.thickness, 0.1F, SupportRender.instance.textures);
            cube.rotateToDirection(side);
            cube.translateCoord(0.5F, 0.5F, 0.5F);
            cube.bake(SupportRender.instance.bakedQuads[i]);
        }

        return SupportRender.instance.bakedQuads[i];
    }

    @SubscribeEvent
    public void stitcherEventPre(Pre event) {
        //Register textures
        TextureMap map = event.getMap();

        TextureAtlasSprite texture = map.registerSprite(new ResourceLocation(Essential.modID + ":blocks/coverpanel/support"));
        this.textures = new TextureAtlasSprite[]{null, null, texture, texture, texture, texture};
    }
}
