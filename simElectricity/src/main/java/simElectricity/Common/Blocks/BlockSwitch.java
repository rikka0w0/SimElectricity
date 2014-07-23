package simElectricity.Common.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.AutoFacing;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Energy;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileSwitch;
import simElectricity.SimElectricity;

import java.util.Random;

public class BlockSwitch extends BlockContainerSE {
    private IIcon[] iconBuffer = new IIcon[6];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;

        TileSwitch te = (TileSwitch) world.getTileEntity(x, y, z);

        if (te.getFacing() != ForgeDirection.getOrientation(side)) {
            player.openGui(SimElectricity.instance, 0, world, x, y, z);
        } else {
            if (!world.isRemote) {
                te.isOn = !te.isOn;
                Util.updateTileEntityField(te, "isOn");
                Energy.postTileRejoinEvent(te);
            }
        }
        return true;
    }

    public BlockSwitch() {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("Switch");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:Transformer_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:Switch_Out");
        iconBuffer[2] = r.registerIcon("simElectricity:Switch_In");
        iconBuffer[3] = r.registerIcon("simElectricity:Switch_On");
        iconBuffer[4] = r.registerIcon("simElectricity:Switch_Off");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileSwitch te = (TileSwitch) world.getTileEntity(x, y, z);


        if (side == te.inputSide.ordinal())
            return iconBuffer[2];
        else if (side == te.outputSide.ordinal())
            return iconBuffer[1];
        else if (side == te.getFacing().ordinal()) {
            if (te.isOn)
                return iconBuffer[3];
            else
                return iconBuffer[4];
        } else
            return iconBuffer[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4)
            return iconBuffer[3];
        else
            return iconBuffer[0];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        TileSwitch te = (TileSwitch) world.getTileEntity(x, y, z);

        te.setFacing(Util.getPlayerSight(player).getOpposite());

        te.inputSide = AutoFacing.autoConnect(te, ForgeDirection.UP , te.getFacing());
        te.outputSide = AutoFacing.autoConnect(te, te.inputSide.getOpposite(), new ForgeDirection[]{te.inputSide,te.getFacing()});
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote)
            return;

        TileSwitch te = (TileSwitch) world.getTileEntity(x, y, z);

        Util.updateTileEntityField(te, "inputSide");
        Util.updateTileEntityField(te, "outputSide");
        Util.updateTileEntityField(te, "isOn");
        Util.updateTileEntityFacing(te);
        world.notifyBlockChange(x, y, z, this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSwitch();
    }
}
