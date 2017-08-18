package simelectricity.essential.client.grid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import simelectricity.essential.client.ISEModelLoader;
import simelectricity.essential.client.SingleTextureModel;
import simelectricity.essential.grid.Properties;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.EnumBlockType;

public class GridStateMapper extends StateMapperBase implements ISEModelLoader {
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
		case "essential_powerpole_bottom":
			facing = state.getValue(Properties.propertyFacing);
			varStr = name + "," + facing;
			break;
		case "essential_powerpole":
			type = state.getValue(Properties.propertyType);
			facing = state.getValue(Properties.propertyFacing);
			varStr = name + "," + facing + "," + type;
			break;
		case "essential_powerpole_collision_box":
			varStr = name;
			break;
		case "essential_powerpole2":
			type = state.getValue(Properties.propertyType);
			isRod = state.getValue(Properties.propertyIsRod);
			facing = state.getValue(Properties.propertyFacing2);
			varStr = name + "," + facing + "," + type + "," + isRod;
			break;
		case "essential_powertransformer":
			type = state.getValue(EnumBlockType.property).ordinal();
			varStr = name + "," + type;
			break;
		}
		ModelResourceLocation res = new ModelResourceLocation(this.domain + ":" + VPATH, varStr);
		return res;
	}
	
	@Override
	public IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
		String[] splited = variantStr.split(",");
		String name = splited[0];
		
		//Hard code everything
		int type;
		boolean isRod;
		int facing;
		EnumBlockType blockType;
		switch (name) {
		case "essential_cable_joint":
			facing = Integer.parseInt(splited[1]);
			return new CableJointRawModel(facing);
		case "essential_powerpole_bottom":
			facing = Integer.parseInt(splited[1]);
			return new PowerPoleBottomRawModel(facing);
		case "essential_powerpole":
			facing = Integer.parseInt(splited[1]);
			type = Integer.parseInt(splited[2]);
			return new PowerPoleTopRawModel(facing, type);
		case "essential_powerpole_collision_box":
			return new GhostModel();
		case "essential_powerpole2":
			facing = Integer.parseInt(splited[1]);
			type = Integer.parseInt(splited[2]);
			isRod = Boolean.parseBoolean(splited[3]);
			return new PowerPole2RawModel(facing, type, isRod);		
		case "essential_powertransformer":
			type = Integer.parseInt(splited[1]);
			blockType = EnumBlockType.fromInt(type);
			return new SingleTextureModel(domain, "powertransformer_"+blockType.getName(), true);
		}
		
		return null;
	}
	
	@Override
	public boolean accepts(String resPath){
		return resPath.startsWith(VPATH);
	}
	
	public void register(Block block){
		ModelLoader.setCustomStateMapper(block, this);
		
		if (block instanceof BlockPowerTransformer) {
			ItemBlock itemBlock = ((BlockPowerTransformer)block).itemBlock;
			for (EnumBlockType blockType: EnumBlockType.values){
				IBlockState blockState = ((BlockPowerTransformer)block).stateFromType(blockType);
				int meta = block.getMetaFromState(blockState);
				ModelResourceLocation res = this.getModelResourceLocation(blockState);
				//Also register inventory variants here
				ModelLoader.setCustomModelResourceLocation(itemBlock, meta, res);
			}
		}
	}
}
