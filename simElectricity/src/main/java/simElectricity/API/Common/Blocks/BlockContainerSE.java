package simElectricity.API.Common.Blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import simElectricity.API.Common.Items.ItemBlockSE;
import simElectricity.API.Util;

/**
 * Basic SimElectricity Container Block
 *
 * @author <Meow J>
 */
public abstract class BlockContainerSE extends BlockContainer {

    public BlockContainerSE(Material material) {
        super(material);
        if (registerInCreativeTab())
            setCreativeTab(Util.SETab);
    }

    /**
     * If this block has its own ItemBlock, just override this method and shouldRegister(set to false).
     *
     * @param name name of this block.
     *
     * @see simElectricity.Common.Blocks.BlockWire
     * @see simElectricity.Common.Items.ItemBlocks.ItemBlockWire
     */
    @Override
    public Block setBlockName(String name) {
        if (shouldRegister())
            GameRegistry.registerBlock(this, ItemBlockSE.class, name);
        return super.setBlockName(name);
    }

    public boolean registerInCreativeTab() {
        return true;
    }

    public boolean shouldRegister() {
        return true;
    }
}
