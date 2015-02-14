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

package simElectricity.Client.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import simElectricity.Common.Blocks.WindMill.TileWindMillTop;

@SideOnly(Side.CLIENT)
public class RenderWindMillTop extends TileEntitySpecialRenderer {
    public static float half_thickness = 0.03125F;
    public TileWindMillTop te;

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick, int i) {
        te = (TileWindMillTop) tileEntity;

        if (!te.settled)
            return;

        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y, (float) z);
        renderBlockYour(tileEntity.getWorld(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());

        GL11.glPopMatrix();
    }

    int getDirection() {
        switch (te.getFacing().ordinal()) {
            case 2:
                return 0;
            case 5:
                return 1;
            case 3:
                return 2;
            default:
                return 3;
        }
    }

    public void renderBlockYour(World world, int i, int j, int k) {
        bindTexture(new ResourceLocation("simelectricity", "textures/render/WindMill_Fan.png"));

        GL11.glTranslatef(0.5F, 0, 0.5F);
        GL11.glRotatef(getDirection() * (270F), 0F, 1F, 0F);
        GL11.glTranslatef(0, 0.5F, -0.6F);


        GL11.glRotatef(te.randAngle + (Minecraft.getMinecraft().getSystemTime() % 1800000) / 20, 0.0F, 0.0F, 1.0F);
        draw();
        GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180, 0.0F, 0.0F, 1.0F);
        draw();
    }

    private void render_light(int direction, float center_x, float center_y) {
        // TODO:
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0, 240);
    }

    private void draw() {
        float radius = 10;
        float start_x = -radius / 2.0F;
        float start_y = radius / 2.0F;


        for (float i = 0; i < radius; i++) {
            for (float j = 0; j < radius; j++) {

                float left = start_x + i;
                float right = left + 1;
                float top = start_y - j;
                float bottom = top - 1;

                this.render_light(getDirection(), (right + left) / 2, (bottom + top) / 2);

                float texture_left = i / radius;
                float texture_right = (i + 1) / radius;
                float texture_top = j / radius;
                float texture_bottom = (j + 1) / radius;

                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldRenderer = tessellator.getWorldRenderer();

                worldRenderer.startDrawingQuads();

                worldRenderer.setNormal(0.0F, 0.0F, -1.0F);
                worldRenderer.addVertexWithUV(right, top, half_thickness, texture_right, texture_top);
                worldRenderer.addVertexWithUV(left, top, half_thickness, texture_left, texture_top);
                worldRenderer.addVertexWithUV(left, bottom, half_thickness, texture_left, texture_bottom);
                worldRenderer.addVertexWithUV(right, bottom, half_thickness, texture_right, texture_bottom);


                worldRenderer.setNormal(0.0F, 0.0F, 1.0F);
                worldRenderer.addVertexWithUV(left, top, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, top, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, bottom, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, bottom, -half_thickness, 0, 0);


                worldRenderer.setNormal(0.0F, 1.0F, 0.0F);
                worldRenderer.addVertexWithUV(right, top, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, top, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, top, half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, top, half_thickness, 0, 0);


                worldRenderer.setNormal(0.0F, -1.0F, 0.0F);
                worldRenderer.addVertexWithUV(right, bottom, half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, bottom, half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, bottom, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, bottom, -half_thickness, 0, 0);


                worldRenderer.setNormal(-1.0F, 0.0F, 0.0F);
                worldRenderer.addVertexWithUV(left, top, half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, top, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, bottom, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(left, bottom, half_thickness, 0, 0);


                worldRenderer.setNormal(1.0F, 0.0F, 0.0F);
                worldRenderer.addVertexWithUV(right, top, -half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, top, half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, bottom, half_thickness, 0, 0);
                worldRenderer.addVertexWithUV(right, bottom, -half_thickness, 0, 0);

                tessellator.draw();
            }
        }
    }
}