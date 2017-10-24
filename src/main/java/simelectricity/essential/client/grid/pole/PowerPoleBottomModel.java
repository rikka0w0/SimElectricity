package simelectricity.essential.client.grid.pole;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.EasyTextureLoader;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.grid.BlockPowerPoleBottom;

@SideOnly(Side.CLIENT)
public class PowerPoleBottomModel extends CodeBasedModel {
	@EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite texture = null;

    @Override
    public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {    	
    	for (int facing=0; facing<8; facing++) {
            int rotation = 0;	//Legacy Rotation
            switch (facing) {
                case 1:
                    rotation = 0;
                    break;
                case 3:
                    rotation = 90;
                    break;
                case 5:
                    rotation = 180;
                    break;
                case 7:
                    rotation = 270;
                    break;

                case 2:
                    rotation = 45;
                    break;
                case 4:
                    rotation = 135;
                    break;
                case 6:
                    rotation = 225;
                    break;
                case 0:
                    rotation = 315;
                    break;
            }

            Vec3i offset = BlockPowerPoleBottom.getCenterBoxOffset(facing);
            List<BakedQuad> quads = new ArrayList();
            Models.renderTower0Bottom(texture).rotateAroundY(rotation).transform(0.5 + offset.getX(), 0, 0.5 + offset.getZ()).bake(quads);
            FastTESRPowerPoleBottom.bakedModel[facing] = quads;
    	}
    }

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return ImmutableList.of();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return texture;
	}
}
