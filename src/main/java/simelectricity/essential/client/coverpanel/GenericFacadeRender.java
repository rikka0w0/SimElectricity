package simelectricity.essential.client.coverpanel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import rikka.librikka.model.quadbuilder.MutableQuad;
import rikka.librikka.model.quadbuilder.MutableVertex;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;

@OnlyIn(Dist.CLIENT)
public class GenericFacadeRender implements ISECoverPanelRender<ISEFacadeCoverPanel> {
	public final static ISECoverPanelRender instance = new GenericFacadeRender();
	protected GenericFacadeRender() {};
	
	public static void tintFunc(Direction side, MutableQuad mquad) {
		int tint = mquad.getTint();
		if (tint != -1)
			mquad.setTint(tintFunc(side, tint));
	}
	
	//Encode the side information in the tint variable
	public static int tintFunc(Direction side, int tint)	{	return tint * Direction.values().length + side.ordinal();}
	
	public static int getTint(int in) {	return in / 6;}
	
	public static Direction getFacing(int in) {	return Direction.byIndex(in % Direction.values().length);}
	
    public final static Consumer<MutableVertex> shrinkX = (vertex) -> {
		if (vertex.position_x < 0.5)
			vertex.position_x += ISECoverPanel.thickness;
		else
			vertex.position_x -= ISECoverPanel.thickness;
	};

	public final static Consumer<MutableVertex> shrinkY = (vertex) -> {
		if (vertex.position_y < 0.5)
			vertex.position_y += ISECoverPanel.thickness;
		else
			vertex.position_y -= ISECoverPanel.thickness;
	};

	public final static Consumer<MutableVertex> shrinkZ = (vertex) -> {
		if (vertex.position_z < 0.5)
			vertex.position_z += ISECoverPanel.thickness;
		else
			vertex.position_z -= ISECoverPanel.thickness;
	};
	
	public static Direction.Axis perpendicular(Direction.Axis a, Direction.Axis b) {
		if (a==b || a==null || b==null)
			return null;
		
		for (Direction.Axis axis: Direction.Axis.values()) {
			if (axis!=a && axis!=b)
				return axis;
		}
		return null;
	}
	
	@Override
	public void renderCoverPanel(ISEFacadeCoverPanel coverPanel, Direction side, Random random, List quads) {		
		BlockState blockState = coverPanel.getBlockState();
		if (RenderTypeLookup.canRenderInLayer(blockState, MinecraftForgeClient.getRenderLayer())) {	
	        List<MutableQuad> mquads = new LinkedList<>();
	        
	        // Get the block model
			IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(blockState);
			
			final Direction.Axis theAxis = side.getAxis();
			final boolean posAxisDir = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
			Predicate<MutableVertex> canShrinkOnSide = (vertex)->{
				if (theAxis == Direction.Axis.X) {
					return vertex.position_x > 0.5 != posAxisDir;
				} else if (theAxis == Direction.Axis.Y) {
					return vertex.position_y > 0.5 != posAxisDir;
				} else if (theAxis == Direction.Axis.Z) {
					return vertex.position_z > 0.5 != posAxisDir;
				}
				return false;
			};
			
			for (Direction dir: Direction.values()) {
				for (BakedQuad quad: model.getQuads(blockState, dir, random, EmptyModelData.INSTANCE)) {
					MutableQuad mquad = new MutableQuad(quad);
					
					if (dir != side) {
						List<MutableVertex> vertexes = Arrays.asList(
								mquad.vertex_0,
								mquad.vertex_1,
								mquad.vertex_2,
								mquad.vertex_3
						);
						
						double minU = vertexes.stream().mapToDouble(vertex -> vertex.tex_u).min().orElse(0);
						double minV = vertexes.stream().mapToDouble(vertex -> vertex.tex_v).min().orElse(0);
						double maxU = vertexes.stream().mapToDouble(vertex -> vertex.tex_u).max().orElse(0);
						double maxV = vertexes.stream().mapToDouble(vertex -> vertex.tex_v).max().orElse(0);
						float lenShrink = posAxisDir ? 1-ISECoverPanel.thickness : ISECoverPanel.thickness-1;
						
						if (dir == side.getOpposite()) {
							// Back
				            Consumer<MutableVertex> func1 = null, func2 = null;
							if (theAxis == Direction.Axis.X) {
								func1 = shrinkY;
								func2 = shrinkZ;
							} else if (theAxis == Direction.Axis.Y) {
								func1 = shrinkX;
								func2 = shrinkZ;
							} else if (theAxis == Direction.Axis.Z) {
								func1 = shrinkX;
								func2 = shrinkY;
							}
							
							for (MutableVertex vertex: vertexes) {
								vertex.offset(theAxis, lenShrink);
								func1.accept(vertex);
								func2.accept(vertex);

								if (vertex.tex_u < (maxU+minU)/2)
									vertex.tex_u += (maxU-minU)/16D;
								else
									vertex.tex_u -= (maxU-minU)/16D;
								
								if (vertex.tex_v < (maxV+minV)/2)
									vertex.tex_v += (maxV-minV)/16D;
								else
									vertex.tex_v -= (maxV-minV)/16D;
							}
						} else {	// if (dir == side.getOpposite())
							//Side
							Direction.Axis axisToShrink = perpendicular(theAxis, dir.getAxis());
							
							for (MutableVertex vertex: vertexes) {
								if (canShrinkOnSide.test(vertex)) {
									vertex.offset(theAxis, lenShrink);
									vertex.offset(dir.getAxis(), 
											dir.getAxisDirection()==Direction.AxisDirection.POSITIVE
											? -ISECoverPanel.thickness
											: ISECoverPanel.thickness);

									if (axisToShrink == Direction.Axis.X) {
										shrinkX.accept(vertex);
									} else if (axisToShrink == Direction.Axis.Y) {
										shrinkY.accept(vertex);
									} else if (axisToShrink == Direction.Axis.Z) {
										shrinkZ.accept(vertex);
									}
									
									if (theAxis == Direction.Axis.Y) {
										if (vertex.tex_v < (maxV+minV)/2)
											vertex.tex_v += (maxV-minV)*14D/16D;
										else
											vertex.tex_v -= (maxV-minV)*14D/16D;
									} else {
										if (vertex.tex_u < (maxU+minU)/2)
											vertex.tex_u += (maxU-minU)*14D/16D;
										else
											vertex.tex_u -= (maxU-minU)*14D/16D;
									}

								}	// if (canShrinkOnSide.test(vertex))
							}	// for (MutableVertex vertex: vertexes)
						}	// if (dir == side.getOpposite())
					}	// if (dir != side)
					
					mquads.add(mquad);
				}	// for (BakedQuad quad: model.getQuads(blockState, dir, random, EmptyModelData.INSTANCE))
			}	// for (Direction dir: Direction.values())
			
			mquads.stream()
				.peek((mquad)->tintFunc(side, mquad))
				.map(MutableQuad::bake)
				.forEach(quads::add);
			
	        quads.addAll(SupportRender.forSide(side));
		}
	}
}
