package simelectricity;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import simelectricity.api.SEAPI;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(SEAPI.managementToolItem, 0,
                new ModelResourceLocation(SEAPI.managementToolItem.getRegistryName(), "inventory"));
    }

}
