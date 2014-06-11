package simElectricity.API;

import simElectricity.EnergyNet;
import simElectricity.mod_SimElectricity;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.Events.TileAttachEvent;
import simElectricity.API.Events.TileChangeEvent;
import simElectricity.API.Events.TileDetachEvent;
import simElectricity.API.Events.TileRejoinEvent;
import simElectricity.Network.PacketTileEntityFieldUpdate;
import simElectricity.Network.PacketTileEntitySideUpdate;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class Util {
	/** Creative Tab for SimElectricity project */
	public static CreativeTabs SETab;
	
	//Energy net-------------------------------------------------------------------------------------------------------------------------------
	/** Post a TileAttachEvent for a tileEntity */
	public static void postTileAttachEvent(TileEntity te){MinecraftForge.EVENT_BUS.post(new TileAttachEvent(te));}
	/** Post a TileChangeEvent for a tileEntity */
	public static void postTileChangeEvent(TileEntity te){MinecraftForge.EVENT_BUS.post(new TileChangeEvent(te));}
	/** Post a TileDetachEvent for a tileEntity */
	public static void postTileDetachEvent(TileEntity te){MinecraftForge.EVENT_BUS.post(new TileDetachEvent(te));}
	/** Post a TileRejoinEvent for a tileEntity */
	public static void postTileRejoinEvent(TileEntity te){MinecraftForge.EVENT_BUS.post(new TileRejoinEvent(te));}
		
	/** Calculate the energy output from a IEnergyTile in one tick (1/20 second)*/
	public static float getWorkDonePerTick(IEnergyTile Tile){
		if(Tile.getOutputVoltage()>0){            //Energy Source
			return (float) (0.2*Util.getVoltage(Tile)*Util.getCurrent(Tile));
		}else{                                    //Energy Sink
			return 0;  				
		}	
	}
	
	/** Calculate the consumed power for a given EnergyTile*/
	public static float getPower(IEnergyTile Tile){
		if(Tile.getOutputVoltage()>0){            //Energy Source
			return ((Tile.getOutputVoltage()-getVoltage(Tile))*(Tile.getOutputVoltage()-getVoltage(Tile)))/Tile.getResistance(); 
		}else{                                    //Energy Sink
			return getVoltage(Tile)*getVoltage(Tile)/Tile.getResistance();    				
		}
	}

	/** Calculate the input/output for a given EnergyTile*/
	public static float getCurrent(IEnergyTile Tile){
		if(Tile.getOutputVoltage()>0){            //Energy Source
			return (Tile.getOutputVoltage()-getVoltage(Tile))/Tile.getResistance(); 
		}else{                                    //Energy Sink
			return getVoltage(Tile)/Tile.getResistance();    				
		}
	}
	
	/** Calculate the volage of a given EnergyTile RELATIVE TO GROUND! */
	public static float getVoltage(IBaseComponent Tile){
		if (EnergyNet.getForWorld(((TileEntity)Tile).getWorldObj()).voltageCache.containsKey(Tile))
			return EnergyNet.getForWorld(((TileEntity)Tile).getWorldObj()).voltageCache.get(Tile);
		else
			return 0;
	}
	
	//Network & Sync------------------------------------------------------------------------------------------------------------------------
	/** Update a client tileEntity field from the server */
	public static void updateTileEntityField(TileEntity te,String field){
		mod_SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntityFieldUpdate(te,field),te.getWorldObj().getWorldInfo().getVanillaDimension());
	}
	
	/** Update a server tileEntity field from a client */
	public static void updateTileEntityFieldToServer(TileEntity te,String field){
		mod_SimElectricity.instance.packetPipeline.sendToServer(new PacketTileEntityFieldUpdate(te,field));
	}
	
	/** Update a tileEntity's facing on client side */
	public static void updateTileEntityFacing(TileEntity te){
		mod_SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntitySideUpdate(te,(byte)0),te.getWorldObj().getWorldInfo().getVanillaDimension());
	}
	
	/** Update a tileEntity's functional side on client side */
	public static void updateTileEntityFunctionalSide(TileEntity te){
		mod_SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntitySideUpdate(te,(byte)1),te.getWorldObj().getWorldInfo().getVanillaDimension());
	}	
	
	//Util
	/** Post some text in chat box(Other player cannot see it) */
    public static void chat(EntityPlayer player,String text){
    	player.addChatMessage(new ChatComponentText(text));
    }
	//Facing and Rendering------------------------------------------------------------------------------------------------------------------
	/** Update a block rendering after 10 ticks */
	public static void scheduleBlockUpdate(TileEntity te){ scheduleBlockUpdate(te,10);}
	
	/** Update a block rendering after some ticks */
	public static void scheduleBlockUpdate(TileEntity te,int time){
		if(te==null)
			return;
		te.getWorldObj().scheduleBlockUpdate(te.xCoord, te.yCoord, te.zCoord, te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord), time);
	}
	
	/** Get the texture index for a given side with a rotation */
	public static int getTextureOnSide(int side,ForgeDirection direction){
		switch (direction){
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
	
	/** Return which direction the player is looking at */
	public static ForgeDirection getPlayerSight(EntityLivingBase player){
        int heading = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        
        if (pitch >= 65)
        	return ForgeDirection.DOWN;  //1
        
        if (pitch<=-65)
        	return ForgeDirection.UP;    //0
        
        switch (heading){
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
	
	/** Get a ForgeDirection from a byte, used in network packets */
	public static ForgeDirection byte2Direction(byte byteData){
        switch (byteData){
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
	
	/** Convert a ForgeDirection to a byte, used in network packets */
	public static byte direction2Byte(ForgeDirection direction){
        switch (direction){
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
	
	/** Internal use only! [side][facing] */
	public static byte[][] sideAndFacingToSpriteOffset = new byte[][]{
            {3, 2, 0, 0, 0, 0}, 
            {2, 3, 1, 1, 1, 1}, 
            {1, 1, 3, 2, 5, 4}, 
            {0, 0, 2, 3, 4, 5}, 
            {4, 5, 4, 5, 3, 2}, 
            {5, 4, 5, 4, 2, 3}
    };
}
