package simElectricity.API.Common.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Util;

/**
 * Standard SE machine block
 *
 * @author <Meow J>
 */
public abstract class BlockStandardSEHoriMachine extends BlockSidedHoriFacingMachine {

    public BlockStandardSEHoriMachine(Material material) {
        super(material);
    }

    public BlockStandardSEHoriMachine() {
        this(Material.iron);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, player, itemStack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileStandardSEMachine))
            return;

        EnumFacing functionalSide = Util.getPlayerSight(player, true);
        ((TileStandardSEMachine) te).setFacing(functionalSide.getOpposite());

        functionalSide = AutoFacing.autoConnect(te, functionalSide);
        ((TileStandardSEMachine) te).setFunctionalSide(functionalSide);
    }
}
