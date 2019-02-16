package simelectricity.extension.facades;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import simelectricity.essential.api.SEEAPI;

public class ClientProxy extends CommonProxy{
    @Override
    public void RegisterBlockColorHandlers() {
        for (Block block: SEEAPI.coloredBlocks) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(BlockColorHandler.colorHandler, block);
        }
    }
}