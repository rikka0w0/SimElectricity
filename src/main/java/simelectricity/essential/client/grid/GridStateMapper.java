package simelectricity.essential.client.grid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import simelectricity.essential.grid.BlockCableJoint;

public class GridStateMapper extends StateMapperBase{
	public final static String VPATH = "virtual/blockstates/grid";
	public final String domain;
	
	public GridStateMapper(String domain) {
		this.domain = domain;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		Block block = state.getBlock();
		String name = block.getRegistryName().getResourcePath();
		String varStr = name;
		
		switch (name) {
		case "essential_cable_joint":
			int facing = state.getValue(BlockCableJoint.propertyFacing);
			varStr = name + "," + facing;
			break;
		}
		ModelResourceLocation res = new ModelResourceLocation(this.domain + ":" + VPATH, varStr);
		return res;
	}
	
	public static IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
		String[] splited = variantStr.split(",");
		String name = splited[0];
		
		switch (name) {
		case "essential_cable_joint":
			int facing = Integer.parseInt(splited[1]);
			return new CableJointRawModel(facing);
		}
		
		return null;
	}
	
	public static boolean accepts(String resPath){
		return resPath.startsWith(VPATH);
	}
	
	public void register(Block block){
		ModelLoader.setCustomStateMapper(block, this);
	}
}
