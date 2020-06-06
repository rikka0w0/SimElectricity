package simelectricity.essential.client.cable;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.MinecraftForgeClient;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class CableModel extends CodeBasedModel {
    private final float thickness;
    private final ResourceLocation insulatorTextureLoc, conductorTextureLoc;
    public TextureAtlasSprite insulatorTexture, conductorTexture;

    private final List<BakedQuad>[] branches = new List[6];

    public CableModel(ResourceLocation insulatorTextureLoc, ResourceLocation conductorTextureLoc, float thickness) {
        this.insulatorTextureLoc = registerTexture(insulatorTextureLoc);
        this.conductorTextureLoc = registerTexture(insulatorTextureLoc);
        this.thickness = thickness;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.conductorTexture;
    }

    public RenderType getCableRenderLayer() {
    	return RenderType.getSolid();
    }
    
    @Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction cullingSide, Random rand,
			IModelData extraData) {
    	List<BakedQuad> quads = new ArrayList<BakedQuad>();
        
        ISEGenericCable cable = extraData.getData(ISEGenericCable.prop);
    	if (cable == null)
    		return quads;
        
    	if (cullingSide == null) {
            //Render center & branches in SOLID layer
            if (MinecraftForgeClient.getRenderLayer() == getCableRenderLayer()) {
                byte numOfCon = 0;
                Direction conSide = Direction.DOWN;

                TextureAtlasSprite[] centerTexture = {this.insulatorTexture, this.insulatorTexture,
                        this.insulatorTexture, this.insulatorTexture,
                        this.insulatorTexture, this.insulatorTexture};

                for (Direction direction : Direction.values()) {
                    if (cable.connectedOnSide(direction)) {
                        quads.addAll(this.branches[direction.ordinal()]);
                        centerTexture[direction.ordinal()] = null;
                        conSide = direction;
                        numOfCon++;
                    }
                }

                if (numOfCon == 1) {
                    centerTexture[conSide.getOpposite().ordinal()] = this.conductorTexture;
                }

                RawQuadCube cube = new RawQuadCube(this.thickness, this.thickness, this.thickness, centerTexture);
                cube.translateCoord(0.5F, 0.5F - this.thickness / 2, 0.5F);
                cube.bake(quads);
            }
    	}
    	
        //CoverPanel can be rendered in any layer
        for (Direction side : Direction.values()) {
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                ISECoverPanelRender render = coverPanel.getCoverPanelRender();
                if (render != null)
                    render.renderCoverPanel(coverPanel, side, rand, quads);
            }
        }

        return quads;
    }

	@Override
	protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
		this.conductorTexture = textureRegistry.apply(conductorTextureLoc);
		this.insulatorTexture = textureRegistry.apply(insulatorTextureLoc);
		
		//Bake branches
        List<BakedQuad> branchDown = new ArrayList<BakedQuad>();
        List<BakedQuad> branchUp = new ArrayList<BakedQuad>();
        List<BakedQuad> branchNorth = new ArrayList<BakedQuad>();
        List<BakedQuad> branchSouth = new ArrayList<BakedQuad>();
        List<BakedQuad> branchWest = new ArrayList<BakedQuad>();
        List<BakedQuad> branchEast = new ArrayList<BakedQuad>();

        RawQuadCube cube = new RawQuadCube(thickness, 0.5F - thickness / 2, thickness,
                new TextureAtlasSprite[]{conductorTexture, null,
                        insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
        cube.translateCoord(0, -0.5F, 0);
        cube.translateCoord(0.5F, 0.5F, 0.5F);
        cube.bake(branchDown);

        cube = new RawQuadCube(thickness, 0.5F - thickness / 2, thickness,
                new TextureAtlasSprite[]{null, conductorTexture,
                        insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
        cube.translateCoord(0, thickness / 2, 0);
        cube.translateCoord(0.5F, 0.5F, 0.5F);
        cube.bake(branchUp);

        cube = new RawQuadCube(thickness, thickness, 0.5F - thickness / 2,
                new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                        conductorTexture, null, insulatorTexture, insulatorTexture});
        cube.translateCoord(0, -thickness / 2, -0.25F - thickness / 4);
        cube.translateCoord(0.5F, 0.5F, 0.5F);
        cube.bake(branchNorth);

        cube = new RawQuadCube(thickness, thickness, 0.5F - thickness / 2,
                new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                        null, conductorTexture, insulatorTexture, insulatorTexture});
        cube.translateCoord(0, -thickness / 2, 0.25F + thickness / 4);
        cube.translateCoord(0.5F, 0.5F, 0.5F);
        cube.bake(branchSouth);

        cube = new RawQuadCube(0.5F - thickness / 2, thickness, thickness,
                new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                        insulatorTexture, insulatorTexture, conductorTexture, null});
        cube.translateCoord(-0.25F - thickness / 4, -thickness / 2, 0);
        cube.translateCoord(0.5F, 0.5F, 0.5F);
        cube.bake(branchWest);

        cube = new RawQuadCube(0.5F - thickness / 2, thickness, thickness,
                new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                        insulatorTexture, insulatorTexture, null, conductorTexture});
        cube.translateCoord(0.25F + thickness / 4, -thickness / 2, 0);
        cube.translateCoord(0.5F, 0.5F, 0.5F);
        cube.bake(branchEast);

        this.branches[0] = branchDown;
        this.branches[1] = branchUp;
        this.branches[2] = branchNorth;
        this.branches[3] = branchSouth;
        this.branches[4] = branchWest;
        this.branches[5] = branchEast;
	}
}
