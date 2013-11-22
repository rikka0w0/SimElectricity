package simElectricity;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import simElectricity.API.IBaseComponent;
import simElectricity.API.IConductor;
import simElectricity.API.IEnergyTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Item_UltimateMultimeter extends Item{
	public Item_UltimateMultimeter(int id) {
		super(id);
		maxStackSize = 1;
		setHasSubtypes(true);
		setUnlocalizedName("Item_UltimateMultimeter");
		setMaxDamage(256);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
    	itemIcon=par1IconRegister.registerIcon("simElectricity:Item_UltimateMultimeter");
    }
    
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	if((world.getBlockTileEntity(x, y, z) instanceof IBaseComponent)&(!world.isRemote)){
    		IBaseComponent te=(IBaseComponent) world.getBlockTileEntity(x, y, z);
    		float voltage;
    		if (EnergyNet.getForWorld(world).voltageCache.containsKey(te))
    			voltage = EnergyNet.getForWorld(world).voltageCache.get(te);
    		else
    			voltage = 0;
    		
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
    		player.sendChatToPlayer(ChatMessageComponent.createFromText("Type: "+tileType));  
    		player.sendChatToPlayer(ChatMessageComponent.createFromText("Resistance: "+String.valueOf(te.getResistance())));  
    		if (te instanceof IEnergyTile){
    			if(outputVoltage>0){//Energy Source
    				player.sendChatToPlayer(ChatMessageComponent.createFromText("Current: "+String.valueOf((outputVoltage-voltage)/te.getResistance()))); 
    				player.sendChatToPlayer(ChatMessageComponent.createFromText("Power consumed: "+String.valueOf((outputVoltage-voltage)*(outputVoltage-voltage)/te.getResistance()))); 
    			}else{//Energy Sink
    				player.sendChatToPlayer(ChatMessageComponent.createFromText("Current: "+String.valueOf(voltage/te.getResistance()))); 
    				player.sendChatToPlayer(ChatMessageComponent.createFromText("Power consumed: "+String.valueOf(voltage*voltage/te.getResistance())));    				
    			}
    		}
    		player.sendChatToPlayer(ChatMessageComponent.createFromText("Voltage: "+String.valueOf(voltage)));    	
    		if(outputVoltage>0) //Energy Source
        		player.sendChatToPlayer(ChatMessageComponent.createFromText("Internal voltage: "+String.valueOf(outputVoltage)));  
    		player.sendChatToPlayer(ChatMessageComponent.createFromText("-----------------------"));  
    		
    		return true;
    	}else{
    		return false;
    	}
        
    }
}
