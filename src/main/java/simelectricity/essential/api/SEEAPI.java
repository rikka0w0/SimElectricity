package simelectricity.essential.api;

import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.api.internal.ISECoverPanelRegistry;

import java.util.LinkedList;

public class SEEAPI {
    /**
     * Register your CoverPanelFactory, create CoverPanel from ItemStack or NBT
     */
    public static ISECoverPanelRegistry coverPanelRegistry;

    @SideOnly(Side.CLIENT)
    public static LinkedList<Block> coloredBlocks;
}
