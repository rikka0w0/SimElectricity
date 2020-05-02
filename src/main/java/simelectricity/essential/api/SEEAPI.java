package simelectricity.essential.api;

import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.api.internal.ISECoverPanelRegistry;

import java.util.LinkedList;

public class SEEAPI {
    /**
     * Register your CoverPanelFactory, create CoverPanel from ItemStack or NBT
     */
    public static ISECoverPanelRegistry coverPanelRegistry;

    /**
     * Register new colored blocks during the FMLInitializationEvent event
     */
    @OnlyIn(Dist.CLIENT)
    public static LinkedList<Block> coloredBlocks;
}
