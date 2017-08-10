package simelectricity.essential.client.cable;

import simelectricity.essential.cable.BlockCable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CableStateMapper extends StateMapperBase{
	public final static String VPATH = "virtual/blockstates/standardcable";
	public final String domain;
	
	public CableStateMapper(String domain){
		this.domain = domain;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		Block block = state.getBlock();
		
		if (block instanceof BlockCable){
			BlockCable cable = (BlockCable) block;
			int meta = cable.getMetaFromState(state);
			String name = cable.getRegistryName().getResourcePath();
			String subName = cable.subNames[meta];
			double thickness = cable.thickness[meta];
			
			//Encode relative information in the variant name part
			String varStr = name + "_" + subName + "," + thickness;
			
			//The resource path indicates the loader
			ModelResourceLocation res = new ModelResourceLocation(
					this.domain + ":" + VPATH,
					varStr
					);
			return res;
		}
		
		return null;
	}

	public static boolean accepts(String resPath){
		return resPath.startsWith(VPATH);
	}
	
	public static IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
		String[] splited = variantStr.split(",");
		String name = splited[0];
		float thickness = Float.parseFloat(splited[1]);
		return new CableRawModel(domain, name, thickness);
	}
	
	public void register(BlockCable block){
		ModelLoader.setCustomStateMapper(block, this);
	}
}
