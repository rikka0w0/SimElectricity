package simelectricity.essential.client.cable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import rikka.librikka.properties.UnlistedPropertyRef;
import simelectricity.essential.api.ISEGenericWire;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WireModel extends CodeBasedModel {
    private final ResourceLocation insulatorTextureLoc, conductorTextureLoc;
    private TextureAtlasSprite insulatorTexture, conductorTexture;

    public WireModel(String domain, String name) {
//        this.insulatorTextureLoc = this.registerTexture(domain + ":blocks/" + name + "_insulator");    // We just want to bypass the ModelBakery
//        this.conductorTextureLoc = this.registerTexture(domain + ":blocks/" + name + "_conductor");    // and load our texture
        this.insulatorTextureLoc = this.registerTexture(domain + ":blocks/cable/essential_cable_aluminum_thin_insulator");    // We just want to bypass the ModelBakery
        this.conductorTextureLoc = this.registerTexture(domain + ":blocks/cable/essential_cable_aluminum_thin_conductor");    // and load our texture
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.conductorTexture;
    }

    @Override
    protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
        this.conductorTexture = textureRegistry.apply(conductorTextureLoc);
        this.insulatorTexture = textureRegistry.apply(insulatorTextureLoc);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState blockState,
                                    @Nullable EnumFacing cullingSide, long rand) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();

        TileEntity te = UnlistedPropertyRef.get(blockState);

        if (!(te instanceof ISEGenericWire))
            return quads;

        ISEGenericWire wireTile = (ISEGenericWire) te;

        if (cullingSide == null) {
            //Render center & branches in SOLID layer
            if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.SOLID) {
                for (EnumFacing wire_side: EnumFacing.VALUES) {
                    float thickness = wireTile.getWireThickness(wire_side);

                    byte numOfCon = 0;
                    EnumFacing conSide = EnumFacing.DOWN;

                    TextureAtlasSprite[] centerTexture = {this.insulatorTexture, this.insulatorTexture,
                            this.insulatorTexture, this.insulatorTexture,
                            this.insulatorTexture, this.insulatorTexture};

                    RawQuadGroup group = new RawQuadGroup();

                    for (EnumFacing direction : EnumFacing.VALUES) {
                        if (wireTile.connectedOnSide(wire_side, direction)) {
                            group.add(genBranch(wire_side, direction, thickness));
                            centerTexture[direction.ordinal()] = null;
                            conSide = direction;
                            numOfCon++;
                        }
                    }

                    if (numOfCon > 0) {
                        if (numOfCon == 1) {
                            centerTexture[conSide.getOpposite().ordinal()] = this.conductorTexture;
                        }

                        // Center
                        RawQuadCube cube = new RawQuadCube(thickness, thickness, thickness, centerTexture);
                        cube.translateCoord(0.5F, 0.5F - thickness / 2, 0.5F);
                        group.add(cube);
                    }

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

                    group.bake(quads);
                }
            }
        }

        return quads;
    }

    private RawQuadCube genBranch(EnumFacing wire_side, EnumFacing branch, float thickness) {
        RawQuadCube cube;

        switch (branch) {
            case DOWN:
                cube = new RawQuadCube(thickness, 0.5F - thickness / 2, thickness,
                        new TextureAtlasSprite[]{conductorTexture, null,
                                insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -0.5F, 0);
                cube.translateCoord(0.5F, 0.5F, 0.5F);
                break;

            case UP:
                cube = new RawQuadCube(thickness, 0.5F - thickness / 2, thickness,
                        new TextureAtlasSprite[]{null, conductorTexture,
                                insulatorTexture, insulatorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, thickness / 2, 0);
                cube.translateCoord(0.5F, 0.5F, 0.5F);
                break;

            case NORTH:
                cube = new RawQuadCube(thickness, thickness, 0.5F - thickness / 2,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                conductorTexture, null, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -thickness / 2, -0.25F - thickness / 4);
                cube.translateCoord(0.5F, 0.5F, 0.5F);
                break;

            case SOUTH:
                cube = new RawQuadCube(thickness, thickness, 0.5F - thickness / 2,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                null, conductorTexture, insulatorTexture, insulatorTexture});
                cube.translateCoord(0, -thickness / 2, 0.25F + thickness / 4);
                cube.translateCoord(0.5F, 0.5F, 0.5F);
                break;

            case WEST:
                cube = new RawQuadCube(0.5F - thickness / 2, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, insulatorTexture, conductorTexture, null});
                cube.translateCoord(-0.25F - thickness / 4, -thickness / 2, 0);
                cube.translateCoord(0.5F, 0.5F, 0.5F);
                break;

            case EAST:
                cube = new RawQuadCube(0.5F - thickness / 2, thickness, thickness,
                        new TextureAtlasSprite[]{insulatorTexture, insulatorTexture,
                                insulatorTexture, insulatorTexture, null, conductorTexture});
                cube.translateCoord(0.25F + thickness / 4, -thickness / 2, 0);
                cube.translateCoord(0.5F, 0.5F, 0.5F);
                break;

            default:
                cube = null;
        }

        return cube;
    }
}
