/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simelectricity.Templates.Client.Render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import simelectricity.Templates.Blocks.BlockWire;
import simelectricity.Templates.TileEntity.TileWire;

@Deprecated
@SideOnly(Side.CLIENT)
public class RenderWire extends TileEntitySpecialRenderer {
    public float WIDTH = 0.2F;
    public String textureString = "";

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        TileWire wire = (TileWire) tileEntity;
        WIDTH = wire.width;
        textureString = wire.textureString;

        Tessellator t = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        renderWireBox(t, -1, wire.getConnections());
        for (int i = 0; i < 6; i++) {
            if (wire.getConnections()[i])
                renderWireBox(t, i, wire.getConnections());
        }
        GL11.glPopMatrix();
    }

    private void renderWireBox(Tessellator t, int side, boolean[] sideArr) {
        ForgeDirection[] dirs = ForgeDirection.values();
        Vec3
                v1 = Vec3.createVectorHelper(-WIDTH, -WIDTH, -WIDTH),
                v2 = Vec3.createVectorHelper(WIDTH, -WIDTH, -WIDTH),
                v3 = Vec3.createVectorHelper(WIDTH, -WIDTH, WIDTH),
                v4 = Vec3.createVectorHelper(-WIDTH, -WIDTH, WIDTH),
                v5 = Vec3.createVectorHelper(-WIDTH, WIDTH, -WIDTH),
                v6 = Vec3.createVectorHelper(WIDTH, WIDTH, -WIDTH),
                v7 = Vec3.createVectorHelper(WIDTH, WIDTH, WIDTH),
                v8 = Vec3.createVectorHelper(-WIDTH, WIDTH, WIDTH);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        float dx = 0.0F, dy = 0.0F, dz = 0.0F;
        switch (side) {
            case 0:
                dy = -1;
                break;
            case 1:
                dy = 1;
                break;
            case 4:
                dx = -1;
                break;
            case 5:
                dx = 1;
                break;
            case 2:
                dz = -1;
                break;
            case 3:
                dz = 1;
                break;
        }

        float offset = (0.5F + WIDTH) / 2F;
        GL11.glTranslatef(dx * offset, dy * offset, dz * offset);
        if (side != -1) {
            float scale = 2F;

            if (WIDTH == BlockWire.renderingWidthList[1])
                scale = 0.75F;
            else if (WIDTH == BlockWire.renderingWidthList[2])
                scale = 0.34F;

            GL11.glScalef(Math.abs(dx == 0.0F ? 1 : dx * scale), Math.abs(dy == 0.0F ? 1 : dy * scale), Math.abs(dz == 0.0F ? 1 : dz * scale));
        }

        int a = 0;
        for (boolean i : sideArr)
            if (i)
                a++;

        for (int i = 0; i < 6; i++) {
            if (!doesRenderSide(side, i, sideArr))
                continue;
            Vec3 vec1 = null, vec2 = null, vec3 = null, vec4 = null;
            dx = 0.0F;
            dy = 0.0F;
            dz = 0.0F;
            switch (i) {
                case 0:
                    vec1 = v4;
                    vec2 = v3;
                    vec3 = v2;
                    vec4 = v1;
                    dy = -1.0F;
                    break;
                case 1:
                    vec1 = v5;
                    vec2 = v6;
                    vec3 = v7;
                    vec4 = v8;
                    dy = 1.0F;
                    break;
                case 4:
                    vec1 = v1;
                    vec2 = v5;
                    vec3 = v8;
                    vec4 = v4;
                    dx = -1.0F;
                    break;
                case 5:
                    vec1 = v2;
                    vec2 = v3;
                    vec3 = v7;
                    vec4 = v6;
                    dx = 1.0F;
                    break;
                case 2:
                    vec1 = v1;
                    vec2 = v2;
                    vec3 = v6;
                    vec4 = v5;
                    dz = -1.0F;
                    break;
                case 3:
                    vec1 = v4;
                    vec2 = v8;
                    vec3 = v7;
                    vec4 = v3;
                    dz = 1.0F;
                    break;
            }
            GL11.glPushMatrix();
            if (side == -1) {
                if (a == 1 && sideArr[dirs[i].getOpposite().ordinal()])
                    bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/" + textureString + "_Head.png"));
                else
                    bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/" + textureString + "_Side.png"));
            } else {
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/" + textureString + "_Side2.png"));
            }

            t.startDrawingQuads();
            t.setNormal(dx, dy, dz);

            addVertex(t, vec4, 0.0, 1.0);
            addVertex(t, vec3, 1.0, 1.0);
            addVertex(t, vec2, 1.0, 0.0);
            addVertex(t, vec1, 0.0, 0.0);
            t.draw();
            GL11.glPopMatrix();
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    void addVertex(Tessellator t, Vec3 vec3, double texU, double texV) {
        t.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, texU, texV);
    }

    private static boolean doesRenderSide(int blockSide, int subSide, boolean[] sideArr) {
        return blockSide != -1 || !sideArr[subSide];
    }
}
