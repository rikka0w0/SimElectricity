package simelectricity.essential.client.semachine;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.common.semachine.ISESocketProvider;

import java.util.List;

@SideOnly(Side.CLIENT)
public class SocketRender {
    private static TextureAtlasSprite[] icons;

    public SocketRender() {
        SocketRender.icons = new TextureAtlasSprite[ISESocketProvider.numOfSockets];

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void getBaked(List<BakedQuad> list, int[] iconIndex) {
        TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
        for (int side = 0; side < 6; side++) {
            int i = iconIndex[side];
            if (i >= SocketRender.icons.length)
                i = 0;

            textures[side] = i < 0 ? null : SocketRender.icons[i];
        }

        RawQuadCube cube = new RawQuadCube(1.001F, 1.001F, 1.001F, textures);
        cube.translateCoord(0.5F, 0, 0.5F);
        cube.bake(list);
    }

    @SubscribeEvent
    public void stitcherEventPre(Pre event) {
        //Register textures
        TextureMap map = event.getMap();

        for (int i = 0; i < SocketRender.icons.length; i++)
            SocketRender.icons[i] = map.registerSprite(new ResourceLocation(Essential.MODID + ":blocks/sockets/" + i));
    }
}
