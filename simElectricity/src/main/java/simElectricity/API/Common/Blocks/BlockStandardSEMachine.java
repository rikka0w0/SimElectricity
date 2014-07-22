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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileStandardSEMachine))
            return;

        ForgeDirection functionalSide = Util.getPlayerSight(player);

        //TODO player
        ((TileStandardSEMachine) te).setFacing(functionalSide.getOpposite());

        functionalSide=AutoFacing.autoConnect(te, functionalSide);

        //TODO side
        //TODO ((TileStandardSEMachine) te).setFacing(functionalSide.getOpposite());
        ((TileStandardSEMachine) te).setFunctionalSide(functionalSide);
    }
    //TODO
}
