package simelectricity.essential.client.grid.pole;

import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class CableJointModel extends CodeBasedModel {
	protected final int facing;
	protected final List<BakedQuad> quads = new LinkedList<>();

    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_updown")
    public final TextureAtlasSprite texture_updown = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_metal")
    public final TextureAtlasSprite texture_metal = null;
    @EasyTextureLoader.Mark("sime_essential:render/transmission/essential_cable_joint_texture_side")
    public final TextureAtlasSprite texture_side = null;


    protected CableJointModel(DirHorizontal8 facing) {
    	this.facing = (8-facing.ordinal()) & 7;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    	if (side != null)
            return emptyQuadList;
    	
        return this.quads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return texture_side;
    }
	
	public static class Type10kV extends CableJointModel {
		public Type10kV(DirHorizontal8 facing) {
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
		public Type415V(DirHorizontal8 facing) {
			super(facing);
		}
		
		@Override
		protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
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
		}
	}
}
