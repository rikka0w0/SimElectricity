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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import simElectricity.API.Client.RenderHVTowerBase;

@SideOnly(Side.CLIENT)
public class RenderTower extends RenderHVTowerBase{
    @Override
    public void bindTexture(int index, int side) {
        switch (index) {
            case 1:
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/CopperCable_Thin_Side.png"));
                return;
            case 2:
                bindTexture(new ResourceLocation("simelectricity", "textures/render/HvInsulator.png"));
                return;
            default:
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/AdjustableResistor_Top.png"));
        }
    }

    @Override
    public void renderTower(TileEntity tileEntity, double x, double y, double z){
        switch (tileEntity.getBlockMetadata()){
        case 0: renderTower0();break;
        case 1: renderTower1();break;
        }
    }
        
    private void renderTower1() {
    	GL11.glTranslated(0, -6, 0);
    	
    	GL11.glPushMatrix();
        render.render_cube(0.3, 8, 0.3);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 6, -1);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.1, 2.5, 0.1);
        GL11.glPopMatrix();
    
        GL11.glPushMatrix();
        GL11.glTranslated(0, 7.5, 0);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.1, 1, 0.1);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 6, -1);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 6, 1.5);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 7.5, 1);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();
    }
    
    private void renderTower0() {
    	GL11.glTranslated(0, -6, 0);
    	
        //Base1
        GL11.glPushMatrix();
        GL11.glTranslated(2, 0, -2);
        GL11.glRotatef(10F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(10F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 7, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-2, 0, 2);
        GL11.glRotatef(350F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(350F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 7, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(2, 0, 2);
        GL11.glRotatef(350F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(10, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 7, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-2, 0, -2);
        GL11.glRotatef(10F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(350, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 7, 0.15);
        GL11.glPopMatrix();


        //Base2 (H)
        GL11.glPushMatrix();
        GL11.glTranslated(-0.8, 6.7, -0.9);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 1.8, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.8, 6.7, -0.9);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 1.8, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.85, 6.7, -0.8);
        GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 1.7, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.85, 6.7, 0.8);
        GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 1.7, 0.15);
        GL11.glPopMatrix();

        //Base3
        GL11.glPushMatrix();
        GL11.glTranslated(-0.8, 6.7, -0.9);
        GL11.glRotatef(340F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(355F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.8, 6.7, -0.9);
        GL11.glRotatef(340F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.85, 6.7, 0.8);
        GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-0.85, 6.7, 0.8);
        GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(355F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        //Horizontal1
        GL11.glPushMatrix();
        GL11.glTranslated(-0.45, 10, -4);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 8, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.45, 10, -4);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 8, 0.15);
        GL11.glPopMatrix();

        //Insulators
        GL11.glPushMatrix();
        GL11.glTranslated(0, 9, 0);
        renderInsulator(8,1);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0, 9, -3);
        renderInsulator(8,1);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0, 9, 3);
        renderInsulator(8,1);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.45, 10, 2);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(167F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 4.2, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.45, 10, -2);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(13F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 4.2, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 10, 2);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(26F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 2.2, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-0.5, 10, 2);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-26F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 2.2, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.45, 10, -3.9);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(26F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 2, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-0.45, 10, -3.9);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-26F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 2, 0.15);
        GL11.glPopMatrix();
    }


}
