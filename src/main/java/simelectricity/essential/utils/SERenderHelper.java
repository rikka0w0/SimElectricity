package simelectricity.essential.utils;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.Essential;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * double[index][dimension]: each double[index] contains a 3D double array. double[index][0] - xCoord, double[index][1] - yCoord, double[index][2] - zCoord
 * 
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class SERenderHelper {
	public static ResourceLocation createResourceLocation(String path){
		return new ResourceLocation(Essential.modID, path);
	}
	
	public static void rotateUpwardCoordSysTo(ForgeDirection direction){
		switch (direction){
		case DOWN:
			GL11.glRotatef(180, 1, 0, 0);
			return;
		case NORTH:
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glRotatef(180, 0, 1, 0);
			return;
		case SOUTH:
			GL11.glRotatef(90, 1, 0, 0);
			return;
		case WEST:
			GL11.glRotatef(90, 0, 0, 1);
			GL11.glRotatef(270, 0, 1, 0);
			return;
		case EAST:
			GL11.glRotatef(270, 0, 0, 1);
			GL11.glRotatef(90, 0, 1, 0);
			return;
		default:
			return;
		}
    }
	
	
	
	
	
	
    ////////////////////////////////////////////////////////////
    /// Cube-Based model generation, double[8][3] cubeVertexes
    ////////////////////////////////////////////////////////////
	public static double[][] createCubeVertexes(double maxX, double maxY, double maxZ){
		double[][] vertexes = new double[8][];
		double x = maxX / 2.0D;
		double z = maxZ / 2.0D;
		
		//Top
		vertexes[0] = new double[]{x,maxY,x};
		vertexes[1] = new double[]{x,maxY,-x};
		vertexes[2] = new double[]{-x,maxY,-x};
		vertexes[3] = new double[]{-x,maxY,x};
		
		//Bottom
		vertexes[4] = new double[]{x,0,x};
		vertexes[5] = new double[]{x,0,-x};
		vertexes[6] = new double[]{-x,0,-x};
		vertexes[7] = new double[]{-x,0,x};
        
        return vertexes;
	}
	
	public static void addCubeToTessellator(double[][] cubeVertexes, IIcon[] icons, int lightValue){
		addCubeToTessellator(cubeVertexes, icons, new int[]{lightValue, lightValue, lightValue, lightValue, lightValue, lightValue});
	}
	
	public static void addCubeToTessellator(double[][] cubeVertexes, IIcon[] icons, int[] lightValue){
		Tessellator tessellator = Tessellator.instance;    
		
		double uMin, uMax, vMin, vMax;
		
        //Down - Yneg
		if (icons[0] != null){
	        uMin = icons[0].getMinU();
	        uMax = icons[0].getMaxU();
	        vMin = icons[0].getMinV();
	        vMax = icons[0].getMaxV();
	        Tessellator.instance.setBrightness(lightValue[0]);
	        Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			tessellator.addVertexWithUV(cubeVertexes[7][0], cubeVertexes[7][1], cubeVertexes[7][2], uMin, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[6][0], cubeVertexes[6][1], cubeVertexes[6][2], uMin, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[5][0], cubeVertexes[5][1], cubeVertexes[5][2], uMax, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[4][0], cubeVertexes[4][1], cubeVertexes[4][2], uMax, vMax);
		}
        
		//Up - Ypos
		if (icons[1] != null){
	        uMin = icons[1].getMinU();
	        uMax = icons[1].getMaxU();
	        vMin = icons[1].getMinV();
	        vMax = icons[1].getMaxV();
	        Tessellator.instance.setBrightness(lightValue[1]);
	        Tessellator.instance.setColorOpaque_F(1F, 1F, 1F);
			tessellator.addVertexWithUV(cubeVertexes[0][0], cubeVertexes[0][1], cubeVertexes[0][2], uMax, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[1][0], cubeVertexes[1][1], cubeVertexes[1][2], uMax, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[2][0], cubeVertexes[2][1], cubeVertexes[2][2], uMin, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[3][0], cubeVertexes[3][1], cubeVertexes[3][2], uMin, vMax);
		}
        
        //North - Zneg
		if (icons[2] != null){
	        uMin = icons[2].getMinU();
	        uMax = icons[2].getMaxU();
	        vMin = icons[2].getMinV();
	        vMax = icons[2].getMaxV();
	        Tessellator.instance.setBrightness(lightValue[3]);
	        Tessellator.instance.setColorOpaque_F(0.8F, 0.8F, 0.8F);
	        tessellator.addVertexWithUV(cubeVertexes[2][0], cubeVertexes[2][1], cubeVertexes[2][2], uMax, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[1][0], cubeVertexes[1][1], cubeVertexes[1][2], uMin, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[5][0], cubeVertexes[5][1], cubeVertexes[5][2], uMin, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[6][0], cubeVertexes[6][1], cubeVertexes[6][2], uMax, vMax);
		}
        
        //South - Zpos
		if (icons[3] != null){
	        uMin = icons[3].getMinU();
	        uMax = icons[3].getMaxU();
	        vMin = icons[3].getMinV();
	        vMax = icons[3].getMaxV();
	        Tessellator.instance.setBrightness(lightValue[3]);
	        Tessellator.instance.setColorOpaque_F(0.8F, 0.8F, 0.8F);
	        tessellator.addVertexWithUV(cubeVertexes[3][0], cubeVertexes[3][1], cubeVertexes[3][2], uMin, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[7][0], cubeVertexes[7][1], cubeVertexes[7][2], uMin, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[4][0], cubeVertexes[4][1], cubeVertexes[4][2], uMax, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[0][0], cubeVertexes[0][1], cubeVertexes[0][2], uMax, vMin);
		}
	        
        //West - Xneg
		if (icons[4] != null){
	        uMin = icons[4].getMinU();
	        uMax = icons[4].getMaxU();
	        vMin = icons[4].getMinV();
	        vMax = icons[4].getMaxV();
	        Tessellator.instance.setBrightness(lightValue[4]);
	        Tessellator.instance.setColorOpaque_F(0.6F, 0.6F, 0.6F);
	        tessellator.addVertexWithUV(cubeVertexes[3][0], cubeVertexes[3][1], cubeVertexes[3][2], uMax, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[2][0], cubeVertexes[2][1], cubeVertexes[2][2], uMin, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[6][0], cubeVertexes[6][1], cubeVertexes[6][2], uMin, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[7][0], cubeVertexes[7][1], cubeVertexes[7][2], uMax, vMax);
		}

        //East - Xpos
		if (icons[5] != null){
	        uMin = icons[5].getMinU();
	        uMax = icons[5].getMaxU();
	        vMin = icons[5].getMinV();
	        vMax = icons[5].getMaxV();
	        Tessellator.instance.setBrightness(lightValue[5]);
	        Tessellator.instance.setColorOpaque_F(0.6F, 0.6F, 0.6F);
	        tessellator.addVertexWithUV(cubeVertexes[1][0], cubeVertexes[1][1], cubeVertexes[1][2], uMax, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[0][0], cubeVertexes[0][1], cubeVertexes[0][2], uMin, vMin);
	        tessellator.addVertexWithUV(cubeVertexes[4][0], cubeVertexes[4][1], cubeVertexes[4][2], uMin, vMax);
	        tessellator.addVertexWithUV(cubeVertexes[5][0], cubeVertexes[5][1], cubeVertexes[5][2], uMax, vMax);
		}
	}

    public static void rotateCubeToDirection(double[][] cubeVertexes, ForgeDirection direction){
		switch (direction){
		case DOWN:
			SERenderHelper.rotateAroundX(cubeVertexes, 180);
			break;
		case NORTH:
			SERenderHelper.rotateAroundY(cubeVertexes, 180);
			SERenderHelper.rotateAroundX(cubeVertexes, 270);
			break;
		case SOUTH:
			SERenderHelper.rotateAroundX(cubeVertexes, 90);
			break;
		case WEST:
			SERenderHelper.rotateAroundY(cubeVertexes, 270);
			SERenderHelper.rotateAroundZ(cubeVertexes, 90);
			break;
		case EAST:
			SERenderHelper.rotateAroundY(cubeVertexes, 90);
			SERenderHelper.rotateAroundZ(cubeVertexes, 270);
			break;
		default:
			break;
		}
    }

    
    /////////////////////////////////////////////////////////////
    /// Utilities, double[i][3] vertexes, i can be any numbers
    /////////////////////////////////////////////////////////////
    public static double[][] createSafeCopy(double[][] vertexes){
    	double[][] ret = new double[vertexes.length][];
    	for (int i=0; i<vertexes.length; i++){
    		ret[i] = new double[vertexes[i].length];
    		for (int j=0; j<vertexes[i].length; j++)
    			ret[i][j] = vertexes[i][j];
    	}

    	return ret;
    }
    
	public static void translateCoord(double[][] vertexes, double x, double y, double z){
		for (int i=0; i<vertexes.length; i++){
			vertexes[i][0] += x;
			vertexes[i][1] += y;
			vertexes[i][2] += z;
		}
	}
	
	public static void rotateAroundX(double[][] vertexes, float angle){
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        for (int i=0; i<vertexes.length; i++){
	        double d0 = vertexes[i][0];
	        double d1 = vertexes[i][1] * f1 + vertexes[i][2] * f2;
	        double d2 = vertexes[i][2] * f1 - vertexes[i][1] * f2;
	        vertexes[i][0] = d0;
	        vertexes[i][1] = d1;
	        vertexes[i][2] = d2;
        }
	}

	public static void rotateAroundY(double[][] vertexes, float angle){
        float f1 = MathHelper.cos(angle * 0.01745329252F);
        float f2 = MathHelper.sin(angle * 0.01745329252F);
        
        for (int i=0; i<vertexes.length; i++){
            double d0 = vertexes[i][0] * f1 + vertexes[i][2] * f2;
            double d1 = vertexes[i][1];
            double d2 = vertexes[i][2] * f1 - vertexes[i][0] * f2;
            vertexes[i][0] = d0;
            vertexes[i][1] = d1;
            vertexes[i][2] = d2;
        }
	}
	
    public static void rotateAroundZ(double[][] vertexes, float angle)
    {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        
        for (int i=0; i<vertexes.length; i++){
	        double d0 = vertexes[i][0] * f1 + vertexes[i][1] * f2;
	        double d1 = vertexes[i][1] * f1 - vertexes[i][0] * f2;
	        double d2 = vertexes[i][2];
	        vertexes[i][0] = d0;
	        vertexes[i][1] = d1;
	        vertexes[i][2] = d2;
	    }
    }
    
    public static void rotateToVec(double[][] vertexes, double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd){
        double distance = SEMathHelper.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        rotateAroundY(vertexes, (float)(Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        rotateAroundVector(vertexes, (float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);
    }
    
    public static void rotateAroundVector(double[][] vertexes, float angle, double x, double y, double z){
    	//Normalize the axis vector
    	double length = Math.sqrt(x*x + y*y + z*z);
    	x = x/length;
    	y = y/length;
    	z = z/length;
    	
    	angle = angle * 0.01745329252F;	//Cast to radian
    	double cos = MathHelper.cos(angle);
    	double sin = MathHelper.sin(angle);
    	
    	for (int i=0; i<vertexes.length; i++){
    		double d0 = vertexes[i][0]*(cos+x*x*(1-cos)) 	+ vertexes[i][1]*(x*y*(1-cos)-z*sin) 	+ vertexes[i][2]*(x*z*(1-cos)+y*sin);
    		double d1 = vertexes[i][0]*(x*y*(1-cos)+z*sin) 	+ vertexes[i][1]*(cos+y*y*(1-cos)) 		+ vertexes[i][2]*(y*z*(1-cos)-x*sin);
    		double d2 = vertexes[i][0]*(x*z*(1-cos)-y*sin) 	+ vertexes[i][1]*(y*z*(1-cos)+x*sin) 	+ vertexes[i][2]*(cos+z*z*(1-cos));
    		vertexes[i][0] = d0;
    		vertexes[i][1] = d1;
    		vertexes[i][2] = d2;
    	}
    }

    public static IIcon[] createTextureArray(IIcon icon){
    	return new IIcon[]{icon, icon, icon, icon, icon, icon};
    }
}
