package simelectricity.essential.client.coverpanel;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.model.quadbuilder.BakedQuadHelper;

/** Modified from BuildCraft source code */
@OnlyIn(Dist.CLIENT)
public class MutableQuad {
    public final MutableVertex vertex_0 = new MutableVertex();
    public final MutableVertex vertex_1 = new MutableVertex();
    public final MutableVertex vertex_2 = new MutableVertex();
    public final MutableVertex vertex_3 = new MutableVertex();

    private int tintIndex = -1;
    private Direction face = null;
    private boolean shade = false;
    private TextureAtlasSprite sprite = null;

    public MutableQuad fromBakedItem(BakedQuad quad) {
        tintIndex = quad.getTintIndex();
        face = quad.getFace();
        sprite = quad.func_187508_a();
        shade = quad.shouldApplyDiffuseLighting();

        int[] data = quad.getVertexData();
        int stride = data.length / 4;

        vertex_0.fromBakedItem(data, 0);
        vertex_1.fromBakedItem(data, stride);
        vertex_2.fromBakedItem(data, stride * 2);
        vertex_3.fromBakedItem(data, stride * 3);

        return this;
    }

    public int getTint() {
        return tintIndex;
    }

    public MutableQuad setTint(int tint) {
        tintIndex = tint;
        return this;
    }

    public BakedQuad toBakedItem() {
        int[] data = new int[32];
        vertex_0.toBakedItem(data, 0);
        vertex_1.toBakedItem(data, 8);
        vertex_2.toBakedItem(data, 16);
        vertex_3.toBakedItem(data, 24);

        // Rikka's Patch
        // Fix normal vector
        int normal= BakedQuadHelper.calculatePackedNormal(
                vertex_0.position_x, vertex_0.position_y, vertex_0.position_z,
                vertex_1.position_x, vertex_1.position_y, vertex_1.position_z,
                vertex_2.position_x, vertex_2.position_y, vertex_2.position_z,
                vertex_3.position_x, vertex_3.position_y, vertex_3.position_z
                );
        data[7] = normal;
        data[15] = normal;
        data[23] = normal;
        data[31] = normal;

        return new BakedQuad(data, tintIndex, face, sprite, shade);
    }
}
