package simelectricity.essential.client.cable;

import simelectricity.essential.cable.BlockCable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
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
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean accepts(String resPath){
		return resPath.startsWith(VPATH);
	}
	
	public void register(BlockCable block){
		//ModelLoader.setCustomStateMapper(block, this);
	}
}
