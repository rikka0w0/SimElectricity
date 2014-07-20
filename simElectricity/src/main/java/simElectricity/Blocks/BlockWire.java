package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.Util;

import java.util.List;
import java.util.Random;

public class BlockWire extends BlockContainer {

    public static final String[] subNames = { "CopperCable_Thin", "CopperCable_Medium", "CopperCable_Thick" };
    public static final float[] resistanceList = { 0.27F, 0.09F, 0.03F };
    public static final float[] collisionWidthList = { 0.12F, 0.22F, 0.32F };
    public static final float[] renderingWidthList = { 0.1F, 0.2F, 0.3F };

    public IIcon[] iconBuffer = new IIcon[subNames.length];


    //Initialize Block
    public BlockWire() {
        super(Material.circuits);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("Wire");
        setCreativeTab(Util.SETab);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            Util.scheduleBlockUpdate(world.getTileEntity(x, y, z));
            //updateRenderSides();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);
        ((TileWire) te).updateSides();
        Util.updateTileEntityField(te, "renderSides");

        updateRenderSides(world.getTileEntity(x + 1, y, z));
        updateRenderSides(world.getTileEntity(x - 1, y, z));
        updateRenderSides(world.getTileEntity(x, y + 1, z));
        updateRenderSides(world.getTileEntity(x, y - 1, z));
        updateRenderSides(world.getTileEntity(x, y, z + 1));
        updateRenderSides(world.getTileEntity(x, y, z - 1));
    }

    void updateRenderSides(TileEntity te) {
        if (te instanceof TileWire) {
            ((TileWire) te).updateSides();
            Util.updateTileEntityField(te, "renderSides");
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
        if (world.isRemote)
            return;
        ((TileWire) world.getTileEntity(x, y, z)).updateSides();
        Util.updateTileEntityField(world.getTileEntity(x, y, z), "renderSides");
    }


    //Rendering ----------------------------------------------------

    @Override
    public void setBlockBoundsForItemRender() {

        //TODO unused declaration
        float f = 0.1875F;
        setBlockBounds(1F, 0.6F, 1F, 0.0F, 0.6F, 0.0F);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
        super.addCollisionBoxesToList(world, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
        TileWire tile = (TileWire) world.getTileEntity(x, y, z);
        float WIDTH = collisionWidthList[world.getBlockMetadata(x, y, z)];

        float minA = 0.5F - WIDTH, maxA = 0.5F + WIDTH;
        float minX = 0.5F - WIDTH,
                minY = 0.5F - WIDTH,
                minZ = 0.5F - WIDTH,
                maxX = 0.5F + WIDTH,
                maxY = 0.5F + WIDTH,
                maxZ = 0.5F + WIDTH;

        //X方向
        boolean[] arr = tile.renderSides;

        if (arr[5])
            maxA = 1.0F;
        if (arr[4])
            minA = 0.0F;
        if (arr[5] || arr[4]) {
            setBlockBounds(minA, minY, minZ, maxA, maxY, maxZ);
            super.addCollisionBoxesToList(world, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
        }

        if (arr[3])
            maxA = 1.0F;
        else maxA = 0.5F + WIDTH;
        if (arr[2])
            minA = 0.0F;
        else minA = 0.5F - WIDTH;
        if (arr[3] || arr[2]) {
            setBlockBounds(minX, minY, minA, maxX, maxY, maxA);
            super.addCollisionBoxesToList(world, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
        }

        if (arr[1])
            maxA = 1.0F;
        else maxA = 0.5F + WIDTH;
        if (arr[0])
            minA = 0.0F;
        else minA = 0.5F - WIDTH;
        if (arr[1] || arr[0]) {
            setBlockBounds(minX, minA, minZ, maxX, maxA, maxZ);
            super.addCollisionBoxesToList(world, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
        }
    }


    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float WIDTH = collisionWidthList[world.getBlockMetadata(x, y, z)];
        boolean[] possibleConnections = ((TileWire)world.getTileEntity(x, y, z)).renderSides;

        minX = 0.5 - WIDTH;
        minY = 0.5 - WIDTH;
        minZ = 0.5 - WIDTH;
        maxX = 0.5 + WIDTH;
        maxY = 0.5 + WIDTH;
        maxZ = 0.5 + WIDTH;
        
        if (possibleConnections[0])
        	minY = 0;
        
        if (possibleConnections[1])
        	maxY = 1;
        
        if (possibleConnections[2])
        	minZ = 0;
        
        if (possibleConnections[3])
        	maxZ = 1;
        
        if (possibleConnections[4])
        	minX = 0;
        
        if (possibleConnections[5])
        	maxX = 1;
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    //-------------------------------------------------------------------------------------------------------------


    //Multi block stuff starts
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        for (int i = 0; i < subNames.length; i++) {
            iconBuffer[i] = r.registerIcon("simElectricity:Wiring/" + subNames[i]);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int blockMeta = world.getBlockMetadata(x, y, z);
        return iconBuffer[blockMeta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[meta];
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List subItems) {
        for (int ix = 0; ix < subNames.length; ix++) {
            subItems.add(new ItemStack(this, 1, ix));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileWire(meta);
    }
}
