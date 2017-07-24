package simelectricity.essential.grid.render;

import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModelBaker {
	public ModelBaker(){
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static void renderInsulators(TransmissionTowerRenderHelper helper, int x, int y, int z, int rotation, int lightValue){		
		if (helper.render1())
			renderInsulators(helper.from1(), helper.to1(), helper.angle1(), lightValue);	
		if (helper.render2())
			renderInsulators(helper.from2(), helper.to2(), helper.angle2(), lightValue);			
	}
	
	private static void renderInsulators(double[] from, double[] to, double[] angle, int lightValue){
		for (int i=0; i<3; i++){
			SERenderHeap insulator = modelInsulator.clone();
			
			insulator.rotateAroundZ((float) (angle[i]/Math.PI*180));
			insulator.rotateToVec(from[3*i],from[3*i+1],from[3*i+2], to[3*i],from[3*i+1],to[3*i+2]);
			insulator.transform(from[3*i], from[3*i+1],from[3*i+2]);
			insulator.applyToTessellator(lightValue);	
		}
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	public static IIcon textureInsulator, textureMetal;
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
			textureInsulator = event.map.registerIcon("sime_essential:transmission/glass_insulator");
			textureMetal = event.map.registerIcon("sime_essential:transmission/metal");
		}
	}
	
	///////////////////////////////////
	/// Compile stationary parts
	///////////////////////////////////
	public static SERenderHeap model0;
	public static SERenderHeap model1;
	public static SERenderHeap modelInsulator;
	public static SERenderHeap model2;
	public static SERenderHeap model3;
	
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Post event){
		if (event.map.getTextureType() == 0){
			model0 = simelectricity.essential.grid.render.Models.renderTower0Top(textureMetal);
			
			model1 = model0.clone();
			SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
			double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
			SERenderHelper.translateCoord(rod, 0, -0.15, 0);
			insulator.addCube(rod, textureMetal);
			model1.appendHeap(insulator.clone().transform(0,18-1.85,-4.9));
			model1.appendHeap(insulator.clone().transform(0,18-1.85,4.9));
			model1.appendHeap(insulator.transform(0,23.15,3.95));
		
			modelInsulator = Models.renderInsulatorString(1.4, textureInsulator);
			double[][] rod2 = SERenderHelper.createCubeVertexes(0.1, 2, 0.1);				
			SERenderHelper.translateCoord(rod2, 0, -0.3, 0);
			modelInsulator.addCube(rod2, textureMetal);
			modelInsulator.transform(0, 0.3, 0);
			
			
			
			
			
			model2 = new SERenderHeap();
			double[][] cube = SERenderHelper.createCubeVertexes(0.25, 11, 0.25);
			SERenderHelper.translateCoord(cube, 0, -5.5, 0);
			SERenderHelper.rotateAroundX(cube, 90);
			SERenderHelper.translateCoord(cube, 0.25, 0.125, 0);
			model2.addCube(cube, textureMetal);
			cube = SERenderHelper.createCubeVertexes(0.25, 11, 0.25);
			SERenderHelper.translateCoord(cube, 0, -5.5, 0);
			SERenderHelper.rotateAroundX(cube, 90);
			SERenderHelper.translateCoord(cube, -0.25, 0.125, 0);
			model2.addCube(cube, textureMetal);
			
			model3 = model2.clone();
			insulator = Models.renderInsulatorString(1.4, textureInsulator);
			rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
			SERenderHelper.translateCoord(rod, 0, -0.15, 0);
			insulator.addCube(rod, textureMetal);
			model3.appendHeap(insulator.clone().transform(0, 0.125-1.8, -4.5));
			model3.appendHeap(insulator.clone().transform(0, 0.125-1.8, 0));
			model3.appendHeap(insulator.transform(0, 0.125-1.8, 4.5));
		}
	}
}
