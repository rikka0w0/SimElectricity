package simElectricity.Common.Blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileCableClamp;

public class BlockCableClamp extends BlockContainerSE {
    public BlockCableClamp() {
        super();
        setUnlocalizedName("cable_clamp");
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return;

        TileCableClamp tower = (TileCableClamp) world.getTileEntity(pos);
        tower.facing = Util.getPlayerSight(player, true).ordinal();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCableClamp();
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, BlockPos pos, EnumFacing facing) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
