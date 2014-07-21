package simElectricity.API.Common.Blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import simElectricity.API.Common.Items.ItemBlockSE;
import simElectricity.API.Util;

public abstract class BlockSE extends Block {
    public BlockSE(Material material) {
        super(material);
        if (registerInCreativeTab())
            setCreativeTab(Util.SETab);
    }

    @Override
    public Block setBlockName(String par1Str) {
        if (shouldRegister())
            GameRegistry.registerBlock(this, ItemBlockSE.class, par1Str);
        return super.setBlockName(par1Str);
    }

    public boolean registerInCreativeTab() {
        return true;
    }

    public boolean shouldRegister() {
        return true;
    }
}
