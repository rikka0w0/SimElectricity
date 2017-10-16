package simelectricity.essential.client.grid.pole;

import com.google.common.base.Function;
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
import simelectricity.essential.client.ResourcePaths;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PowerPole2Model extends CodeBasedModel {
    private final List<BakedQuad> quads = new ArrayList();
    private final int rotation;
    private final int type;
    private final boolean isRod;

    @EasyTextureLoader.Mark(ResourcePaths.metal)
    private final TextureAtlasSprite textureMetal = null;
    @EasyTextureLoader.Mark(ResourcePaths.glass_insulator)
    private final TextureAtlasSprite textureInsulator = null;
    @EasyTextureLoader.Mark(ResourcePaths.concrete)
    private final TextureAtlasSprite textureConcrete = null;
    
    public PowerPole2Model(int facing, int type, boolean isRod) {
		/*
		 * Meta facing: MC: South - 0, OpenGL: Xpos(East) - 0
		 */
        this.rotation = facing * 90 - 90;
        this.type = type;
        this.isRod = isRod;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.textureMetal;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
    	if (side != null)
            return ImmutableList.of();
    	
        return this.quads;
    }

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
		this.quads.clear();

        FastTESRPowerPole2.modelInsulator = Models.render35KvInsulator(textureMetal, textureInsulator);		

        if (isRod) {
            RawQuadCube cube = new RawQuadCube(0.25F, 1, 0.25F, textureConcrete);
            cube.translateCoord(0.5F, 0, 0.5F);
            cube.bake(this.quads);
        } else {
        	RawQuadGroup model2 = new RawQuadGroup();
        	model2.add((new RawQuadCube(0.25F, 11F, 0.25F, textureMetal)).translateCoord(0, -5.5F, 0).rotateAroundX(90).translateCoord(0.25F, 0.125F, 0));
        	model2.add((new RawQuadCube(0.25F, 11F, 0.25F, textureMetal)).translateCoord(0, -5.5F, 0).rotateAroundX(90).translateCoord(-0.25F, 0.125F, 0));

            if (type > 0) {    //1
                RawQuadGroup insulator = FastTESRPowerPole2.modelInsulator.clone().rotateAroundX(180);
                model2.merge(insulator.clone().translateCoord(0, 0, -4.5F));
                model2.merge(insulator.clone().translateCoord(0, 0, 0));
                model2.merge(insulator.translateCoord(0, 0, 4.5F));
            }

            model2.rotateAroundY(rotation).translateCoord(0.5F, 0, 0.5F).bake(this.quads);
        }
	}
}
