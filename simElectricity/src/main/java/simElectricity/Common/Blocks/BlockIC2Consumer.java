package simElectricity.Common.Blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.SimElectricity;

public class BlockIC2Consumer extends BlockStandardSEMachine {

    public BlockIC2Consumer() {
        super();
        setUnlocalizedName("ic2consumer");
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;

        player.openGui(SimElectricity.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
//TODO        return new TileIC2Consumer();
    }
}
