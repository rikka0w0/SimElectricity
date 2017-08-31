package rikka.librikka.model.quadbuilder;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.math.MathAssitant;

/**
 * Covert code-based model generated with Techne to BakedQuads, with rotation about Y axis </p>
 * This module is designed to provide code-level compatibility, as much as possible
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class TechneModelPart {
	/**
	 * Either your IModel or IBakedModel should also implement this interface!
	 */
	public static interface TextureProvider {
		/**
		 * @return the relative scale, NOT the actual size of the texture PNG in pixels.
		 * This value seems to vary with models, common values are 32, 64 and 128.
		 */
		int getTextureRelativeSize();
		
		/**
		 * Notes:</P>
		 * 1. Width of the texture PNG file must equal to its height</P>
		 * 2. Both width and height must be multiple of 16, otherwise TextureManager will reject it</P>
		 * 3. If your texture PNG is not 1:1 ratio, then enlarge the png to make it a square
		 * @return an instance of a loaded texture sheet which is going to be used for the entire model
		 */
		TextureAtlasSprite getTexture();
		
		int getUOffset();
		int getVOffset();
	}
	
	private final TextureAtlasSprite texture;
	
	private final int textureRelativeSize;
    /** The X offset into the texture used for displaying this model */
    private final int textureOffsetX;
    /** The Y offset into the texture used for displaying this model */
    private final int textureOffsetY;
    
	private float offX, offY, offZ;
	private int width, height, depth;
	private float rotationPointX;
	private float rotationPointY;
	private float rotationPointZ;
	private float rotateAngleX;
	private float rotateAngleY;
	private float rotateAngleZ;
	
	public TechneModelPart(TextureProvider textureProvider, int textureOffsetX, int textureOffsetY) {
		this.texture = textureProvider.getTexture();
		this.textureRelativeSize = textureProvider.getTextureRelativeSize();
		this.textureOffsetX = textureOffsetX + textureProvider.getUOffset();
		this.textureOffsetY = textureOffsetY + textureProvider.getVOffset();
	}
	
	public TechneModelPart addBox(float offX, float offY, float offZ, int width, int height, int depth) {
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		this.width = width;
		this.height = height;
		this.depth = depth;
		return this;
	}
	
	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
        this.rotationPointX = rotationPointXIn;
        this.rotationPointY = rotationPointYIn;
        this.rotationPointZ = rotationPointZIn;
	}
	
	public void setRotation(float x, float y, float z) {
		this.rotateAngleX = x*180F/ MathAssitant.PI;
		this.rotateAngleY = y*180F/ MathAssitant.PI;
		this.rotateAngleZ = z*180F/ MathAssitant.PI;
	}
	
	public void setTextureSize(int u, int v) {
		//Unused stub
	}
	
	public boolean mirror; //Unused stub
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Generate BakedQuad from this part
	 * @param quads The list used to store compiled quads, must NOT be null
	 */
	public void bake(List<BakedQuad> quads, int rotation, float scale) {
		RawQuadCube2 cube = getRawCube(scale);
		cube.rotateAroundY(rotation);
		cube.translateCoord(0.5F, 1.5F, 0.5F);
		cube.bake(quads);
	}
	
	public RawQuadCube2 getRawCube(float scale) {
		//addBox
		RawQuadCube2 cube = new RawQuadCube2(width, height, depth, texture, textureRelativeSize,
				textureOffsetX+depth+width+width		, textureOffsetY+depth			,textureOffsetX+depth+width			, textureOffsetY		, 
				textureOffsetX+depth+width				, textureOffsetY+depth			,textureOffsetX+depth				, textureOffsetY		,
				textureOffsetX+depth+width				, textureOffsetY+depth+height 	,textureOffsetX+depth				, textureOffsetY+depth	,
				textureOffsetX+depth+depth+width+width	, textureOffsetY+depth+height 	,textureOffsetX+depth+depth+width	, textureOffsetY+depth	,
				textureOffsetX+depth+depth+width		, textureOffsetY+depth+height 	,textureOffsetX+depth+width			, textureOffsetY+depth	,
				textureOffsetX+depth					, textureOffsetY+depth+height 	,textureOffsetX						, textureOffsetY+depth	);
		cube.translateCoord(width/2F, 0, depth/2F);
		cube.translateCoord(offX, offY, offZ);
		
		
		//setRotationPoint
		cube.rotateAroundZ(rotateAngleZ);
		cube.rotateAroundY(rotateAngleY);
		cube.rotateAroundX(rotateAngleX);
		cube.translateCoord(rotationPointX, rotationPointY, rotationPointZ);
		
		//TESR
		cube.scale(scale*0.0625F);	//Fix Techne scale
		cube.rotateAroundZ(180);
		
		return cube;
	}
	
	/**
	 * Convert multiple parts to BakedQuads, and also rotate them about the Y axis
	 * @param quads Must NOT be null
	 * @param parts
	 */
	public static void render(List<BakedQuad> quads, int rotation, float scale, TechneModelPart... parts) {
		for (TechneModelPart part: parts)
			part.bake(quads, rotation, scale);
	}
	
	/**
	 * Covert multiple parts to RawQuadGroup, which allows further manipulation
	 * @param scale
	 * @param parts
	 * @return
	 */
	public static RawQuadGroup group(float scale, TechneModelPart... parts) {
		RawQuadGroup group = new RawQuadGroup();
		for (TechneModelPart part: parts)
			group.add(part.getRawCube(scale));
		return group;
	}
}
