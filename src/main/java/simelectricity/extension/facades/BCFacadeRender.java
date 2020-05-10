package simelectricity.extension.facades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.client.coverpanel.SupportRender;

public class BCFacadeRender implements ISECoverPanelRender{
	public final static ISECoverPanelRender instance = new BCFacadeRender();
	protected BCFacadeRender() {};
	
	public static int getVertexIndex(List<Vec3d> positions, Direction.Axis axis,
									 boolean minOrMax1, boolean minOrMax2) {
		Direction.Axis axis1, axis2;
		switch (axis) {
			case X:
				axis1 = Direction.Axis.Y;
				axis2 = Direction.Axis.Z;
				break;
			case Y:
				axis1 = Direction.Axis.X;
				axis2 = Direction.Axis.Z;
				break;
			case Z:
				axis1 = Direction.Axis.X;
				axis2 = Direction.Axis.Y;
				break;
			default:
				throw new IllegalArgumentException();
		}
		double min1 = positions.stream().mapToDouble(pos -> VecUtil.getValue(pos, axis1)).min().orElse(0);
		double min2 = positions.stream().mapToDouble(pos -> VecUtil.getValue(pos, axis2)).min().orElse(0);
		double max1 = positions.stream().mapToDouble(pos -> VecUtil.getValue(pos, axis1)).max().orElse(0);
		double max2 = positions.stream().mapToDouble(pos -> VecUtil.getValue(pos, axis2)).max().orElse(0);
		double center1 = (min1 + max1) / 2;
		double center2 = (min2 + max2) / 2;
		return positions.indexOf(
				positions.stream()
						.filter(pos ->
								(minOrMax1 ? VecUtil.getValue(pos, axis1) < center1 : VecUtil.getValue(pos, axis1) > center1) &&
										(minOrMax2 ? VecUtil.getValue(pos, axis2) < center2 : VecUtil.getValue(pos, axis2) > center2)
						)
						.findFirst()
						.orElse(positions.get(0))
		);
	}

	public static List<MutableQuad> getTransformedQuads(BlockState state, IBakedModel model, Direction side, Random rand,
														Vec3d pos0, Vec3d pos1, Vec3d pos2, Vec3d pos3) {
		return model.getQuads(state, side, rand).stream()
				.map(quad -> {
					MutableQuad mutableQuad = new MutableQuad().fromBakedItem(quad);
					boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
					Function<Vec3d, Vec3d> transformPosition = pos -> {
						switch (side.getAxis()) {
							case X:
								return new Vec3d(
										positive ? 1 - pos.z : pos.z,
										pos.y,
										pos.x
								);
							case Y:
								return new Vec3d(
										pos.x,
										positive ? 1 - pos.z : pos.z,
										pos.y
								);
							case Z:
								return new Vec3d(
										pos.y,
										pos.x,
										positive ? 1 - pos.z : pos.z
								);
							default:
								throw new IllegalArgumentException();
						}
					};
					List<Vec3d> poses = Arrays.asList(
							transformPosition.apply(pos0),
							transformPosition.apply(pos1),
							transformPosition.apply(pos2),
							transformPosition.apply(pos3)
					);
					List<MutableVertex> vertexes = Arrays.asList(
							mutableQuad.vertex_0,
							mutableQuad.vertex_1,
							mutableQuad.vertex_2,
							mutableQuad.vertex_3
					);
					List<Vec3d> vertexesPoses = vertexes.stream()
							.map(vertex -> new Vec3d(vertex.position_x, vertex.position_y, vertex.position_z))
							.collect(Collectors.toList());
					double minU = vertexes.stream().mapToDouble(vertex -> vertex.tex_u).min().orElse(0);
					double minV = vertexes.stream().mapToDouble(vertex -> vertex.tex_v).min().orElse(0);
					double maxU = vertexes.stream().mapToDouble(vertex -> vertex.tex_u).max().orElse(0);
					double maxV = vertexes.stream().mapToDouble(vertex -> vertex.tex_v).max().orElse(0);
					Stream.of(
							Pair.of(false, false),
							Pair.of(false, true),
							Pair.of(true, true),
							Pair.of(true, false)
					).forEach(minOrMaxPair -> {
						Vec3d newPos = poses.get(
								getVertexIndex(poses, side.getAxis(), minOrMaxPair.getLeft(), minOrMaxPair.getRight())
						);
						MutableVertex vertex = vertexes.get(
								getVertexIndex(vertexesPoses, side.getAxis(), minOrMaxPair.getLeft(), minOrMaxPair.getRight())
						);
						vertex.positiond(newPos.x, newPos.y, newPos.z);
						switch (side.getAxis()) {
							case X:
								vertex.texf(
										(float) (minU + (maxU - minU) * (positive ? (1 - newPos.z) : newPos.z)),
										(float) (minV + (maxV - minV) * (1 - newPos.y))
								);
								break;
							case Y:
								vertex.texf(
										(float) (minU + (maxU - minU) * (positive ? (1 - newPos.x) : newPos.x)),
										(float) (minV + (maxV - minV) * (1 - newPos.z))
								);
								break;
							case Z:
								vertex.texf(
										(float) (minU + (maxU - minU) * (positive ? newPos.x : (1 - newPos.x))),
										(float) (minV + (maxV - minV) * (1 - newPos.y))
								);
								break;
						}
					});
					return mutableQuad;
				})
				.collect(Collectors.toList());
	}

	public static Vec3d rotate(Vec3d vec, Rotation rotation) {
		switch (rotation) {
			case NONE:
				return new Vec3d(vec.x, vec.y, vec.z);
			case CLOCKWISE_90:
				return new Vec3d(1 - vec.y, 1 - vec.x, vec.z);
			case CLOCKWISE_180:
				return new Vec3d(1 - vec.x, 1 - vec.y, vec.z);
			case COUNTERCLOCKWISE_90:
				return new Vec3d(vec.y, vec.x, vec.z);
		}
		throw new IllegalArgumentException();
	}

	public static void addRotatedQuads(List<MutableQuad> quads, BlockState state, IBakedModel model, Direction side, Rotation rotation,
									   Random rand, Vec3d pos0, Vec3d pos1, Vec3d pos2, Vec3d pos3) {
		quads.addAll(getTransformedQuads(
				state, model, side, rand,
				rotate(pos0, rotation),
				rotate(pos1, rotation),
				rotate(pos2, rotation),
				rotate(pos3, rotation)
		));
	}
	
    public static List<MutableQuad> bake(BlockState blockState, Direction side) {
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(blockState);
        List<MutableQuad> quads = new ArrayList<>();
        int pS = 1;
        int nS = 16 - pS;
        
        Random random = new Random();
        random.setSeed(42L);
        
        quads.addAll(getTransformedQuads(
                blockState, model, side, random, 
                new Vec3d(0 / 16D, 16 / 16D, 0 / 16D),
                new Vec3d(16 / 16D, 16 / 16D, 0 / 16D),
                new Vec3d(16 / 16D, 0 / 16D, 0 / 16D),
                new Vec3d(0 / 16D, 0 / 16D, 0 / 16D)
        ));
        quads.addAll(getTransformedQuads(
                blockState, model, side.getOpposite(), random,
                new Vec3d(pS / 16D, nS / 16D, nS / 16D),
                new Vec3d(nS / 16D, nS / 16D, nS / 16D),
                new Vec3d(nS / 16D, pS / 16D, nS / 16D),
                new Vec3d(pS / 16D, pS / 16D, nS / 16D)
        ));

        for (Rotation rotation : Rotation.values()) {
            addRotatedQuads(
                    quads, blockState, model, side.getOpposite(), rotation, random,
                    new Vec3d(0 / 16D, 16 / 16D, 16 / 16D),
                    new Vec3d(pS / 16D, nS / 16D, nS / 16D),
                    new Vec3d(pS / 16D, pS / 16D, nS / 16D),
                    new Vec3d(0 / 16D, 0 / 16D, 16 / 16D)
            );
        }

        for (MutableQuad quad : quads) {
            int tint = quad.getTint();
            if (tint != -1) {
                quad.setTint(tintFunc(side, tint));
            }
        }
        return quads;
    }

	//Encode the side information in the tint variable
	public static int tintFunc(Direction side, int tint)	{	return tint * Direction.values().length + side.ordinal();}
	
	public static int getTint(int in) {	return in / 6;}
	
	public static Direction getFacing(int in) {	return Direction.byIndex(in % Direction.values().length);}
	
	@Override
	public void renderCoverPanel(ISECoverPanel coverPanel, Direction side, List quads) {
		BCFacadePanel facade = (BCFacadePanel) coverPanel;
		BlockState blockState = facade.getBlockState();
		if (RenderTypeLookup.canRenderInLayer(blockState, MinecraftForgeClient.getRenderLayer())) {
			List<MutableQuad> mutableQuads = bake(blockState, side);
	        List<BakedQuad> baked = new ArrayList<>();
	        for (MutableQuad quad : mutableQuads)
	        	quads.add(quad.toBakedItem());
	        
	        quads.addAll(SupportRender.forSide(side));
		}
	}
}
