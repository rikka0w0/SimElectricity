package simelectricity.essential.utils.client;

import java.awt.Color;
import java.util.List;

import com.google.common.primitives.Ints;

import simelectricity.essential.utils.SEMathHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

/**
 * Represent a 'raw' cube
 * @author Rikka0_0
 */
public class SERawQuadCube implements ISERawElement<SERawQuadCube>{
	private final float[][] vertexes;
	private final TextureAtlasSprite[] icons;
	
	public SERawQuadCube(float maxX, float maxY, float maxZ, TextureAtlasSprite icon){
		this(maxX, maxY, maxZ, new TextureAtlasSprite[]{icon, icon, icon, icon, icon, icon});
	}
	
	public SERawQuadCube(float maxX, float maxY, float maxZ, TextureAtlasSprite[] icons){
		this.icons = icons;
		this.vertexes = new float[8][];
		float x = maxX / 2.0F;
		float z = maxZ / 2.0F;
		
		//Top
		vertexes[0] = new float[]{x,maxY,z};
		vertexes[1] = new float[]{x,maxY,-z};
		vertexes[2] = new float[]{-x,maxY,-z};
		vertexes[3] = new float[]{-x,maxY,z};
		
		//Bottom
		vertexes[4] = new float[]{x,0,z};
		vertexes[5] = new float[]{x,0,-z};
		vertexes[6] = new float[]{-x,0,-z};
		vertexes[7] = new float[]{-x,0,z};
	}
	
	@Deprecated
	public SERawQuadCube(double[][] vertexes, TextureAtlasSprite[] icons){
		this.icons = icons;
		this.vertexes = new float[vertexes.length][];
		
    	for (int i=0; i<vertexes.length; i++){
    		this.vertexes[i] = new float[vertexes[i].length];
    		for (int j=0; j<vertexes[i].length; j++)
    			this.vertexes[i][j] = (float) vertexes[i][j];
    	}
	}
	
	public SERawQuadCube(float[][] vertexes, TextureAtlasSprite[] icons){
		this.icons = icons;
		this.vertexes = new float[vertexes.length][];
		
    	for (int i=0; i<vertexes.length; i++){
    		this.vertexes[i] = new float[vertexes[i].length];
    		for (int j=0; j<vertexes[i].length; j++)
    			this.vertexes[i][j] = vertexes[i][j];
    	}
	}
	
	@Override
	public SERawQuadCube clone(){
		return new SERawQuadCube(vertexes, icons);
	}
	
	@Override
	public void translateCoord(float x, float y, float z){
		for (int i=0; i<vertexes.length; i++){
			vertexes[i][0] += x;
			vertexes[i][1] += y;
			vertexes[i][2] += z;
		}			
	}

	@Override
	public void rotateAroundX(float angle){
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        for (int i=0; i<vertexes.length; i++){
        	float d0 = vertexes[i][0];
        	float d1 = vertexes[i][1] * f1 + vertexes[i][2] * f2;
        	float d2 = vertexes[i][2] * f1 - vertexes[i][1] * f2;
	        vertexes[i][0] = d0;
	        vertexes[i][1] = d1;
	        vertexes[i][2] = d2;
        }
	}

	@Override
	public void rotateAroundY(float angle){
        float f1 = MathHelper.cos(angle * 0.01745329252F);
        float f2 = MathHelper.sin(angle * 0.01745329252F);
        
        for (int i=0; i<vertexes.length; i++){
        	float d0 = vertexes[i][0] * f1 + vertexes[i][2] * f2;
        	float d1 = vertexes[i][1];
        	float d2 = vertexes[i][2] * f1 - vertexes[i][0] * f2;
            vertexes[i][0] = d0;
            vertexes[i][1] = d1;
            vertexes[i][2] = d2;
        }
	}
	
	@Override
    public void rotateAroundZ(float angle)
    {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        
        for (int i=0; i<vertexes.length; i++){
        	float d0 = vertexes[i][0] * f1 + vertexes[i][1] * f2;
        	float d1 = vertexes[i][1] * f1 - vertexes[i][0] * f2;
        	float d2 = vertexes[i][2];
	        vertexes[i][0] = d0;
	        vertexes[i][1] = d1;
	        vertexes[i][2] = d2;
	    }
    }

	@Override
    public void rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd){
    	float distance = SEMathHelper.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        rotateAroundY((float)(Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        rotateAroundVector((float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);
    }
    
	@Override
    public void rotateToDirection(EnumFacing direction){
		switch (direction){
		case DOWN:
			rotateAroundX(180);
			break;
		case NORTH:
			rotateAroundY(180);
			rotateAroundX(270);
			break;
		case SOUTH:
			rotateAroundX(90);
			break;
		case WEST:
			rotateAroundY(270);
			rotateAroundZ(90);
			break;
		case EAST:
			rotateAroundY(90);
			rotateAroundZ(270);
			break;
		default:
			break;
		}
    }
	
	@Override
    public void rotateAroundVector(float angle, float x, float y, float z){
    	//Normalize the axis vector
    	float length = MathHelper.sqrt(x*x + y*y + z*z);
    	x = x/length;
    	y = y/length;
    	z = z/length;
    	
    	angle = angle * 0.01745329252F;	//Cast to radian
    	float cos = MathHelper.cos(angle);
    	float sin = MathHelper.sin(angle);
    	
    	for (int i=0; i<vertexes.length; i++){
    		float d0 = vertexes[i][0]*(cos+x*x*(1-cos)) 	+ vertexes[i][1]*(x*y*(1-cos)-z*sin) 	+ vertexes[i][2]*(x*z*(1-cos)+y*sin);
    		float d1 = vertexes[i][0]*(x*y*(1-cos)+z*sin) 	+ vertexes[i][1]*(cos+y*y*(1-cos)) 		+ vertexes[i][2]*(y*z*(1-cos)-x*sin);
    		float d2 = vertexes[i][0]*(x*z*(1-cos)-y*sin) 	+ vertexes[i][1]*(y*z*(1-cos)+x*sin) 	+ vertexes[i][2]*(cos+z*z*(1-cos));
    		vertexes[i][0] = d0;
    		vertexes[i][1] = d1;
    		vertexes[i][2] = d2;
    	}
    }

	@Override
    public void bake(List<BakedQuad> list){
    	float uMin, uMax, vMin, vMax;
		
        //Down - Yneg
		if (icons[0] != null){
	        uMin = 0;
	        uMax = icons[0].getIconWidth();
	        vMin = 0;
	        vMax = icons[0].getIconHeight();
	        list.add(new BakedQuad(Ints.concat(
	        		SEBakedQuadHelper.vertexToInts(vertexes[7][0], vertexes[7][1], vertexes[7][2], Color.WHITE.getRGB(), icons[0], uMin, vMin),	//uMin, vMax
	        		SEBakedQuadHelper.vertexToInts(vertexes[6][0], vertexes[6][1], vertexes[6][2], Color.WHITE.getRGB(), icons[0], uMin, vMax),	//uMin, vMin
	        		SEBakedQuadHelper.vertexToInts(vertexes[5][0], vertexes[5][1], vertexes[5][2], Color.WHITE.getRGB(), icons[0], uMax, vMax), //uMax, vMin
	        		SEBakedQuadHelper.vertexToInts(vertexes[4][0], vertexes[4][1], vertexes[4][2], Color.WHITE.getRGB(), icons[0], uMax, vMin)	//uMax, vMax
	                ),0, EnumFacing.DOWN, icons[0], true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
		}
        
		//Up - Ypos
		if (icons[1] != null){
	        uMin = 0;
	        uMax = icons[1].getIconWidth();
	        vMin = 0;
	        vMax = icons[1].getIconHeight();
	        list.add(new BakedQuad(Ints.concat(
	        		SEBakedQuadHelper.vertexToInts(vertexes[0][0], vertexes[0][1], vertexes[0][2], Color.WHITE.getRGB(), icons[1], uMax, vMax),
	        		SEBakedQuadHelper.vertexToInts(vertexes[1][0], vertexes[1][1], vertexes[1][2], Color.WHITE.getRGB(), icons[1], uMax, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[2][0], vertexes[2][1], vertexes[2][2], Color.WHITE.getRGB(), icons[1], uMin, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[3][0], vertexes[3][1], vertexes[3][2], Color.WHITE.getRGB(), icons[1], uMin, vMax)
	                ),0, EnumFacing.UP, icons[1], true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
		}
        
        //North - Zneg
		if (icons[2] != null){
	        uMin = 0;
	        uMax = icons[2].getIconWidth();
	        vMin = 0;
	        vMax = icons[2].getIconHeight();
	        list.add(new BakedQuad(Ints.concat(
	        		SEBakedQuadHelper.vertexToInts(vertexes[2][0], vertexes[2][1], vertexes[2][2], Color.WHITE.getRGB(), icons[2], uMax, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[1][0], vertexes[1][1], vertexes[1][2], Color.WHITE.getRGB(), icons[2], uMin, vMin),
	                SEBakedQuadHelper.vertexToInts(vertexes[5][0], vertexes[5][1], vertexes[5][2], Color.WHITE.getRGB(), icons[2], uMin, vMax),
	                SEBakedQuadHelper.vertexToInts(vertexes[6][0], vertexes[6][1], vertexes[6][2], Color.WHITE.getRGB(), icons[2], uMax, vMax)
	                ),0, EnumFacing.NORTH, icons[2], true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
		}
        
        //South - Zpos
		if (icons[3] != null){
	        uMin = 0;
	        uMax = icons[3].getIconWidth();
	        vMin = 0;
	        vMax = icons[3].getIconHeight();
	        list.add(new BakedQuad(Ints.concat(
	        		SEBakedQuadHelper.vertexToInts(vertexes[3][0], vertexes[3][1], vertexes[3][2], Color.WHITE.getRGB(), icons[3], uMin, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[7][0], vertexes[7][1], vertexes[7][2], Color.WHITE.getRGB(), icons[3], uMin, vMax),
	        		SEBakedQuadHelper.vertexToInts(vertexes[4][0], vertexes[4][1], vertexes[4][2], Color.WHITE.getRGB(), icons[3], uMax, vMax),
	                SEBakedQuadHelper.vertexToInts(vertexes[0][0], vertexes[0][1], vertexes[0][2], Color.WHITE.getRGB(), icons[3], uMax, vMin)
	                ), 0, EnumFacing.SOUTH, icons[3], true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
		}
	        
        //West - Xneg
		if (icons[4] != null){
	        uMin = 0;
	        uMax = icons[4].getIconWidth();
	        vMin = 0;
	        vMax = icons[4].getIconHeight();
	        list.add(new BakedQuad(Ints.concat(
	        		SEBakedQuadHelper.vertexToInts(vertexes[3][0], vertexes[3][1], vertexes[3][2], Color.WHITE.getRGB(), icons[4], uMax, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[2][0], vertexes[2][1], vertexes[2][2], Color.WHITE.getRGB(), icons[4], uMin, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[6][0], vertexes[6][1], vertexes[6][2], Color.WHITE.getRGB(), icons[4], uMin, vMax),
	                SEBakedQuadHelper.vertexToInts(vertexes[7][0], vertexes[7][1], vertexes[7][2], Color.WHITE.getRGB(), icons[4], uMax, vMax)
	                ),0, EnumFacing.WEST, icons[4], true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
		}

        //East - Xpos
		if (icons[5] != null){
	        uMin = 0;
	        uMax = icons[5].getIconWidth();
	        vMin = 0;
	        vMax = icons[5].getIconHeight();
	        list.add(new BakedQuad(Ints.concat(
	        		SEBakedQuadHelper.vertexToInts(vertexes[1][0], vertexes[1][1], vertexes[1][2], Color.WHITE.getRGB(), icons[5], uMax, vMin),
	        		SEBakedQuadHelper.vertexToInts(vertexes[0][0], vertexes[0][1], vertexes[0][2], Color.WHITE.getRGB(), icons[5], uMin, vMin),
	                SEBakedQuadHelper.vertexToInts(vertexes[4][0], vertexes[4][1], vertexes[4][2], Color.WHITE.getRGB(), icons[5], uMin, vMax),
	                SEBakedQuadHelper.vertexToInts(vertexes[5][0], vertexes[5][1], vertexes[5][2], Color.WHITE.getRGB(), icons[5], uMax, vMax)
	                ),0, EnumFacing.EAST, icons[5], true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
		}
    }
}
