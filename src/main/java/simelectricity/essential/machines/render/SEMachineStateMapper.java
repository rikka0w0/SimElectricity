package simelectricity.essential.machines.render;

import simelectricity.essential.client.ISESidedTextureBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SEMachineStateMapper extends StateMapperBase{
	public final static String VPATH = "virtual/blockstates/semachine";
	public final String domain;
	
	public SEMachineStateMapper(String domain){
		this.domain = domain;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		Block block = state.getBlock();
		
		if (block instanceof ISESidedTextureBlock){
			ISESidedTextureBlock stBlock = (ISESidedTextureBlock) block;
			
			String modelName = stBlock.getModelNameFrom(state);
			ModelResourceLocation res = new ModelResourceLocation(
					this.domain + ":" + VPATH,
					modelName
					);
			return res;
		}
		
		
		return null;
	}

	public static boolean accepts(String resPath){
		return resPath.startsWith(VPATH);
	}
	
	public static IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
		IModel model = new SEMachineRawModel(domain, variantStr, false);
		return model;
	}
}
