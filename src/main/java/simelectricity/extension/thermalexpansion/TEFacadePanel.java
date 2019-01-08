package simelectricity.extension.thermalexpansion;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public class TEFacadePanel implements ISECoverPanel{
    private final IBlockState blockState;
    private final ItemStack itemStack;

    public TEFacadePanel(NBTTagCompound nbt) {
        Block block = Block.getBlockById(nbt.getInteger("blockID"));
        int meta = nbt.getByte("meta");

        this.blockState = block.getStateFromMeta(meta);

        this.itemStack = new ItemStack(nbt);
        if (itemStack != null)
            itemStack.setCount(1);
    }

    public TEFacadePanel(IBlockState blockState, ItemStack itemStack){
        this.blockState = blockState;

        if (itemStack == null){
            this.itemStack = ItemStack.EMPTY;
        }else{
            this.itemStack = itemStack.copy();
            this.itemStack.setCount(1);
        }

    }

    public IBlockState getBlockState() {return blockState;}

    @Override
    public boolean isHollow() {return true;}

    @Override
    public void toNBT(NBTTagCompound nbt) {
        nbt.setString("coverPanelType", "TEFacade");

        Block block = blockState.getBlock();
        int meta = block.getMetaFromState(blockState);
        nbt.setInteger("meta", meta);
        nbt.setInteger("blockID", Block.getIdFromBlock(block));

        if (itemStack != null)
            itemStack.writeToNBT(nbt);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISECoverPanelRender getCoverPanelRender() {
        return TEFacadeRender.instance;
    }

    @Override
    public void setHost(TileEntity hostTileEntity, EnumFacing side) {}

    @Override
    public ItemStack getDroppedItemStack() {
        return itemStack.copy();
    }
}
