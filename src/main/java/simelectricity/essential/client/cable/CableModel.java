package simelectricity.essential.client.cable;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import rikka.librikka.model.codebased.BlockRenderModel;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.properties.UnlistedPropertyRef;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class CableModel extends BlockRenderModel {
    private final TextureAtlasSprite insulatorTexture, conductorTexture;
    private final float thickness;

    private final List<BakedQuad>[] branches = new LinkedList[6];

    public CableModel(TextureAtlasSprite insulatorTexture,
                      TextureAtlasSprite conductorTexture, float thickness) {
        this.insulatorTexture = insulatorTexture;
        this.conductorTexture = conductorTexture;
        this.thickness = thickness;

        //Bake branches
        List<BakedQuad> branchDown = new LinkedList<BakedQuad>();
        List<BakedQuad> branchUp = new LinkedList<BakedQuad>();
        List<BakedQuad> branchNorth = new LinkedList<BakedQuad>();
        List<BakedQuad> branchSouth = new LinkedList<BakedQuad>();
        List<BakedQuad> branchWest = new LinkedList<BakedQuad>();
        List<BakedQuad> branchEast = new LinkedList<BakedQuad>();

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

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.conductorTexture;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState blockState,
                                    @Nullable EnumFacing uselessside, long rand) {

        List<BakedQuad> quads = new LinkedList<BakedQuad>();

        TileEntity te = UnlistedPropertyRef.get(blockState);
        if (!(te instanceof ISEGenericCable))
            return ImmutableList.of();
        
        ISEGenericCable cable = (ISEGenericCable) te;
        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

        //Render center & branches in SOLID layer
        if (layer == BlockRenderLayer.SOLID) {
            byte numOfCon = 0;
            EnumFacing conSide = EnumFacing.DOWN;

            TextureAtlasSprite[] centerTexture = {this.insulatorTexture, this.insulatorTexture,
                    this.insulatorTexture, this.insulatorTexture,
                    this.insulatorTexture, this.insulatorTexture};

            for (EnumFacing direction : EnumFacing.VALUES) {
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

        //CoverPanel can be rendered in any layer
        for (EnumFacing side : EnumFacing.VALUES) {
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                ISECoverPanelRender render = coverPanel.getCoverPanelRender();
                if (render != null)
                    render.renderCoverPanel(coverPanel, side, quads);
            }

        }

        return quads;
    }
}
