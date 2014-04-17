package simElectricity.API;

import simElectricity.EnergyNet;
import simElectricity.mod_SimElectricity;
import simElectricity.Network.PacketTileEntityFieldUpdate;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class Util {
	//Creative Tab for SimElectricity project
	public static CreativeTabs SETab;
	
	//Internal use only! [side][facing]
	public static byte[][] sideAndFacingToSpriteOffset = new byte[][]{
            {
                3, 2, 0, 0, 0, 0
            }, {
                2, 3, 1, 1, 1, 1
            }, {
                1, 1, 3, 2, 5, 4
            }, {
                0, 0, 2, 3, 4, 5
            }, {
                4, 5, 4, 5, 3, 2
            }, {
                5, 4, 5, 4, 2, 3
            }
    };
	
	public static void scheduleBlockUpdate(TileEntity te){
		scheduleBlockUpdate(te,10);
	}
	
	public static void scheduleBlockUpdate(TileEntity te,int time){
		te.getWorldObj().scheduleBlockUpdate(te.xCoord, te.yCoord, te.zCoord, te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord), time);
	}
	
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
	
	public static float getPower(IEnergyTile Tile){
		if(Tile.getOutputVoltage()>0){//Energy Source
			return ((Tile.getOutputVoltage()-getVoltage(Tile))*(Tile.getOutputVoltage()-getVoltage(Tile)))/Tile.getResistance(); 
		}else{//Energy Sink
			return getVoltage(Tile)*getVoltage(Tile)/Tile.getResistance();    				
		}
	}

	public static float getCurrent(IEnergyTile Tile){
		if(Tile.getOutputVoltage()>0){//Energy Source
			return (Tile.getOutputVoltage()-getVoltage(Tile))/Tile.getResistance(); 
		}else{//Energy Sink
			return getVoltage(Tile)/Tile.getResistance();    				
		}
	}
	
	public static float getVoltage(IBaseComponent Tile){
		if (EnergyNet.getForWorld(((TileEntity)Tile).getWorldObj()).voltageCache.containsKey(Tile))
			return EnergyNet.getForWorld(((TileEntity)Tile).getWorldObj()).voltageCache.get(Tile);
		else
			return 0;
	}
	
	public static void updateTileEntityField(TileEntity te,String field){
		mod_SimElectricity.instance.packetPipeline.sendToDimension(new PacketTileEntityFieldUpdate(te,field),te.getWorldObj().getWorldInfo().getVanillaDimension());
	}
}
