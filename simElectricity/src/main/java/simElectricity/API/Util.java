package simElectricity.API;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.EnergyTile.ITransformer;
import simElectricity.Common.Network.PacketTileEntityFieldUpdate;
import simElectricity.Common.Network.PacketTileEntitySideUpdate;
import simElectricity.SimElectricity;

public class Util {

    /**
     * For getBlock/Item
     */
    public static final String MODID = "SimElectricity";
    public static final String NAME = "SimElectricity";
    /**
     * Creative Tab for SimElectricity project
     */
    public static CreativeTabs SETab;

    //Network & Sync

    /**
     * Update a client tileEntity field from the server
     */
    public static void updateTileEntityField(TileEntity tileEntity, String field) {
        SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntityFieldUpdate(tileEntity, field), tileEntity.getWorldObj().getWorldInfo().getVanillaDimension());
    }

    /**
     * Update a server tileEntity field from a client
     */
    public static void updateTileEntityFieldToServer(TileEntity tileEntity, String field) {
        SimElectricity.instance.packetPipeline.sendToServer(new PacketTileEntityFieldUpdate(tileEntity, field));
    }

    /**
     * Update a tileEntity's facing on client side
     */
    public static void updateTileEntityFacing(TileEntity tileEntity) {
        SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntitySideUpdate(tileEntity, (byte) 0), tileEntity.getWorldObj().getWorldInfo().getVanillaDimension());
    }

    /**
     * Update a tileEntity's functional side on client side
     */
    public static void updateTileEntityFunctionalSide(TileEntity tileEntity) {
        SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntitySideUpdate(tileEntity, (byte) 1), tileEntity.getWorldObj().getWorldInfo().getVanillaDimension());
    }

    // Util

    /**
     * Post some text in chat box(Other player cannot see it)
     */
    public static void chat(EntityPlayer player, String text) {
        player.addChatMessage(new ChatComponentText(text));
    }

    /**
     * Get a tileEntity on the given side of a tileEntity
     */
    public static TileEntity getTileEntityonDirection(TileEntity tileEntity, ForgeDirection direction) {
        return tileEntity.getWorldObj().getTileEntity(
                tileEntity.xCoord + direction.offsetX,
                tileEntity.yCoord + direction.offsetY,
                tileEntity.zCoord + direction.offsetZ);
    }

    /**
     * Used by wires to find possible connections
     */
    public static boolean possibleConnection(TileEntity tileEntity, ForgeDirection direction) {
        TileEntity ent = getTileEntityonDirection(tileEntity, direction);

        if (ent instanceof IConductor) {
        	if (tileEntity instanceof IConductor){
            	if (((IConductor) ent).getColor() == 0 || 
            			((IConductor) tileEntity).getColor() == 0 ||
                		((IConductor) ent).getColor() == ((IConductor) tileEntity).getColor()){
                		return true;
                	}
        	} else {
        		return true;
        	}

        } else if (ent instanceof IEnergyTile) {
            ForgeDirection functionalSide = ((IEnergyTile) ent).getFunctionalSide();

            if (direction == functionalSide.getOpposite())
                return true;

        } else if (ent instanceof IConnectable) {
            if (((IConnectable) ent).canConnectOnSide(direction.getOpposite()))
                return true;
        } else if (ent instanceof ITransformer) {
            if (((ITransformer) ent).getPrimarySide() == direction.getOpposite() ||
                    ((ITransformer) ent).getSecondarySide() == direction.getOpposite())
                return true;
        }

        return false;
    }

    //Facing and Rendering

    /**
     * Update a block rendering after 10 ticks
     */
    public static void scheduleBlockUpdate(TileEntity tileEntity) {
        scheduleBlockUpdate(tileEntity, 10);
    }

    /**
     * Update a block rendering after some ticks
     */
    public static void scheduleBlockUpdate(TileEntity tileEntity, int time) {
        if (tileEntity == null)
            return;
        tileEntity.getWorldObj().scheduleBlockUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.getWorldObj().getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord), time);
    }

    /**
     * Get the texture index for a given side with a rotation
     */
    public static int getTextureOnSide(int side, ForgeDirection direction) {
        switch (direction) {
            case NORTH:
                return sideAndFacingToSpriteOffset[side][3];
            case SOUTH:
                return sideAndFacingToSpriteOffset[side][2];
            case WEST:
                return sideAndFacingToSpriteOffset[side][5];
            case EAST:
                return sideAndFacingToSpriteOffset[side][4];
            case UP:
                return sideAndFacingToSpriteOffset[side][0];
            case DOWN:
                return sideAndFacingToSpriteOffset[side][1];
            default:
                return 0;
        }
    }

    /**
     * Return which direction the player is looking at
     */
    public static ForgeDirection getPlayerSight(EntityLivingBase player) {
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);

        if (pitch >= 65)
            return ForgeDirection.DOWN;  //1

        if (pitch <= -65)
            return ForgeDirection.UP;    //0

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
     * Get a ForgeDirection from a byte, used in network packets
     *
     * @see net.minecraftforge.common.util.ForgeDirection
     */
    public static ForgeDirection byte2Direction(byte byteData) {
        switch (byteData) {
            case 2:
                return ForgeDirection.NORTH;
            case 0:
                return ForgeDirection.SOUTH;
            case 1:
                return ForgeDirection.WEST;
            case 3:
                return ForgeDirection.EAST;
            case 4:
                return ForgeDirection.UP;
            case 5:
                return ForgeDirection.DOWN;
            default:
                return null;
        }
    }

    /**
     * Convert a ForgeDirection to a byte, used in network packets
     *
     * @see net.minecraftforge.common.util.ForgeDirection
     */
    public static byte direction2Byte(ForgeDirection direction) {
        switch (direction) {
            case NORTH:
                return 2;
            case SOUTH:
                return 0;
            case WEST:
                return 1;
            case EAST:
                return 3;
            case UP:
                return 4;
            case DOWN:
                return 5;
            default:
                return 0;
        }
    }

    /**
     * Internal use only! [side][facing]
     */
    public static byte[][] sideAndFacingToSpriteOffset = new byte[][] {
            { 3, 2, 0, 0, 0, 0 },
            { 2, 3, 1, 1, 1, 1 },
            { 1, 1, 3, 2, 5, 4 },
            { 0, 0, 2, 3, 4, 5 },
            { 4, 5, 4, 5, 3, 2 },
            { 5, 4, 5, 4, 2, 3 }
    };

    // Block/Item

    /**
     * @param name The name of the block.
     *
     * @return The block or null if not found
     */
    public static Block getBlock(String name) {
        return GameRegistry.findBlock(MODID, name);
    }

    /**
     * @param name The name of the item.
     *
     * @return The item or null if not found
     */
    public static Item getItem(String name) {
        return GameRegistry.findItem(MODID, name);
    }
}
