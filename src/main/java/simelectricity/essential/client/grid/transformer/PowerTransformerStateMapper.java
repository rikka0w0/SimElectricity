package simelectricity.essential.client.grid.transformer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.client.ISEModelLoader;
import simelectricity.essential.client.SingleTextureModel;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.EnumBlockType;

public class PowerTransformerStateMapper extends StateMapperBase implements ISEModelLoader {
	public final static String VPATH = "virtual/blockstates/powertransformer";
	public final String domain;
	
	public PowerTransformerStateMapper(String domain) {
		this.domain = domain;
		
		OBJLoader.INSTANCE.addDomain(domain);
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
		
		if (block == BlockRegistry.powerTransformer) {
			varStr = "" + state.getValue(EnumBlockType.property).ordinal();
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
		
		if (block == BlockRegistry.powerTransformer) {
			EnumBlockType blockType  = EnumBlockType.fromInt(Integer.parseInt(splited[2]));
			
			if (blockType == EnumBlockType.Render)
				return new PowerTransformerRawModel();
			else
				return new SingleTextureModel(domain, "powertransformer_"+blockType.getName(), true);
		}
		
		return null;
	}
	
	public void register(Block block) {
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
