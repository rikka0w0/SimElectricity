/** 
 * Copyright (c) LambdaCraft Modding Team, 2013
 * 鐗堟潈璁稿彲锛歀ambdaCraft 鍒朵綔灏忕粍锛�2013.
 * http://lambdacraft.half-life.cn/
 * 
 * LambdaCraft is open-source. It is distributed under the terms of the
 * LambdaCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 *
 * LambdaCraft鏄畬鍏ㄥ紑婧愮殑銆傚畠鐨勫彂甯冮伒浠庛�LambdaCraft寮�簮鍗忚銆嬨�浣犲厑璁搁槄璇伙紝淇敼浠ュ強璋冭瘯杩愯
 * 婧愪唬鐮侊紝 鐒惰�浣犱笉鍏佽灏嗘簮浠ｇ爜浠ュ彟澶栦换浣曠殑鏂瑰紡鍙戝竷锛岄櫎闈炰綘寰楀埌浜嗙増鏉冩墍鏈夎�鐨勮鍙�
 */
package simElectricity.Client.Render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public abstract class RendererSidedCube extends TileEntitySpecialRenderer {
	/** Get the texture resource for given index*/
	public abstract ResourceLocation getTexture(int index,int side);
	
	//Using the default texture 0
	public void renderCable(Vec3 from,Vec3 to,double thickness) {renderCable(from,to,thickness,0);}
	public void render_cube(double maxX,double maxY,double maxZ) {render_cube(maxX,maxY,maxZ,0);}	
	
	/** Render a cable, from one point to another, with given thickness and texture */
	public void renderCable(Vec3 from,Vec3 to ,double thickness, int textureIndex) {
		double distance=from.distanceTo(to);
		GL11.glRotated(Math.acos((to.yCoord-from.yCoord)/distance)*180/Math.PI,(to.zCoord-from.zCoord)/distance,0,(from.xCoord-to.xCoord)/distance);
		render_cube(thickness,distance,thickness,textureIndex);
	}
	
	/** Render a cube, from current normal, with a given size and texture*/
	public void render_cube(double maxX,double maxY,double maxZ, int textureIndex) {
		Tessellator t = Tessellator.instance;

		Vec3 v1, v2, v3, v4, v5, v6, v7, v8;
		v1 = newV3(-maxX/2, 0, -maxZ/2);
		v2 = newV3(-maxX/2, 0, maxZ/2);
		v3 = newV3(-maxX/2, maxY, maxZ/2);
		v4 = newV3(-maxX/2, maxY, -maxZ/2);

		v5 = newV3(maxX/2, 0, -maxZ/2);
		v6 = newV3(maxX/2, 0, maxZ/2);
		v7 = newV3(maxX/2, maxY, maxZ/2);
		v8 = newV3(maxX/2, maxY, -maxZ/2);

		GL11.glPushMatrix();

		bindTexture(getTexture(textureIndex,4));
		t.startDrawingQuads();
		t.setNormal(-1, 0, 0);
		addVertex(v1, 0, 1);
		addVertex(v2, 1, 1);
		addVertex(v3, 1, 0);
		addVertex(v4, 0, 0);
		t.draw();

		bindTexture(getTexture(textureIndex,5));
		t.startDrawingQuads();
		t.setNormal(1, 0, 0);
		addVertex(v8, 1, 0);
		addVertex(v7, 0, 0);
		addVertex(v6, 0, 1);
		addVertex(v5, 1, 1);
		t.draw();

		bindTexture(getTexture(textureIndex,2));
		t.startDrawingQuads();
		t.setNormal(0, 0, -1);
		addVertex(v4, 1, 0);
		addVertex(v8, 0, 0);
		addVertex(v5, 0, 1);
		addVertex(v1, 1, 1);
		t.draw();

		bindTexture(getTexture(textureIndex,3));
		t.startDrawingQuads();
		t.setNormal(0, 0, 1);
		addVertex(v3, 0, 0);
		addVertex(v2, 0, 1);
		addVertex(v6, 1, 1);
		addVertex(v7, 1, 0);
		t.draw();

		bindTexture(getTexture(textureIndex,1));
		t.startDrawingQuads();
		t.setNormal(0, 1, 0);
		addVertex(v3, 0, 0);
		addVertex(v7, 1, 0);
		addVertex(v8, 1, 1);
		addVertex(v4, 0, 1);
		t.draw();

		bindTexture(getTexture(textureIndex,0));
		t.startDrawingQuads();
		t.setNormal(0, -1, 0);
		addVertex(v1, 0, 1);
		addVertex(v5, 1, 1);
		addVertex(v6, 1, 0);
		addVertex(v2, 0, 0);
		t.draw();

		GL11.glPopMatrix();
	}

	
	public static Vec3 newV3(TileEntity te){
		return Vec3.createVectorHelper(te.xCoord, te.yCoord, te.zCoord);
	}
	
	public static Vec3 newV3(double x, double y, double z) {
		return Vec3.createVectorHelper(x, y, z);
	}
	
	public static void addVertex(Vec3 vec3, double texU, double texV) {
		Tessellator.instance.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord,texU, texV);
	}
}
