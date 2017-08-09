package simelectricity;

import simelectricity.api.SEAPI;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy{
	public void registerRender(){
		ModelLoader.setCustomModelResourceLocation(SEAPI.managementToolItem, 0, 
				new ModelResourceLocation(SEAPI.managementToolItem.getRegistryName(),"inventory"));
	};
}
