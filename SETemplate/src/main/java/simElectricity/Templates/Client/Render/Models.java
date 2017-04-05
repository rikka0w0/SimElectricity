package simElectricity.Templates.Client.Render;

import org.lwjgl.opengl.GL11;

import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITextureProvider;

public class Models {
    public static void renderInsulator(double length, ITextureProvider textureProvider, int textureID1, int textureID2) {
        SEAPI.clientRender.renderCube(0.1, length, 0.1, textureProvider, textureID1);
        
        GL11.glTranslated(0, 0.4, 0);
        SEAPI.clientRender.renderCube(0.5, 0.05, 0.5, textureProvider, textureID2);
        
        for (int i = 0; i< (length - 0.6)/0.1;i++){
        	GL11.glTranslated(0, 0.1, 0);
        	SEAPI.clientRender.renderCube(0.5, 0.05, 0.5, textureProvider, textureID2);
        }
    }
	
	public static void renderTower0(ITextureProvider textureProvider) {
    	//1
        GL11.glPushMatrix();
        GL11.glTranslated(-2.455, 0.075, -2.385);
        GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(-2.385, 0.075, -2.455);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix();
        
        //2
        GL11.glPushMatrix();
        GL11.glTranslated(2.455, 0.075, 2.385);
        GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(2.385, 0.075, 2.455);
        GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix();
        
    	//3
        GL11.glPushMatrix();
        GL11.glTranslated(2.455, 0.075, -2.385);
        GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(2.385, 0.075, -2.455);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix();
        
        //4
        GL11.glPushMatrix();
        GL11.glTranslated(-2.455, 0.075, 2.385);
        GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix(); 	
        
        GL11.glPushMatrix();
        GL11.glTranslated(-2.385, 0.075, 2.455);
        GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 0.5, 0.15, textureProvider, 0);
        GL11.glPopMatrix();
        
        
        
        
    	//GL11.glTranslated(0, -6, 0);
    	
    	float angle1 = 4.4474F;
        //Base1
        GL11.glPushMatrix();
        GL11.glTranslated(2.4, 0, -2.4);
        GL11.glRotatef(angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 18.06, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-2.4, 0, 2.4);
        GL11.glRotatef(360.0F - angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(360.0F - angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 18.06, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(2.4, 0, 2.4);
        GL11.glRotatef(360.0F - angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 18.06, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-2.4, 0, -2.4);
        GL11.glRotatef(angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(360.0F - angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 18.06, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        
        //Base2 (H)
        GL11.glPushMatrix();
        GL11.glTranslated(-2.025, 4.5, -2.025);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 4.05, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(2.025, 4.5, -2.025);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 4.05, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(2.025, 4.5, -2.025);
        GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 4.05, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(2.025, 4.5, 2.025);
        GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 4.05, 0.15, textureProvider, 0);
        GL11.glPopMatrix();


        //Base3 (H)
        GL11.glPushMatrix();
        GL11.glTranslated(-1, 18, -5);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 10, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(1, 18, -5);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 10, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        //Base4
        angle1 = 4.0856F;
        GL11.glPushMatrix();
        GL11.glTranslated(1, 18, -1);
        GL11.glRotatef(angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 7, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-1, 18, 1);
        GL11.glRotatef(360.0F - angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(360.0F - angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 7, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(1, 18, 1);
        GL11.glRotatef(360.0F - angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 7, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-1, 18, -1);
        GL11.glRotatef(angle1, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(360.0F - angle1, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 7, 0.15, textureProvider, 0);
        GL11.glPopMatrix();
        
        //Base5 (H)
        GL11.glPushMatrix();
        GL11.glTranslated(-0.5, 25, -4);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 8, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 25, -4);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        SEAPI.clientRender.renderCube(0.15, 8, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        //Support1
        GL11.glPushMatrix();
        GL11.glTranslated(-1, 18, -5);
        GL11.glRotatef(70F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-1.5F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 4.4, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(1, 18, -5);
        GL11.glRotatef(70F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(1.5F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 4.4, 0.15, textureProvider, 0);
        GL11.glPopMatrix();        
        
        GL11.glPushMatrix();
        GL11.glTranslated(-1, 18, 5);
        GL11.glRotatef(-70F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-1.5F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 4.4, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(1, 18, 5);
        GL11.glRotatef(-70F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(1.5F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 4.4, 0.15, textureProvider, 0);
        GL11.glPopMatrix();    
        
        //Support2
        GL11.glPushMatrix();
        GL11.glTranslated(-0.5, 25, -4);
        GL11.glRotatef(110F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(1.2F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 3.6, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 25, -4);
        GL11.glRotatef(110F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-1.2F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 3.6, 0.15, textureProvider, 0);
        GL11.glPopMatrix();        
        
        GL11.glPushMatrix();
        GL11.glTranslated(-0.5, 25, 4);
        GL11.glRotatef(-110F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(1.2F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 3.6, 0.15, textureProvider, 0);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 25, 4);
        GL11.glRotatef(-110F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-1.2F, 0.0F, 0.0F, 1.0F);
        SEAPI.clientRender.renderCube(0.15, 3.6, 0.15, textureProvider, 0);
        GL11.glPopMatrix(); 
        
        
        //Details
        for (int a =0;a<4;a++){
        	GL11.glPushMatrix();
        	GL11.glRotatef(a*90F, 0.0F, 1.0F, 0.0F);
        	
        	//
            GL11.glPushMatrix();
            GL11.glTranslated(-2.4, 0.075, -2.4);
            GL11.glRotatef(4.77F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-28.0725F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 5.05, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-2.4, 0.075, -2.4);
            GL11.glRotatef(-4.77F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(28.0725F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 5.05, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
        	
            //
            GL11.glPushMatrix();
            GL11.glTranslated(-2, 4.5, -2);
            GL11.glRotatef(4F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-33F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 6.6, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-2, 4.5, -2);
            GL11.glRotatef(-4F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(33F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 6.6, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
            
            //
            GL11.glPushMatrix();
            GL11.glTranslated(-1.6, 10, -1.6);
            GL11.glRotatef(4.3F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-33F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 5.2, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-1.6, 10, -1.6);
            GL11.glRotatef(-4.3F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(33F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 5.2, 0.15, textureProvider, 0);
            GL11.glPopMatrix();

            //
            GL11.glPushMatrix();
            GL11.glTranslated(-1.28, 14.2, -1.28);
            GL11.glRotatef(4.5F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-30F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 4.4, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-1.28, 14.2, -1.28);
            GL11.glRotatef(-4.5F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(30F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 4.4, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
            
            //
            GL11.glPushMatrix();
            GL11.glTranslated(-1, 18, -1);
            GL11.glRotatef(4.5F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-51F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 2.4, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-1, 18, -1);
            GL11.glRotatef(-4.5F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(51F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 2.4, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
            
            //
            GL11.glPushMatrix();
            GL11.glTranslated(-0.85, 19.5, -0.85);
            GL11.glRotatef(3.5F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-30F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 3, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.85, 19.5, -0.85);
            GL11.glRotatef(-3.5F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(30F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 3, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
            
            //
            GL11.glPushMatrix();
            GL11.glTranslated(-0.7, 22, -0.7);
            GL11.glRotatef(4.5F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-35F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 2.2, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.7, 22, -0.7);
            GL11.glRotatef(-4.5F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(35F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 2.2, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
            
            //
            GL11.glPushMatrix();
            GL11.glTranslated(-0.57, 23.8, -0.57);
            GL11.glRotatef(4F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-40F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.15, 1.5, 0.15, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.57, 23.8, -0.57);
            GL11.glRotatef(-4F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(40F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.15, 1.5, 0.15, textureProvider, 0);
            GL11.glPopMatrix();
            
        	GL11.glPopMatrix(); 
        }

        for (int a =0;a<2;a++){
        	GL11.glPushMatrix();
        	GL11.glRotatef(a*180F, 0.0F, 1.0F, 0.0F);
        	
        	//Top
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5, 25, 0.5);
            GL11.glRotatef(-3.7F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(115F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 1.6, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5, 25, -0.5);
            GL11.glRotatef(-3.7F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-115F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 1.6, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5, 25, 2);
            GL11.glRotatef(-3.7F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 0.75, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5, 25, -2);
            GL11.glRotatef(-3.7F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-180F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 0.75, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.91, 19.1, 2);
            GL11.glRotatef(-4F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 1.1, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.91, 19.1, -2);
            GL11.glRotatef(-4F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-180F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 1.1, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            
            GL11.glPushMatrix();
            GL11.glTranslated(-1, 18, 1.9);
            GL11.glRotatef(-4.2F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(70F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 1.7, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-1, 18, -1.9);
            GL11.glRotatef(-4.2F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-70F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 1.7, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            
            
            
            
            
            
            GL11.glPushMatrix();
            GL11.glTranslated(0.5, 25, 0.4);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(74F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 3.7, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5, 25, 0.4);
            GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(74F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 3.7, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(0.6, 23.8, 0.5);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(72.8F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-18F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.1, 3.8, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.6, 23.8, 0.5);
            GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(72.8F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(18F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.1, 3.8, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            //  /---\
            GL11.glPushMatrix();
            GL11.glTranslated(0.85, 19.5, 0.8);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(66F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(18F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.1, 4.7, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-0.85, 19.5, 0.8);
            GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(66F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-18F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.1, 4.7, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            GL11.glPushMatrix();
            GL11.glTranslated(1, 18, 1);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(63F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 4.4, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 	
            
            GL11.glPushMatrix();
            GL11.glTranslated(-1, 18, 1);
            GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(63F, 1.0F, 0.0F, 0.0F);
            SEAPI.clientRender.renderCube(0.1, 4.4, 0.1, textureProvider, 0);
            GL11.glPopMatrix();
            
            
            
            GL11.glPushMatrix();
            GL11.glTranslated(0.5, 25, 3.95);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.1, 1, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 
            
            
            
            GL11.glPushMatrix();
            GL11.glTranslated(1, 18, 4.9);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            SEAPI.clientRender.renderCube(0.1, 2, 0.1, textureProvider, 0);
            GL11.glPopMatrix(); 
            
        	GL11.glPopMatrix(); 
        }
	}
}
