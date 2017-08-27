package simelectricity.essential.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;

public abstract class SEBlock extends Block {
    public final SEItemBlock itemBlock;

    public SEBlock(String unlocalizedName, Material material, Class<? extends SEItemBlock> itemBlockClass) {
        super(material);
        setUnlocalizedName(unlocalizedName);
        setRegistryName(unlocalizedName);                //Key!

        beforeRegister();

        GameRegistry.register(this);

        try {
            Constructor constructor = itemBlockClass.getConstructor(Block.class);
            itemBlock = (SEItemBlock) constructor.newInstance(this);
            GameRegistry.register(this.itemBlock, getRegistryName());
        } catch (Exception e) {
            throw new RuntimeException("Invalid ItemBlock constructor!");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.itemBlock.getHasSubtypes()) {
            for (int ix = 0; ix < ((ISESubBlock) this).getSubBlockUnlocalizedNames().length; ix++)
                subItems.add(new ItemStack(this, 1, ix));
        } else {
            super.getSubBlocks(itemIn, tab, subItems);
        }
    }

    protected void beforeRegister() {
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
    //BlockState --------------------------------------------------------------------
    //createBlockState, setDefaultBlockState
}