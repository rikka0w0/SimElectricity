package simElectricity.API.Common.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.Util;

/**
 * Standard SE machine block
 *
 * @author <Meow J>
 */
public abstract class BlockStandardSEMachine extends BlockContainerSE {
    public BlockStandardSEMachine(Material material) {
        super(material);
    }

    public BlockStandardSEMachine() {
        this(Material.iron);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileStandardSEMachine))
            return;

        ForgeDirection functionalSide = Util.getPlayerSight(player);

        //TODO player
        ((TileStandardSEMachine) te).setFacing(functionalSide.getOpposite());

        if (world.getTileEntity(x + 1, y, z) instanceof IConductor)
            functionalSide = ForgeDirection.EAST;
        else if (world.getTileEntity(x - 1, y, z) instanceof IConductor)
            functionalSide = ForgeDirection.WEST;
        else if (world.getTileEntity(x, y, z + 1) instanceof IConductor)
            functionalSide = ForgeDirection.SOUTH;
        else if (world.getTileEntity(x, y, z - 1) instanceof IConductor)
            functionalSide = ForgeDirection.NORTH;
        else if (world.getTileEntity(x, y + 1, z) instanceof IConductor)
            functionalSide = ForgeDirection.UP;
        else if (world.getTileEntity(x, y - 1, z) instanceof IConductor)
            functionalSide = ForgeDirection.DOWN;

        //TODO side
        //TODO ((TileStandardSEMachine) te).setFacing(functionalSide.getOpposite());
        ((TileStandardSEMachine) te).setFunctionalSide(functionalSide);
    }
    //TODO
}
