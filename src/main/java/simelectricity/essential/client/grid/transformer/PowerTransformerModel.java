package simelectricity.essential.client.grid.transformer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.client.BlockRenderModel;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

import java.util.LinkedList;
import java.util.List;

public class PowerTransformerModel extends BlockRenderModel {
    public static final int[] rotationMatrix = {4, 0, 6, 2};    //NSWE
    private final int rotation;
    private final boolean mirrored;
    private final IBakedModel model;
    private final LinkedList<BakedQuad> insulator = new LinkedList();
    private final TextureAtlasSprite textureMetal, textureInsulator;

    public PowerTransformerModel(int facing, boolean mirrored, IBakedModel bakedModel, TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		rotation = PowerTransformerModel.rotationMatrix[facing] * 45 - 90;
        this.mirrored = mirrored;
		model = bakedModel;
        this.textureMetal = textureMetal;
        this.textureInsulator = textureInsulator;

        //Rotation is done first (with the .obj loader), so we need to figure out the reflection axis
        int b = 1;

        if (mirrored)
            b = -1;

        SERenderHeap model = new SERenderHeap();
        SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
        double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.8, 0.1);
        SERenderHelper.translateCoord(rod, 0, -0.1, 0);
        insulator.addCube(rod, textureMetal);
        insulator.transform(0, 0.1, 0);
        model.appendHeap(insulator.clone().transform(1, 1, -1.5 * b));
        model.appendHeap(insulator.clone().transform(1, 1, 0 * b));
        model.appendHeap(insulator.transform(1, 1, 1.5 * b));

        insulator = Models.renderInsulatorString(0.7, textureInsulator);
        rod = SERenderHelper.createCubeVertexes(0.1, 1.1, 0.1);
        SERenderHelper.translateCoord(rod, 0, -0.1, 0);
        insulator.addCube(rod, textureMetal);
        insulator.transform(0, 0.1, 0);
        model.appendHeap(insulator.clone().transform(-1, 1, 0.2 * b));
        model.appendHeap(insulator.clone().transform(-1, 1, 1 * b));
        model.appendHeap(insulator.transform(-1, 1, 1.8 * b));
        model.rotateAroundY(this.rotation).transform(0.5, 0, 0.5).bake(this.insulator);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.model.getParticleTexture();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<BakedQuad>();
        quads.addAll(this.model.getQuads(state, side, rand));
        quads.addAll(insulator);
        return quads;
    }
}
