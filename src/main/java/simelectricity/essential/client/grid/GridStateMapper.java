package simelectricity.essential.client.grid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.client.GhostModel;
import simelectricity.essential.client.ISEModelLoader;
import simelectricity.essential.client.grid.pole.CableJointRawModel;
import simelectricity.essential.client.grid.pole.PowerPole2RawModel;
import simelectricity.essential.client.grid.pole.PowerPoleBottomRawModel;
import simelectricity.essential.client.grid.pole.PowerPoleTopRawModel;
import simelectricity.essential.grid.Properties;

public class GridStateMapper extends StateMapperBase implements ISEModelLoader {
	public final static String VPATH = "virtual/blockstates/grid";
	public final String domain;
	
	public GridStateMapper(String domain) {
		this.domain = domain;
	}
	
	@Override
	public boolean accepts(String resPath){
		return resPath.startsWith(VPATH);
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		Block block = state.getBlock();
		String blockDomain = block.getRegistryName().getResourceDomain();
		String blockName = block.getRegistryName().getResourcePath();
		
		String varStr = "";
		
		if (block == BlockRegistry.cableJoint) {
			varStr = "" + state.getValue(Properties.propertyFacing);
		} else if (block == BlockRegistry.powerPoleBottom) {
			varStr = "" + state.getValue(Properties.propertyFacing);
		} else if (block == BlockRegistry.powerPoleTop) {
			int type = state.getValue(Properties.propertyType);
			int facing = state.getValue(Properties.propertyFacing);
			varStr = facing + "," + type;
		} else if (block == BlockRegistry.powerPoleCollisionBox) {
			
		} else if (block == BlockRegistry.powerPole2) {
			int type = state.getValue(Properties.propertyType);
			boolean isRod = state.getValue(Properties.propertyIsRod);
			int facing = state.getValue(Properties.propertyFacing2);
			varStr = facing + "," + type + "," + isRod;
		}
		
		ModelResourceLocation res = new ModelResourceLocation(this.domain + ":" + VPATH, 
				blockDomain + "," + blockName + "," + varStr);
		return res;
	}
	
	@Override
	public IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
		String[] splited = variantStr.split(",");
		String blockDomain = splited[0];
		String blockName = splited[1];
		Block block = Block.getBlockFromName(blockDomain + ":" + blockName);
		
		if (block == BlockRegistry.cableJoint) {
			int facing = Integer.parseInt(splited[2]);
			return new CableJointRawModel(facing);
		} else if (block == BlockRegistry.powerPoleBottom) {
			int facing = Integer.parseInt(splited[2]);
			return new PowerPoleBottomRawModel(facing);
		} else if (block == BlockRegistry.powerPoleTop) {
			int facing = Integer.parseInt(splited[2]);
			int type = Integer.parseInt(splited[3]);
			return new PowerPoleTopRawModel(facing, type);
		} else if (block == BlockRegistry.powerPoleCollisionBox) {
			return new GhostModel();
		} else if (block == BlockRegistry.powerPole2) {
			int facing = Integer.parseInt(splited[2]);
			int type = Integer.parseInt(splited[3]);
			boolean isRod = Boolean.parseBoolean(splited[4]);
			return new PowerPole2RawModel(facing, type, isRod);		
		}
		
		return null;
	}
	
	public void register(Block block){
		ModelLoader.setCustomStateMapper(block, this);
	}
}
