package simelectricity.essential.client.grid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import simelectricity.essential.grid.Properties;

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
		
		//Hard code everything
		int type;
		boolean isRod;
		int facing;
		switch (name) {
		case "essential_cable_joint":
			facing = state.getValue(Properties.propertyFacing);
			varStr = name + "," + facing;
			break;
		case "essential_transmission_tower_bottom":
			facing = state.getValue(Properties.propertyFacing);
			varStr = name + "," + facing;
			break;
		case "essential_transmission_tower":
			type = state.getValue(Properties.propertyType);
			facing = state.getValue(Properties.propertyFacing);
			varStr = name + "," + facing + "," + type;
			break;
		case "essential_transmission_tower_collision_box":
			varStr = name;
			break;
		case "essential_transmission_tower2":
			type = state.getValue(Properties.propertyType);
			isRod = state.getValue(Properties.propertyIsRod);
			facing = state.getValue(Properties.propertyFacing2);
			varStr = name + "," + facing + "," + type + "," + isRod;
			break;
		}
		ModelResourceLocation res = new ModelResourceLocation(this.domain + ":" + VPATH, varStr);
		return res;
	}
	
	public static IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
		String[] splited = variantStr.split(",");
		String name = splited[0];
		
		//Hard code everything
		int type;
		boolean isRod;
		int facing;
		switch (name) {
		case "essential_cable_joint":
			facing = Integer.parseInt(splited[1]);
			return new CableJointRawModel(facing);
		case "essential_transmission_tower_bottom":
			facing = Integer.parseInt(splited[1]);
			return new TransmissionTowerBottomRawModel(facing);
		case "essential_transmission_tower":
			facing = Integer.parseInt(splited[1]);
			type = Integer.parseInt(splited[2]);
			return new TransmissionTowerTopRawModel(facing, type);
		case "essential_transmission_tower_collision_box":
			return new GhostModel();
		case "essential_transmission_tower2":
			facing = Integer.parseInt(splited[1]);
			type = Integer.parseInt(splited[2]);
			isRod = Boolean.parseBoolean(splited[3]);
			return new TransmissionTower2RawModel(facing, type, isRod);		
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
