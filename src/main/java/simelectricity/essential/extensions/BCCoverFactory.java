package simelectricity.essential.extensions;

import buildcraft.api.facades.IFacadeItem;
import buildcraft.api.transport.pluggable.IFacadePluggable;
import buildcraft.api.transport.pluggable.IPipePluggableItem;
import buildcraft.api.transport.pluggable.PipePluggable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.internal.ISECoverPanelFactory;
import simelectricity.essential.cable.CoverPanel;

public class BCCoverFactory implements ISECoverPanelFactory{		
	@Override
	public ISECoverPanel fromItemStack(ItemStack itemStack) {
        Item item = itemStack.getItem();
        
        if (!(item instanceof IFacadeItem) || !(item instanceof IPipePluggableItem))
        	return null;
        
        PipePluggable pipePluggable;
        try{
        	pipePluggable = ((IPipePluggableItem) item).createPipePluggable(null, null, itemStack);
        }catch(Exception e){
        	return null;
        }
        
        if (!(pipePluggable instanceof IFacadePluggable))
        	return null;
        
        IFacadePluggable facade = (IFacadePluggable) pipePluggable;
        
        return new CoverPanel(
        		true,
        		facade.isHollow(),
        		facade.getCurrentMetadata(),
        		facade.getCurrentBlock()
        		);
	}

	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt) {
		return null;
	}
}
