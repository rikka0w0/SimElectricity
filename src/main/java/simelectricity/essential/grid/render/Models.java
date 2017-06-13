package simelectricity.essential.grid.render;

import org.lwjgl.opengl.GL11;

import simelectricity.api.SEAPI;
import simelectricity.api.client.ITextureProvider;
import simelectricity.essential.utils.SERenderHelper;

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
}
