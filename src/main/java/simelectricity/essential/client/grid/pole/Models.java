package simelectricity.essential.client.grid.pole;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

@OnlyIn(Dist.CLIENT)
public class Models {
	public static RawQuadGroup render10kVInsulator(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
        insulator.add(new RawQuadCube(0.08F, 0.5F, 0.08F, textureMetal));
        insulator.add(new RawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator).translateCoord(0, 0.15F, 0));
        insulator.add(new RawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator).translateCoord(0, 0.225F, 0));
        insulator.add(new RawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator).translateCoord(0, 0.3F, 0));
        return insulator;
	}
	
	public static RawQuadGroup render10kVInsulatorSmall(TextureAtlasSprite textureMetal, TextureAtlasSprite textureCeramic) {
		RawQuadGroup insulator = new RawQuadGroup();
        insulator.add(new RawQuadCube(0.05F, 0.25F, 0.05F, textureMetal));
        insulator.add(new RawQuadCube(0.25F, 0.05F, 0.25F, textureCeramic).translateCoord(0, 0.075F, 0));
        insulator.add(new RawQuadCube(0.15F, 0.05F, 0.15F, textureCeramic).translateCoord(0, 0.15F, 0));
        return insulator;
	}
	
	public static RawQuadGroup render10kVInsulatorTall(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
		insulator.add((new RawQuadCube(0.1F, 0.325F, 0.1F, textureInsulator)));
		insulator.add((new RawQuadCube(0.2F, 0.05F, 0.2F, textureInsulator)).translateCoord(0, 0.05F, 0));
		insulator.add((new RawQuadCube(0.2F, 0.05F, 0.2F, textureInsulator)).translateCoord(0, 0.15F, 0));
		insulator.add((new RawQuadCube(0.2F, 0.05F, 0.2F, textureInsulator)).translateCoord(0, 0.25F, 0));
		insulator.add((new RawQuadCube(0.05F, 0.075F, 0.05F, textureMetal)).translateCoord(0, 0.325F, 0));
        return insulator;
	}
	
	public static RawQuadGroup render415VInsulator(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
        insulator.add(new RawQuadCube(0.08F, 0.25F, 0.08F, textureMetal));
        insulator.add(new RawQuadCube(0.25F, 0.05F, 0.25F, textureInsulator).translateCoord(0, 0.15F, 0));
        return insulator;
	}
	
	public static RawQuadGroup render415VInsulatorTall(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
		insulator.add((new RawQuadCube(0.05F, 0.25F, 0.05F, textureInsulator)));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.025F, 0));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.075F, 0));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.125F, 0));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.175F, 0));
		insulator.add((new RawQuadCube(0.05F, 0.075F, 0.05F, textureMetal)).translateCoord(0, 0.25F, 0));
        return insulator;
	}
	
	public static RawQuadGroup render10kVSwitch(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
		insulator.add((new RawQuadCube(0.075F, 0.425F, 0.075F, textureInsulator)));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.025F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.075F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.125F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.175F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.225F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.275F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.325F, 0));
		insulator.add((new RawQuadCube(0.15F, 0.025F, 0.15F, textureInsulator)).translateCoord(0, 0.375F, 0));
		
		insulator.add((new RawQuadCube(0.2F, 0.025F, 0.05F, textureMetal)).translateCoord(-0.0625F, 0.425F, 0));
		insulator.add((new RawQuadCube(0.2F, 0.025F, 0.05F, textureMetal)).translateCoord(-0.0625F, -0.025F, 0));
		return insulator;
	}
	
	public static RawQuadGroup render415VSwitch(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
		insulator.add((new RawQuadCube(0.05F, 0.2F, 0.05F, textureInsulator)));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.025F, 0));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.075F, 0));
		insulator.add((new RawQuadCube(0.1F, 0.025F, 0.1F, textureInsulator)).translateCoord(0, 0.125F, 0));
		insulator.add((new RawQuadCube(0.05F, 0.05F, 0.025F, textureMetal)).translateCoord(0, -0.05F, 0));
		insulator.merge(insulator.clone().translateCoord(0.3F, 0, 0));
		return insulator;
	}
	
	public static RawQuadGroup render35KvInsulator(TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		RawQuadGroup insulator = new RawQuadGroup();
		RawQuadCube string = new RawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator);

        for (int i = 0; i < 1.4F / 0.1F + 1; i++) {
            insulator.add(string.clone().translateCoord(0, 0.1F * i, 0));
        }
		
        insulator.add((new RawQuadCube(0.1F, 2F, 0.1F, textureMetal)).translateCoord(0F, -0.3F, 0F));
		return insulator.translateCoord(0F, 0.3F, 0F);
	}
	
    public static RawQuadGroup renderInsulatorString(float length, TextureAtlasSprite insulatorTexture) {
    	RawQuadGroup h = new RawQuadGroup();
        RawQuadCube insulator = new RawQuadCube(0.5F, 0.05F, 0.5F, insulatorTexture);

        for (int i = 0; i < length / 0.1 + 1; i++) {
        	h.add(insulator.clone().translateCoord(0, 0.1F * i, 0));
        }
        return h;
    }

    public static SERenderHeap renderTower0Top(TextureAtlasSprite metalTexture) {
        SERenderHeap mainHeap = new SERenderHeap();


        SERenderHeap h1 = new SERenderHeap(); // 90 degrees x n
        SERenderHeap h2 = new SERenderHeap(); // 180 degrees x n

        //Base3 (H)
        float angle1 = -4.4474F;
        double[][] element = SERenderHelper.createCubeVertexes(0.15, 10, 0.15);
        SERenderHelper.rotateAroundX(element, 90);
        SERenderHelper.translateCoord(element, -1, 18, -5);
        h2.addCube(element, metalTexture);

        //Base4
        angle1 = -4.0856F;
        element = SERenderHelper.createCubeVertexes(0.15, 7, 0.15);
        SERenderHelper.rotateAroundZ(element, -angle1);
        SERenderHelper.rotateAroundX(element, -angle1);
        SERenderHelper.translateCoord(element, 1, 18, -1);
        h1.addCube(element, metalTexture);


        //Base5 (H)
        element = SERenderHelper.createCubeVertexes(0.15, 8, 0.15);
        SERenderHelper.rotateAroundX(element, 90);
        SERenderHelper.translateCoord(element, -0.5, 25, -4);
        h2.addCube(element, metalTexture);

        //Support1
        element = SERenderHelper.createCubeVertexes(0.15, 4.4, 0.15);
        SERenderHelper.rotateAroundZ(element, -1.5F);
        SERenderHelper.rotateAroundX(element, 70);
        SERenderHelper.translateCoord(element, -1, 18, -5);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 4.4, 0.15);
        SERenderHelper.rotateAroundZ(element, -1.5F);
        SERenderHelper.rotateAroundX(element, -70);
        SERenderHelper.translateCoord(element, -1, 18, 5);
        h2.addCube(element, metalTexture);

        //Support2
        element = SERenderHelper.createCubeVertexes(0.15, 3.6, 0.15);
        SERenderHelper.rotateAroundZ(element, 1.2F);
        SERenderHelper.rotateAroundX(element, 110);
        SERenderHelper.translateCoord(element, -0.5, 25, -4);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 3.6, 0.15);
        SERenderHelper.rotateAroundZ(element, -1.2F);
        SERenderHelper.rotateAroundX(element, 110);
        SERenderHelper.translateCoord(element, 0.5, 25, -4);
        h2.addCube(element, metalTexture);


        //Details
        //
        element = SERenderHelper.createCubeVertexes(0.15, 2.4, 0.15);
        SERenderHelper.rotateAroundZ(element, -51);
        SERenderHelper.rotateAroundX(element, 4.5F);
        SERenderHelper.translateCoord(element, -1, 18, -1);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 2.4, 0.15);
        SERenderHelper.rotateAroundX(element, 51);
        SERenderHelper.rotateAroundZ(element, -4.5F);
        SERenderHelper.translateCoord(element, -1, 18, -1);
        h1.addCube(element, metalTexture);

        //
        element = SERenderHelper.createCubeVertexes(0.15, 3, 0.15);
        SERenderHelper.rotateAroundZ(element, -30);
        SERenderHelper.rotateAroundX(element, 3.5F);
        SERenderHelper.translateCoord(element, -0.85, 19.5, -0.85);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 3, 0.15);
        SERenderHelper.rotateAroundX(element, 30);
        SERenderHelper.rotateAroundZ(element, -3.5F);
        SERenderHelper.translateCoord(element, -0.85, 19.5, -0.85);
        h1.addCube(element, metalTexture);

        //
        element = SERenderHelper.createCubeVertexes(0.15, 2.2, 0.15);
        SERenderHelper.rotateAroundZ(element, -35);
        SERenderHelper.rotateAroundX(element, 4.5F);
        SERenderHelper.translateCoord(element, -0.7, 22, -0.7);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 2.2, 0.15);
        SERenderHelper.rotateAroundX(element, 35);
        SERenderHelper.rotateAroundZ(element, -4.5F);
        SERenderHelper.translateCoord(element, -0.7, 22, -0.7);
        h1.addCube(element, metalTexture);

        //
        element = SERenderHelper.createCubeVertexes(0.15, 1.5, 0.15);
        SERenderHelper.rotateAroundZ(element, -40);
        SERenderHelper.rotateAroundX(element, 4);
        SERenderHelper.translateCoord(element, -0.57, 23.8, -0.57);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 1.5, 0.15);
        SERenderHelper.rotateAroundX(element, 40);
        SERenderHelper.rotateAroundZ(element, -4);
        SERenderHelper.translateCoord(element, -0.57, 23.8, -0.57);
        h1.addCube(element, metalTexture);


        /////////////////////////////////////////////////////////////////
        //Top
        element = SERenderHelper.createCubeVertexes(0.1, 1.6, 0.1);
        SERenderHelper.rotateAroundX(element, 115);
        SERenderHelper.rotateAroundZ(element, -3.7F);
        SERenderHelper.translateCoord(element, -0.5, 25, 0.5);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 1.6, 0.1);
        SERenderHelper.rotateAroundX(element, -115);
        SERenderHelper.rotateAroundZ(element, -3.7F);
        SERenderHelper.translateCoord(element, -0.5, 25, -0.5);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 0.75, 0.1);
        SERenderHelper.rotateAroundX(element, 180);
        SERenderHelper.rotateAroundZ(element, -3.7F);
        SERenderHelper.translateCoord(element, -0.5, 25, 2);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 0.75, 0.1);
        SERenderHelper.rotateAroundX(element, -180);
        SERenderHelper.rotateAroundZ(element, -3.7F);
        SERenderHelper.translateCoord(element, -0.5, 25, -2);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 1.1, 0.1);
        SERenderHelper.rotateAroundX(element, 180);
        SERenderHelper.rotateAroundZ(element, -4F);
        SERenderHelper.translateCoord(element, -0.91, 19.1, 2);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 1.1, 0.1);
        SERenderHelper.rotateAroundX(element, -180);
        SERenderHelper.rotateAroundZ(element, -4F);
        SERenderHelper.translateCoord(element, -0.91, 19.1, -2);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 1.7, 0.1);
        SERenderHelper.rotateAroundX(element, 70);
        SERenderHelper.rotateAroundZ(element, -4.2F);
        SERenderHelper.translateCoord(element, -1, 18, 1.9);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 1.7, 0.1);
        SERenderHelper.rotateAroundX(element, -70);
        SERenderHelper.rotateAroundZ(element, -4.2F);
        SERenderHelper.translateCoord(element, -1, 18, -1.9);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 3.7, 0.1);
        SERenderHelper.rotateAroundX(element, 74);
        SERenderHelper.rotateAroundZ(element, 90);
        SERenderHelper.translateCoord(element, 0.5, 25, 0.4);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 3.7, 0.1);
        SERenderHelper.rotateAroundX(element, 74);
        SERenderHelper.rotateAroundZ(element, -90);
        SERenderHelper.translateCoord(element, -0.5, 25, 0.4);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 4.4, 0.1);
        SERenderHelper.rotateAroundX(element, 63);
        SERenderHelper.rotateAroundZ(element, 90);
        SERenderHelper.translateCoord(element, 1, 18, 1);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 4.4, 0.1);
        SERenderHelper.rotateAroundX(element, 63);
        SERenderHelper.rotateAroundZ(element, -90);
        SERenderHelper.translateCoord(element, -1, 18, 1);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 1, 0.1);
        SERenderHelper.rotateAroundZ(element, 90);
        SERenderHelper.translateCoord(element, 0.5, 25, 3.95);
        h2.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.1, 2, 0.1);
        SERenderHelper.rotateAroundZ(element, 90);
        SERenderHelper.translateCoord(element, 1, 18, 4.9);
        h2.addCube(element, metalTexture);

        mainHeap.appendHeap(h1);
        mainHeap.appendHeap(h1.clone().rotateAroundVector(90, 0, 1, 0));
        mainHeap.appendHeap(h1.clone().rotateAroundVector(180, 0, 1, 0));
        mainHeap.appendHeap(h1.clone().rotateAroundVector(270, 0, 1, 0));
        mainHeap.appendHeap(h2);
        mainHeap.appendHeap(h2.clone().rotateAroundVector(180, 0, 1, 0));

        return mainHeap;
    }

    public static SERenderHeap renderTower0Bottom(TextureAtlasSprite metalTexture) {
        SERenderHeap h1 = new SERenderHeap(); // 90 degrees x n

        //Ground
        double[][] element = SERenderHelper.createCubeVertexes(0.15, 0.5, 0.15);
        SERenderHelper.rotateAroundZ(element, -90);
        SERenderHelper.translateCoord(element, -2.455, 0.075, -2.385);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 0.5, 0.15);
        SERenderHelper.rotateAroundX(element, 90);
        SERenderHelper.translateCoord(element, -2.385, 0.075, -2.455);
        h1.addCube(element, metalTexture);

        //Base1
        float angle1 = -4.4474F;
        element = SERenderHelper.createCubeVertexes(0.15, 18.06, 0.15);
        SERenderHelper.rotateAroundZ(element, angle1);
        SERenderHelper.rotateAroundX(element, -angle1);
        SERenderHelper.translateCoord(element, -2.4, 0, -2.4);
        h1.addCube(element, metalTexture);

        //Base2 (H)
        element = SERenderHelper.createCubeVertexes(0.15, 4.05, 0.15);
        SERenderHelper.rotateAroundX(element, 90);
        SERenderHelper.translateCoord(element, -2.025, 4.5, -2.025);
        h1.addCube(element, metalTexture);


        //Details
        element = SERenderHelper.createCubeVertexes(0.15, 5.05, 0.15);
        SERenderHelper.rotateAroundZ(element, -28.0725F);
        SERenderHelper.rotateAroundX(element, 4.77F);
        SERenderHelper.translateCoord(element, -2.4, 0.075, -2.4);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 5.05, 0.15);
        SERenderHelper.rotateAroundX(element, 28.0725F);
        SERenderHelper.rotateAroundZ(element, -4.77F);
        SERenderHelper.translateCoord(element, -2.4, 0.075, -2.4);
        h1.addCube(element, metalTexture);

        //
        element = SERenderHelper.createCubeVertexes(0.15, 6.6, 0.15);
        SERenderHelper.rotateAroundZ(element, -33F);
        SERenderHelper.rotateAroundX(element, 4F);
        SERenderHelper.translateCoord(element, -2, 4.5, -2);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 6.6, 0.15);
        SERenderHelper.rotateAroundX(element, 33F);
        SERenderHelper.rotateAroundZ(element, -4F);
        SERenderHelper.translateCoord(element, -2, 4.5, -2);
        h1.addCube(element, metalTexture);


        //
        element = SERenderHelper.createCubeVertexes(0.15, 5.2, 0.15);
        SERenderHelper.rotateAroundZ(element, -33);
        SERenderHelper.rotateAroundX(element, 4.3F);
        SERenderHelper.translateCoord(element, -1.6, 10, -1.6);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 5.2, 0.15);
        SERenderHelper.rotateAroundX(element, 33);
        SERenderHelper.rotateAroundZ(element, -4.3F);
        SERenderHelper.translateCoord(element, -1.6, 10, -1.6);
        h1.addCube(element, metalTexture);

        //////
        element = SERenderHelper.createCubeVertexes(0.15, 4.4, 0.15);
        SERenderHelper.rotateAroundZ(element, -30);
        SERenderHelper.rotateAroundX(element, 4.5F);
        SERenderHelper.translateCoord(element, -1.28, 14.2, -1.28);
        h1.addCube(element, metalTexture);

        element = SERenderHelper.createCubeVertexes(0.15, 4.4, 0.15);
        SERenderHelper.rotateAroundX(element, 30);
        SERenderHelper.rotateAroundZ(element, -4.5F);
        SERenderHelper.translateCoord(element, -1.28, 14.2, -1.28);
        h1.addCube(element, metalTexture);

        return h1;
    }
}
