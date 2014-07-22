package simElectricity.API.Common.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardGenerator;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.Util;

/**
 * Standard generator block
 *
 * @author <Meow J>
 */
public abstract class BlockStandardGenerator extends BlockContainerSE {
    public BlockStandardGenerator(Material material) {
        super(material);
    }

    public BlockStandardGenerator() {
        this(Material.iron);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileStandardGenerator))
            return;

        ForgeDirection functionalSide = Util.getPlayerSight(player).getOpposite();

        functionalSide=AutoFacing.autoConnect(te, functionalSide);
        
        ((TileStandardGenerator) te).setFunctionalSide(functionalSide);
    }

    //TODO
}
