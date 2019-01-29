package simelectricity.essential.client.grid.pole;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class CableJointModel extends CodeBasedModel {
	protected final int facing;
	protected final List<BakedQuad> quads = new ArrayList();

    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_updown")
    public final TextureAtlasSprite texture_updown = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_metal")
    public final TextureAtlasSprite texture_metal = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_side")
    public final TextureAtlasSprite texture_side = null;
    
    
    public CableJointModel(int facing) {
        this.facing = facing;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
    	if (side != null)
            return ImmutableList.of();
    	
        return this.quads;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return texture_side;
    }
	
	public static class Type10kV extends CableJointModel {
		public Type10kV(int facing) {
			super(facing);
		}
		
		@Override
		protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {			
			this.quads.clear();
			
	        RawQuadGroup model = new RawQuadGroup();
	        RawQuadGroup branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.15F, 0.6F, 0.15F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.4F, 0.1F, 0.4F, texture_updown)).translateCoord(0, 0.35F, 0));
	        branch.add((new RawQuadCube(0.4F, 0.1F, 0.4F, texture_updown)).translateCoord(0, 0.55F, 0));
	        branch.add((new RawQuadCube(0.15F, 1F, 0.15F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).rotateAroundX(-10).translateCoord(0, 0.55F, 0));
	        model.merge(branch.rotateAroundX(45).rotateAroundZ(15));
	        
	        branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.15F, 0.6F, 0.15F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.4F, 0.1F, 0.4F, texture_updown)).translateCoord(0, 0.35F, 0));
	        branch.add((new RawQuadCube(0.4F, 0.1F, 0.4F, texture_updown)).translateCoord(0, 0.55F, 0));
	        branch.add((new RawQuadCube(0.15F, 1F, 0.15F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).translateCoord(0, 0.55F, 0));
	        model.merge(branch.rotateAroundZ(-25));

	        branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.15F, 0.6F, 0.15F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.4F, 0.1F, 0.4F, texture_updown)).translateCoord(0, 0.35F, 0));
	        branch.add((new RawQuadCube(0.4F, 0.1F, 0.4F, texture_updown)).translateCoord(0, 0.55F, 0));
	        branch.add((new RawQuadCube(0.15F, 1F, 0.15F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).rotateAroundX(10).translateCoord(0, 0.55F, 0));
	        model.merge(branch.rotateAroundX(-45).rotateAroundZ(15));
	        
	        model.rotateAroundY(facing * 45 - 90).translateCoord(0.5F, 0, 0.5F).bake(this.quads);
		}
	}
	
	public static class Type415V extends CableJointModel {
		public Type415V(int facing) {
			super(facing);
		}
		
		@Override
		protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
			this.quads.clear();
		}
		
	    @Override
	    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
	    	if (side != null)
	            return ImmutableList.of();
	    	
	    	this.quads.clear();
	    	
	        RawQuadGroup model = new RawQuadGroup();
	        RawQuadGroup branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.1F, 0.6F, 0.1F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.25F, 0.1F, 0.25F, texture_updown)).translateCoord(0, 0.5F, 0));
	        branch.add((new RawQuadCube(0.1F, 0.5F, 0.1F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).rotateAroundX(10).translateCoord(0, 0.55F, 0));
	        model.merge(branch.clone().rotateAroundX(-55).rotateAroundZ(5).rotateAroundY(15));
	        
	        
	        branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.1F, 0.6F, 0.1F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.25F, 0.1F, 0.25F, texture_updown)).translateCoord(0, 0.5F, 0));
	        branch.add((new RawQuadCube(0.1F, 0.5F, 0.1F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).rotateAroundX(-10).translateCoord(0, 0.55F, 0));
	        model.merge(branch.clone().rotateAroundX(55).rotateAroundZ(5).rotateAroundY(-15));
	        
	        
	        branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.1F, 0.6F, 0.1F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.25F, 0.1F, 0.25F, texture_updown)).translateCoord(0, 0.5F, 0));
	        branch.add((new RawQuadCube(0.1F, 0.5F, 0.1F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).rotateAroundX(10).translateCoord(0, 0.55F, 0));
	        model.merge(branch.clone().rotateAroundX(15).rotateAroundZ(-15).rotateAroundY(15));
	        
	        branch = new RawQuadGroup();
	        branch.add(new RawQuadCube(0.1F, 0.6F, 0.1F,new TextureAtlasSprite[]{null, null, texture_updown, texture_updown, texture_updown, texture_updown}));
	        branch.add((new RawQuadCube(0.25F, 0.1F, 0.25F, texture_updown)).translateCoord(0, 0.5F, 0));
	        branch.add((new RawQuadCube(0.1F, 0.5F, 0.1F, new TextureAtlasSprite[]{null, texture_metal, texture_side, texture_side, texture_side, texture_side})).rotateAroundX(-10).translateCoord(0, 0.55F, 0));
	        model.merge(branch.clone().rotateAroundX(-15).rotateAroundZ(-15).rotateAroundY(-15));
	        
	        model.rotateAroundY(facing * 45 - 90).translateCoord(0.5F, 0, 0.5F).bake(this.quads);
	    	
	        return this.quads;
	    }
	}
}
