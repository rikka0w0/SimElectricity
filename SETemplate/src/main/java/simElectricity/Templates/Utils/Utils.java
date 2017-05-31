package simElectricity.Templates.Utils;

import java.util.Arrays;

import simElectricity.API.Tile.ISECableTile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class Utils {
    /**
     * Post some text in a player's chat window
     * 
     * @param player
     * @param text
     */
    public static void chat(EntityPlayer player, String text) {
        player.addChatMessage(new ChatComponentText(text));
    }
	
	/**
	 * @param player
	 * @return the direction where the player/entity is looking at, all 6 ForgeDirections are possible
	 */
    public static ForgeDirection getPlayerSight(EntityLivingBase player) {
    	return getPlayerSight(player, false);
    }
	
	/**
	 * @param player
	 * @param ignoreVertical If set to true, possible results are NESW, else the result can also be up or down/
	 * @return the direction where the player/entity is looking at
	 */
    public static ForgeDirection getPlayerSight(EntityLivingBase player, boolean ignoreVertical) {
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        
        if (!ignoreVertical) {
            if (pitch >= 65)
                return ForgeDirection.DOWN;  //1

            if (pitch <= -65)
                return ForgeDirection.UP;    //0
        }
        
        switch (heading) {
            case 0:
                return ForgeDirection.SOUTH; //2
            case 1:
                return ForgeDirection.WEST;  //5
            case 2:
                return ForgeDirection.NORTH; //3
            case 3:
                return ForgeDirection.EAST;  //4
            default:
                return null;
        }
    }
    
    /**
     * @param tileEntity
     * @param direction
     * @return if successful, tileEntity on the specified direction of the given tileEntity, otherwise null
     */
    public static TileEntity getTileEntityonDirection(TileEntity tileEntity, ForgeDirection direction) {
        return tileEntity.getWorldObj().getTileEntity(
                tileEntity.xCoord + direction.offsetX,
                tileEntity.yCoord + direction.offsetY,
                tileEntity.zCoord + direction.offsetZ);
    }
    
    /**
     * Transfer some liquid from items within slots to the container inside the machine, return empty containers
     *
     * @param maxVolume maximum capacity of the machine's container, in mB
     * @param currentFluidStack The fluidStack stored inside the machine's container, its data will not be modified by this function
     * @param slots An array of available slots inside the machine
     * @param filledSlotID The slot with filled containers
     * @param emptySlotID The slot used to receive empty containers
     * @return a NEW liquidStack if successful, otherwise null
     */
    public static FluidStack drainContainer(int maxVolume, FluidStack currentFluidStack, ItemStack[] slots, int filledSlotID, int emptySlotID){
		FluidStack fluidInItem = FluidContainerRegistry.getFluidForFilledItem(slots[filledSlotID]);
		
		//Return if the item is not a filled container
		if(fluidInItem == null)
			return null;
		
    	
    	ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(slots[filledSlotID]);
    	if (emptyContainer == null)
    		return null;  //TODO : No empty Container, should we eat it? May be here's a bug
    	
    	if (slots[emptySlotID] != null)
    		//Is the same type of itemStack in the output slot?
    		if (!slots[emptySlotID].isItemEqual(emptyContainer)||
    		//Is the output slot full?
    		slots[emptySlotID].stackSize > slots[emptySlotID].getMaxStackSize()-emptyContainer.stackSize)
    			return null;
    	
    	
    	if (currentFluidStack!=null){
    		//The fluid type inside the machine's container is different from the one in the slot
    		if (!fluidInItem.isFluidEqual(currentFluidStack))
    			return null;
    		
    		//The machine's container doesn't have any more space 
    		if (fluidInItem.amount>maxVolume - currentFluidStack.amount)
    			return null;
    	}
    	
   
    	
    	//Make an output
    	if (slots[emptySlotID] == null){
    		emptyContainer.stackSize = 1;
    		slots[emptySlotID] = emptyContainer;
    	}
    	else
    		slots[emptySlotID].stackSize += 1;
    	
    	
    	slots[filledSlotID].stackSize -= 1; //Consume the filled stack
    	if (slots[filledSlotID].stackSize == 0)
    		slots[filledSlotID] = null;     //Remove the empty stack
    		

    	return fluidInItem;
    }
    
    //SimElectricity	
    /**
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] { });
    }

    /**
     * Exception version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exception        exception direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction
     *
     * @see simElectricity.Templates.Blocks.BlockSwitch
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection exception) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] { exception });
    }

    /**
     * Exceptions array version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exceptions       exception directions array
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection[] exceptions) {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord + direction.offsetX,
                    tileEntity.yCoord + direction.offsetY,
                    tileEntity.zCoord + direction.offsetZ) instanceof ISECableTile
                    && !Arrays.asList(exceptions).contains(direction))
                return direction;
        }
        return defaultDirection;
    }
}
