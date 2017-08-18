package simelectricity.essential.client.grid;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.MinecraftForgeClient;
import simelectricity.essential.client.BlockRenderModel;
import simelectricity.essential.grid.BlockPowerPoleBottom;

public class PowerPoleBottomModel extends BlockRenderModel {
	private final TextureAtlasSprite texture;
	private final LinkedList<BakedQuad> quads;
	public PowerPoleBottomModel(int facing, TextureAtlasSprite texture) {
		this.texture = texture;
		this.quads = new LinkedList();
	
		int rotation = 0;
		switch (facing){
		case 1:
			rotation=0;
			break;
		case 3:
			rotation=90;
			break;
		case 5:
			rotation=180;
			break;
		case 7:
			rotation=270;
			break;
			
		case 2:
			rotation=45;
			break;	
		case 4:
			rotation=135;
			break;
		case 6:
			rotation=225;
			break;
		case 0:
			rotation=315;
			break;	
		}
		
		Vec3i offset = BlockPowerPoleBottom.getCenterBoxOffset(facing);
		Models.renderTower0Bottom(texture).rotateAroundY(rotation).transform(0.5+offset.getX(), 0, 0.5+offset.getZ()).bake(quads);
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {return texture;}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.SOLID) {
			return ImmutableList.copyOf(quads);
		}
		return null;
	}
}
