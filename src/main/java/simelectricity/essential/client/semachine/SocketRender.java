package simelectricity.essential.client.semachine;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.IModelBakeHandler;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.Essential;
import simelectricity.essential.common.semachine.ISESocketProvider;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public enum SocketRender implements IModelBakeHandler{
	INSTANCE;

	private final static int numOfSockets = 5;
    private final static TextureAtlasSprite[] icons = new TextureAtlasSprite[SocketRender.numOfSockets];
    private final static ResourceLocation[] iconsLoc = new ResourceLocation[SocketRender.numOfSockets];

    public static void getBaked(List<BakedQuad> list, ISESocketProvider sp) {
        TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
        for (int side = 0; side < 6; side++) {
            int i = sp.getSocketIconIndex(Direction.from3DDataValue(side));
            if (i >= SocketRender.icons.length)
                i = 0;

            textures[side] = i < 0 ? null : SocketRender.icons[i];
        }

        RawQuadCube cube = new RawQuadCube(1.001F, 1.001F, 1.001F, textures);
        cube.translateCoord(0.5F, -0.0005F, 0.5F);
        cube.bake(list);
    }

	@Override
	public void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
		if (!EasyTextureLoader.isBlockAtlas(event))
			return;

        //Register textures
        for (int i = 0; i < SocketRender.icons.length; i++) {
        	SocketRender.iconsLoc[i] = new ResourceLocation(Essential.MODID + ":block/sockets/" + i);
        	event.addSprite(SocketRender.iconsLoc[i]);
        }
	}

	@Override
	public BakedModel onModelBakeEvent() {
		for (int i = 0; i < SocketRender.icons.length; i++) {
			SocketRender.icons[i] = EasyTextureLoader.blockTextureGetter().apply(SocketRender.iconsLoc[i]);
		}
		return null;
	}
}
