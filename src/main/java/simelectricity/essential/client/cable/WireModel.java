package simelectricity.essential.client.cable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.quadbuilder.IRawModel;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.api.ISEGenericWire;
import simelectricity.essential.cable.BlockWire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class WireModel extends CodeBasedModel {
    private final ResourceLocation insulatorTextureLoc;
    private TextureAtlasSprite insulatorTexture;
    private final ResourceLocation conductorTextureLoc;
    private TextureAtlasSprite conductorTexture;

    public final float thickness;

    public WireModel(ResourceLocation insulatorTextureLoc, ResourceLocation conductorTextureLoc, float thickness) {
        this.insulatorTextureLoc = registerTexture(insulatorTextureLoc);
        this.conductorTextureLoc = registerTexture(insulatorTextureLoc);
        this.thickness = thickness;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.insulatorTexture;
    }

    @Override
    protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
        this.insulatorTexture = textureRegistry.apply(insulatorTextureLoc);
        this.conductorTexture = textureRegistry.apply(conductorTextureLoc);
    }

    @Override
	public List<BakedQuad> getQuads(BlockState state, Direction cullingSide, Random rand, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();

        ISEGenericWire wireTile = extraData.getData(ISEGenericWire.prop);        
        
        if (cullingSide == null) {
            //Render center & branches in SOLID layer
            if (MinecraftForgeClient.getRenderLayer() == RenderType.getSolid()) {
                for (Direction wire_side: Direction.values()) {
                    byte numOfCon = 0;
                    Direction conSide = Direction.DOWN;

                    TextureAtlasSprite[] centerTexture = {this.insulatorTexture, this.insulatorTexture,
                            this.insulatorTexture, this.insulatorTexture,
                            this.insulatorTexture, this.insulatorTexture};

                    RawQuadGroup group = new RawQuadGroup();

                    for (Direction direction : Direction.values()) {
                        if (wireTile.getWireParam(wire_side).hasBranchOnSide(direction)) {
                            group.add(genBranch(direction, wireTile.getWireParam(direction).hasBranchOnSide(wire_side)));
                            centerTexture[direction.ordinal()] = null;
                            conSide = direction;
                            numOfCon++;
                        }
                    }

                    if (numOfCon > 0) {
                        if (numOfCon == 1 && !wireTile.connectedOnSide(wire_side)) {
                            centerTexture[conSide.getOpposite().ordinal()] = this.conductorTexture;
                        }

                        // Center
                        RawQuadCube cube = new RawQuadCube(thickness, thickness, thickness, centerTexture);
                        cube.translateCoord(0.5F, 0.5F - thickness / 2, 0.5F);
                        group.add(cube);
                    }

                    translateGroupCoord(wire_side, group);

                    group.bake(quads);
                }
            }

            // Corners
            for (Direction[] pair: BlockWire.corners) {
                Direction wire_side = pair[0];
                Direction to = pair[1];

                if (
                        wireTile.getWireParam(wire_side).hasBranchOnSide(to) &&
                        wireTile.getWireParam(to).hasBranchOnSide(wire_side)
                        ) {
                    // Corner - interior
                    RawQuadCube cube = genCorner(to);
                    translateGroupCoord(wire_side, cube);
                    cube.bake(quads);
                }
            }


            for (Direction side: Direction.values()) {
                for (Direction to : Direction.values()) {
                    if (wireTile.hasExtConnection(side, to)) {
                        int index = BlockWire.cornerIdOf(side, to);
                        if (index < 0)
                            continue;

                        Direction f0 = BlockWire.corners[index][0];
                        Direction f1 = BlockWire.corners[index][1];

                        RawQuadCube cube = genCorner(to);
                        if (f0 == Direction.UP)
                            cube.translateCoord(0, thickness, 0);
                        if (f0 == Direction.DOWN)
                            cube.translateCoord(0, -thickness, 0);
                        if (f0 == Direction.NORTH)
                            cube.translateCoord(0, 0, -thickness);
                        if (f0 == Direction.SOUTH)
                            cube.translateCoord(0, 0, +thickness);
                        translateGroupCoord(f1, cube);

                        cube.bake(quads);
                    }

                }
            }
        }

        return quads;
    }

    private void translateGroupCoord(Direction wire_side, IRawModel<?> group) {
        switch (wire_side) {
            case DOWN:
                group.translateCoord(0, thickness / 2 - 0.5F , 0);
                break;
            case UP:
                group.translateCoord(0, 0.5F-thickness / 2 , 0);
                break;
            case NORTH:
                group.translateCoord(0, 0 , thickness / 2 - 0.5F);
                break;
            case SOUTH:
                group.translateCoord(0, 0 , 0.5F - thickness / 2);
                break;
            case WEST:
                group.translateCoord(thickness / 2 - 0.5F, 0 , 0);
                break;
            case EAST:
                group.translateCoord(0.5F - thickness / 2, 0 , 0);
                break;
        }
    }

    private RawQuadCube genCorner(Direction branch) {
        RawQuadCube cube = null;

        switch (branch) {
            case DOWN:
                cube = new RawQuadCube(thickness, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, null,
                                insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -0.5F, 0);
                break;

            case UP:
                cube = new RawQuadCube(thickness, thickness, thickness,
                        new TextureAtlasSprite[]{null, insulatorTexture,
                                insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, 0.5F - thickness, 0);
                break;

            case NORTH:
                cube = new RawQuadCube(thickness, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, null, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -thickness / 2, -0.5F + thickness / 2);
                break;

            case SOUTH:
                cube = new RawQuadCube(thickness, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                null, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -thickness / 2, 0.5F - thickness / 2);
                break;

            case WEST:
                cube = new RawQuadCube(thickness, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, insulatorTexture, insulatorTexture, null});
                cube.translateCoord(-0.5F + thickness / 2, -thickness / 2, 0);
                break;

            case EAST:
                cube = new RawQuadCube(thickness, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, insulatorTexture, null, insulatorTexture});
                cube.translateCoord(0.5F - thickness / 2, -thickness / 2, 0);
                break;
        }

        cube.translateCoord(0.5F, 0.5F, 0.5F);

        return cube;
    }

    private RawQuadCube genBranch(Direction branch, boolean noCorner) {
        RawQuadCube cube = null;
        float yMax = noCorner ? 0.5F - thickness * 3 / 2 : 0.5F - thickness / 2;

        switch (branch) {
            case DOWN:
                cube = new RawQuadCube(thickness, yMax, thickness,
                        new TextureAtlasSprite[]{noCorner?null:conductorTexture, null,
                                insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, noCorner ? -0.5F + thickness : -0.5F, 0);
                break;

            case UP:
                cube = new RawQuadCube(thickness, yMax, thickness,
                        new TextureAtlasSprite[]{null, noCorner?null:conductorTexture,
                                insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, thickness / 2, 0);
                break;

            case NORTH:
                cube = new RawQuadCube(thickness, thickness, yMax,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                noCorner?null:conductorTexture, null, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -thickness / 2, -0.25F - thickness / 4 + (noCorner ? thickness/2 : 0));
                break;

            case SOUTH:
                cube = new RawQuadCube(thickness, thickness, yMax,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                null, noCorner?null:conductorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -thickness / 2, 0.25F + thickness / 4 - (noCorner ? thickness/2 : 0));
                break;

            case WEST:
                cube = new RawQuadCube(yMax, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, insulatorTexture, noCorner?null:conductorTexture, null});
                cube.translateCoord(-0.25F - thickness / 4 + (noCorner ? thickness/2 : 0), -thickness / 2, 0);
                break;

            case EAST:
                cube = new RawQuadCube(yMax, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, insulatorTexture, null, noCorner?null:conductorTexture});
                cube.translateCoord(0.25F + thickness / 4 - (noCorner ? thickness/2 : 0), -thickness / 2, 0);
                break;
        }


        cube.translateCoord(0.5F, 0.5F, 0.5F);

        return cube;
    }
}
