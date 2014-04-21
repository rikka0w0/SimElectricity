package simElectricity.Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.API.IEnergyTile;
import simElectricity.API.Util;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Item_Wrench extends Item{
	public Item_Wrench() {
		super();
		maxStackSize = 1;
		setHasSubtypes(true);
		setUnlocalizedName("sime:Item_Wrench");
		setMaxDamage(256);
		setCreativeTab(Util.SETab);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r)
    {
    	itemIcon=r.registerIcon("simElectricity:Item_Wrench");
    }
	
	@Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	if((world.getTileEntity(x, y, z) instanceof IEnergyTile)&(!world.isRemote)){
    		IEnergyTile te=(IEnergyTile) world.getTileEntity(x, y, z);
    		ForgeDirection newFacing=Util.getPlayerSight(player).getOpposite();   		  	
    		
    		if(te.canSetFunctionalSide(newFacing)){
    			te.setFunctionalSide(newFacing);
    			Util.postTileRejoinEvent((TileEntity) te);
    			Util.updateTileEntityFunctionalSide((TileEntity) te);
    	    	world.notifyBlocksOfNeighborChange(x, y, z, null);
    		}
    		
    		return true;
    	}else{
    		return false;
    	}
    }
}
