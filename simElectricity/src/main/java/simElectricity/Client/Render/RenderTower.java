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

import simElectricity.Common.Blocks.TileEntity.TileCableClamp;
import simElectricity.Common.Blocks.TileEntity.TileTower;

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
        case 2: renderTower2();break;
        }
    }
    
    @Override
    public void renderCable(TileEntity tileEntity, double x, double y, double z){
    	super.renderCable(tileEntity,x,y,z);
        
    	TileEntity neighbor;
    	if (tileEntity.getBlockMetadata() == 1){ 
    		neighbor = tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord,tileEntity.yCoord - 2,tileEntity.zCoord);
        	if (neighbor instanceof TileTower && neighbor.getBlockMetadata() == 2 ||
        		neighbor instanceof TileCableClamp)
        		renderCableTo(tileEntity, neighbor, x, y, z, 0.4);
        }   	
    	if (tileEntity.getBlockMetadata() == 2){
    		neighbor = tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord,tileEntity.yCoord + 2,tileEntity.zCoord);
        	if (neighbor instanceof TileTower && neighbor.getBlockMetadata() == 1)
        		renderCableTo(tileEntity, neighbor, x, y, z, 0.4);		
    	}
    }
        
    private void renderTower2() {
    	GL11.glPushMatrix();
        render.render_cube(0.25, 1, 0.25);
        GL11.glPopMatrix();
    	
    	GL11.glTranslated(0.2, 0.6, -1.5);
    	
    	GL11.glPushMatrix();
    	GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.2, 3, 0.2);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();   
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 3);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();  
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 2);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();  
    }
    
    private void renderTower1() {
    	GL11.glTranslated(0, -8, 0);
    	
    	GL11.glPushMatrix();
    	GL11.glTranslated(0, 8, 0);
        render.render_cube(0.25, 2, 0.25);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 8, -1);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.1, 2.5, 0.1);
        GL11.glPopMatrix();
    
        GL11.glPushMatrix();
        GL11.glTranslated(0, 9.5, 0);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.1, 1, 0.1);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 8, -1);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 8, 1.5);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(0, 9.5, 1);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        renderInsulator(4,0.6);
        GL11.glPopMatrix();
    }
    
    private void renderTower0() {
    	//1
        GL11.glPushMatrix();
        GL11.glTranslated(-2.455, -9.85, -2.385);
        GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(-2.385, -9.85, -2.455);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix();
        
        //2
        GL11.glPushMatrix();
        GL11.glTranslated(2.455, -9.85, 2.385);
        GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(2.385, -9.85, 2.455);
        GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix();
        
    	//3
        GL11.glPushMatrix();
        GL11.glTranslated(2.455, -9.85, -2.385);
        GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(2.385, -9.85, -2.455);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix();
        
        //4
        GL11.glPushMatrix();
        GL11.glTranslated(-2.455, -9.85, 2.385);
        GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(-2.385, -9.85, 2.455);
        GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 0.5, 0.15);
        GL11.glPopMatrix();
        
        
        //1
        GL11.glPushMatrix();
        GL11.glTranslated(-2.4, -9.85, -2.385);
        GL11.glRotatef(-24F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(7.8F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(-2.385, -9.85, -2.4);
        GL11.glRotatef(24F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-7.8F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        //2
        GL11.glPushMatrix();
        GL11.glTranslated(2.4, -9.85, 2.385);
        GL11.glRotatef(24F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-7.8F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(2.385, -9.85, 2.4);
        GL11.glRotatef(-24F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(7.8F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        
        //3
        GL11.glPushMatrix();
        GL11.glTranslated(2.4, -9.85, -2.385);
        GL11.glRotatef(24F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(7.8F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(2.385, -9.85, -2.4);
        GL11.glRotatef(24F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(7.8F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        //4
        GL11.glPushMatrix();
        GL11.glTranslated(-2.4, -9.85, 2.385);
        GL11.glRotatef(-24F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-7.8F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(-2.385, -9.85, 2.4);
        GL11.glRotatef(-24F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-7.8F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 8.8, 0.15);
        GL11.glPopMatrix(); 
               
        
        //1
        GL11.glPushMatrix();
        GL11.glTranslated(-1.18, -1.8, -1.18);
        GL11.glRotatef(-38F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(7F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(-1.18, -1.8, -1.18);
        GL11.glRotatef(38F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-7F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix();
        
        //2
        GL11.glPushMatrix();
        GL11.glTranslated(-1.18, -1.8, 1.18);
        GL11.glRotatef(-38F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-7F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(-1.18, -1.8, 1.18);
        GL11.glRotatef(-38F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-7F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix();
        
        //3
        GL11.glPushMatrix();
        GL11.glTranslated(1.18, -1.8, 1.18);
        GL11.glRotatef(38F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-7F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(1.18, -1.8, 1.18);
        GL11.glRotatef(-38F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(7F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix();
        
        //4
        GL11.glPushMatrix();
        GL11.glTranslated(1.18, -1.8, -1.18);
        GL11.glRotatef(38F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(7F, 1.0F, 0.0F, 0.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix(); 
        
        GL11.glPushMatrix();
        GL11.glTranslated(1.18, -1.8, -1.18);
        GL11.glRotatef(38F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(7F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 3.3, 0.15);
        GL11.glPopMatrix();
        
        
    	GL11.glTranslated(0, -6, 0);
    	
        //Base1
        GL11.glPushMatrix();
        GL11.glTranslated(2.4, -3.9, -2.4);
        GL11.glRotatef(8.5F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(8.5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 10.9, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-2.4, -3.9, 2.4);
        GL11.glRotatef(351.5F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(351.5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 10.9, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(2.4, -3.9, 2.4);
        GL11.glRotatef(351.5F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(8.5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 10.9, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-2.4, -3.9, -2.4);
        GL11.glRotatef(8.5F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(351.5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 10.9, 0.15);
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
        GL11.glTranslated(-0.8, 6.7, -0.8);
        GL11.glRotatef(340F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(355F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.8, 6.7, -0.8);
        GL11.glRotatef(340F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.8, 6.7, 0.8);
        GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(5F, 0.0F, 0.0F, 1.0F);
        render.render_cube(0.15, 5, 0.15);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-0.8, 6.7, 0.8);
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
