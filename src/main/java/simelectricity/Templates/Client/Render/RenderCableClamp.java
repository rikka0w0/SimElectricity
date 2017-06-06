package simelectricity.Templates.Client.Render;

import org.lwjgl.opengl.GL11;

import simelectricity.api.SEAPI;

import net.minecraft.util.ResourceLocation;

public class RenderCableClamp extends RenderTranmissionTowerBase{
	@Override
	public void bindTexture(int index, int side) {
        switch (index) {
        case 1:
            bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/CopperCable_Thin_Side.png"));
            return;
        case 2:
            bindTexture(new ResourceLocation("simelectricity", "textures/render/HvInsulator.png"));
            return;
        case 3:
        	if (side == 5)
        		bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/CopperCable_Thin_Head.png"));
        	else
        		bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/CopperCable_Thin_Side.png"));
        	return;    
        default:
            bindTexture(new ResourceLocation("simelectricity", "textures/blocks/AdjustableResistor_Top.png"));
        }
	}

	@Override
	public void renderInsulator(int meta) {

	}

	@Override
	public void renderTower(int meta) {
    	GL11.glPushMatrix();
        SEAPI.clientRender.renderCube(0.25, 1, 0.25, this, 0);
        GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.3,0.2,0);
		SEAPI.clientRender.renderCube(0.4, 0.6, 0.6, this, 3);
		GL11.glPopMatrix();
		
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.2,0.75,0.2);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(-15, 0, 0, 1);
		GL11.glRotated(45, 1, 0, 0);
		Models.renderInsulator(1, this, 4, 2);
		GL11.glPopMatrix();
		
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.2,0.8,0);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(-25, 0, 0, 1);
		
		Models.renderInsulator(1, this, 4, 2);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.2,0.75,-0.2);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(-15, 0, 0, 1);
		GL11.glRotated(-45, 1, 0, 0);
		Models.renderInsulator(1, this, 4, 2);
		GL11.glPopMatrix();

	}

}
