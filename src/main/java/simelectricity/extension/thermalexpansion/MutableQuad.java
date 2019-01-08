package simelectricity.extension.thermalexpansion;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.BakedQuadHelper;

/** Modified from BuildCraft source code */
@SideOnly(Side.CLIENT)
public class MutableQuad {
    public final MutableVertex vertex_0 = new MutableVertex();
    public final MutableVertex vertex_1 = new MutableVertex();
    public final MutableVertex vertex_2 = new MutableVertex();
    public final MutableVertex vertex_3 = new MutableVertex();

    private int tintIndex = -1;
    private EnumFacing face = null;
    private boolean shade = false;
    private TextureAtlasSprite sprite = null;

    public MutableQuad fromBakedItem(BakedQuad quad) {
        tintIndex = quad.getTintIndex();
        face = quad.getFace();
        sprite = quad.getSprite();
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
        int[] data = new int[28];
        vertex_0.toBakedItem(data, 0);
        vertex_1.toBakedItem(data, 7);
        vertex_2.toBakedItem(data, 14);
        vertex_3.toBakedItem(data, 21);

        // Rikka's Patch
        // Fix normal vector
        int normal= BakedQuadHelper.calculatePackedNormal(
                vertex_0.position_x, vertex_0.position_y, vertex_0.position_z,
                vertex_1.position_x, vertex_1.position_y, vertex_1.position_z,
                vertex_2.position_x, vertex_2.position_y, vertex_2.position_z,
                vertex_3.position_x, vertex_3.position_y, vertex_3.position_z
                );
        data[6] = normal;
        data[13] = normal;
        data[20] = normal;
        data[27] = normal;

        return new BakedQuad(data, tintIndex, face, sprite, shade, DefaultVertexFormats.ITEM);
    }
}
