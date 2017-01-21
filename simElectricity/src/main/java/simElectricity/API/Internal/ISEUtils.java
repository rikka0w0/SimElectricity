package simElectricity.API.Internal;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISEUtils {
	public String GetModName();
	
    /**
     * Get the texture index for a given side with a rotation
     */
	public int getTextureOnSide(int side, ForgeDirection direction);
	
    /**
     * Return which direction the player is looking at
     *
     * @param ignoreVertical return direction will ignore vertical directions(UP, DOWN) if this parameter set to true
     */
    public ForgeDirection getPlayerSight(EntityLivingBase player, boolean ignoreVertical);
    
    /**
     * Post some text in a player's chat window
     * 
     * @param player
     * @param text
     */
    public void chat(EntityPlayer player, String text);
    
    /**
     * Get a tileEntity on the given side of a tileEntity
     */
    public TileEntity getTileEntityonDirection(TileEntity tileEntity, ForgeDirection direction);
    
    /**
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection);
    
    /**
     * Exception version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exception        exception direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction
     *
     * @see simElectricity.Common.Blocks.BlockSwitch
     */
    public ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection exception);
    
    
    /**
     * Exceptions array version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exceptions       exception directions array
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection[] exceptions);
}
