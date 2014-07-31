package simElectricity.Client.Render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import simElectricity.Common.Blocks.TileEntity.TileTower;

public class RenderTower extends RendererSidedCube{
	@Override
	public ResourceLocation getTexture(int index,int side) {
		switch (index){
		case 1:
			return new ResourceLocation("simelectricity","textures/blocks/Wiring/CopperCable_Thin_Side.png");
		case 2:
			return new ResourceLocation("simelectricity","textures/render/HvInsulator.png");
		default:
			return new ResourceLocation("simelectricity","textures/blocks/AdjustableResistor_Top.png");				
		}
	}
	
	/** Render a cable, from one point to another, with given thickness and texture */
	public void renderCable(int[] coordinates,double thickness, int textureIndex) {
		double distance=TileTower.distanceOf(coordinates);
		GL11.glRotated(Math.acos((coordinates[4]-coordinates[1])/distance)*180/Math.PI,(coordinates[5]-coordinates[2])/distance,0,(coordinates[0]-coordinates[3])/distance);
		render_cube(thickness,distance/2,thickness,textureIndex);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {		
		TileTower tower = (TileTower) tileEntity;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(0.5, -6, 0.5);
		GL11.glRotatef(tower.facing * (90F) + 90F, 0F, 1F, 0F);
		
		GL11.glPushMatrix();
		renderTower();
		GL11.glPopMatrix();		
		GL11.glPopMatrix();
		
		for(int i=0;i<tower.neighborsInfo.length;i+=3){
			TileTower neighbor = (TileTower) tower.getWorldObj().getTileEntity(tower.neighborsInfo[i],tower.neighborsInfo[i+1],tower.neighborsInfo[i+2]);
		
			if (neighbor != null){
				//Mid
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);
				GL11.glTranslated(0.5, 3.5, 0.5);
				
				renderCable(new int[]{
						tower.xCoord,tower.yCoord,tower.zCoord,
						tower.neighborsInfo[i],tower.neighborsInfo[i+1],tower.neighborsInfo[i+2]
				},0.1,1);
				
				GL11.glPopMatrix();		
				
				//Side
				if (neighbor.facing == tower.facing){
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glTranslated(0.5 + (tower.facing==1?0:3), 3.5, 0.5 + (tower.facing==1?3:0));
					
					renderCable(new int[]{
							tower.xCoord + (tower.facing==1?0:3),tower.yCoord,tower.zCoord + (tower.facing==1?3:0),
							tower.neighborsInfo[i] + (tower.facing==1?0:3),tower.neighborsInfo[i+1],tower.neighborsInfo[i+2] + (tower.facing==1?3:0)
					},0.1,1);
					
					GL11.glPopMatrix();	
					
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glTranslated(0.5 + (tower.facing==1?0:-3), 3.5, 0.5 + (tower.facing==1?-3:0));
					
					renderCable(new int[]{
							tower.xCoord + (tower.facing==1?0:-3),tower.yCoord,tower.zCoord + (tower.facing==1?-3:0),
							tower.neighborsInfo[i] + (tower.facing==1?0:-3),tower.neighborsInfo[i+1],tower.neighborsInfo[i+2] + (tower.facing==1?-3:0)
					},0.1,1);
					
					GL11.glPopMatrix();	
				}else{
					int xOffset = 0;
					int zOffset = 0;
					
					if (tower.facing == 0){
						if (neighbor.xCoord>tower.xCoord){
							if (neighbor.zCoord>tower.zCoord)
								zOffset = -3;
							else
								zOffset = 3;
						} else {
							if (neighbor.zCoord>tower.zCoord)
								zOffset = 3;
							else
								zOffset = -3;
						}
					}else{
						if (neighbor.xCoord>tower.xCoord){
							if (neighbor.zCoord>tower.zCoord)
								xOffset = -3;
							else
								xOffset = 3;
						} else {
							if (neighbor.zCoord>tower.zCoord)
								xOffset = 3;
							else
								xOffset = -3;						
						}
					}
					
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glTranslated(0.5 + (tower.facing==1?0:3), 3.5, 0.5 + (tower.facing==1?3:0));
					
					renderCable(new int[]{
							tower.xCoord + (tower.facing==1?0:3),tower.yCoord,tower.zCoord + (tower.facing==1?3:0),
							tower.neighborsInfo[i] + xOffset,tower.neighborsInfo[i+1],tower.neighborsInfo[i+2] + zOffset
					},0.1,1);
					
					GL11.glPopMatrix();	
					
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glTranslated(0.5 + (tower.facing==1?0:-3), 3.5, 0.5 + (tower.facing==1?-3:0));
					
					renderCable(new int[]{
							tower.xCoord + (tower.facing==1?0:-3),tower.yCoord,tower.zCoord + (tower.facing==1?-3:0),
							tower.neighborsInfo[i] - xOffset,tower.neighborsInfo[i+1],tower.neighborsInfo[i+2] - zOffset
					},0.1,1);
					
					GL11.glPopMatrix();
				}
			}
		}
	}

	private void renderTower(){
		//Base1
		GL11.glPushMatrix();
		GL11.glTranslated(2, 0, -2);
		GL11.glRotatef(10F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(10F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,7,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(-2, 0, 2);
		GL11.glRotatef(350F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(350F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,7,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(2, 0, 2);
		GL11.glRotatef(350F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(10, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,7,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(-2, 0, -2);
		GL11.glRotatef(10F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(350, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,7,0.15);
		GL11.glPopMatrix();
		
		
		//Base2 (H)
		GL11.glPushMatrix();
		GL11.glTranslated(-0.8, 6.7, -0.9);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		render_cube(0.15,1.8,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.8, 6.7, -0.9);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		render_cube(0.15,1.8,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.85, 6.7, -0.8);
		GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,1.7,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.85, 6.7, 0.8);
		GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,1.7,0.15);
		GL11.glPopMatrix();
		
		//Base3
		GL11.glPushMatrix();
		GL11.glTranslated(-0.8, 6.7, -0.9);
		GL11.glRotatef(340F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(355F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,5,0.15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslated(0.8, 6.7, -0.9);
		GL11.glRotatef(340F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(5F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,5,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.85, 6.7, 0.8);
		GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(5F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,5,0.15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslated(-0.85, 6.7, 0.8);
		GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(355F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,5,0.15);
		GL11.glPopMatrix();		
		
		//Horizontal1
		GL11.glPushMatrix();
		GL11.glTranslated(-0.45, 10, -4);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		render_cube(0.15,8,0.15);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.45, 10, -4);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		render_cube(0.15,8,0.15);
		GL11.glPopMatrix();
		
		//Insulators
		GL11.glPushMatrix();
		GL11.glTranslated(0, 9.5, 0);
		renderInsulator();
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0, 9.5, -3);
		renderInsulator();
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0, 9.5, 3);
		renderInsulator();
		GL11.glPopMatrix();
	
		GL11.glPushMatrix();
		GL11.glTranslated(0.45, 10, 2);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(167F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,4.2,0.15);
		GL11.glPopMatrix();	

		GL11.glPushMatrix();
		GL11.glTranslated(0.45, 10, -2);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(13F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,4.2,0.15);
		GL11.glPopMatrix();	
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 10, 2);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(26F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,2.2,0.15);
		GL11.glPopMatrix();		
		
		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, 10, 2);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-26F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,2.2,0.15);
		GL11.glPopMatrix();	
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.45, 10, -3.9);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(26F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,2,0.15);
		GL11.glPopMatrix();	
		
		GL11.glPushMatrix();
		GL11.glTranslated(-0.45, 10, -3.9);
		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-26F, 0.0F, 0.0F, 1.0F);
		render_cube(0.15,2,0.15);
		GL11.glPopMatrix();	
	}
	
	void renderInsulator(){
		render_cube(0.1,0.5,0.1);
		GL11.glTranslated(0, 0.1, 0);
		render_cube(0.3,0.04,0.3,2);
		GL11.glTranslated(0, 0.08, 0);
		render_cube(0.3,0.04,0.3,2);
		GL11.glTranslated(0, 0.08, 0);
		render_cube(0.3,0.04,0.3,2);
		GL11.glTranslated(0, 0.08, 0);
		render_cube(0.3,0.04,0.3,2);
	}
}
