package simElectricity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import simElectricity.API.IBaseComponent;
import simElectricity.API.IConductor;
import simElectricity.API.IEnergyTile;
import simElectricity.API.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Item_UltimateMultimeter extends Item{
	public Item_UltimateMultimeter() {
		super();
		maxStackSize = 1;
		setHasSubtypes(true);
		setUnlocalizedName("sime:Item_UltimateMultimeter");
		setMaxDamage(256);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r)
    {
    	itemIcon=r.registerIcon("simElectricity:Item_UltimateMultimeter");
    }
    
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	if((world.getTileEntity(x, y, z) instanceof IBaseComponent)&(!world.isRemote)){
    		IBaseComponent te=(IBaseComponent) world.getTileEntity(x, y, z);
    		float voltage=Util.getVoltage(te);
    		
    		String tileType="Unknown";
    		float outputVoltage=0;
    		
    		
    		if (te instanceof IEnergyTile){
    			IEnergyTile ps=(IEnergyTile) te;
    			if (((IEnergyTile) te).getOutputVoltage()==0)
    				tileType="Energy Sink";
    			else{
    				tileType="Energy Source";
    				outputVoltage=((IEnergyTile) te).getOutputVoltage();
    			}
    		}
    		
    		if(te instanceof IConductor){
    			IConductor c=(IConductor) te;
    			tileType="Energy Conductor";
    		}
    		//Print out information here
    		output("Type: "+tileType);  
    		if (te instanceof IEnergyTile)
    			output("FunctionalSide: "+ ((IEnergyTile)te).getFunctionalSide().toString());
    		if (te instanceof IEnergyTile&&outputVoltage>0)
    			output("Internal resistance: "+String.valueOf(te.getResistance()));  
    		else	
    			output("Resistance: "+String.valueOf(te.getResistance()));  
    		if (te instanceof IEnergyTile){
    			output("Current: "+String.valueOf(Util.getCurrent((IEnergyTile) te))); 
    			output("Power consumed: "+String.valueOf(Util.getPower((IEnergyTile) te))); 
    		}
    		output("Voltage: "+String.valueOf(voltage));    	
    		if(outputVoltage>0) //Energy Source
    			output("Internal voltage: "+String.valueOf(outputVoltage));  
    		output("-----------------------");  
    		
    		return true;
    	}else{
    		return false;
    	}
        
    }
    
    public void output(String text){
    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(text));
    }
}
