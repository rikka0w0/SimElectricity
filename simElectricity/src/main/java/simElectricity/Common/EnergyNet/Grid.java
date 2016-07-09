package simElectricity.Common.EnergyNet;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import simElectricity.API.EnergyTile.IBaseComponent;


public class Grid {
	public GridData gridData;
	
	public static String coord2str(int x, int y, int z){
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
	}
	
	public static int[] str2coord(String str){
		String[] sstr = str.split(",");
		return new int[]{Integer.parseInt(sstr[0]),
						 Integer.parseInt(sstr[1]),
						 Integer.parseInt(sstr[2])};
	}
	
	
	public Grid(World world){
		gridData = GridData.get(world, this);
	}
	
	/*
	public double getResistance(IBaseComponent node, IBaseComponent neighbor){
		GridNode gNode = (GridNode)node;
		
		if (gridData.getT1(gNode) == neighbor)
			return gNode.R1;
		else if (gridData.getT2(gNode) == neighbor)
			return gNode.R2;
		else if (gridData.getT3(gNode) == neighbor)
			return gNode.R3;
		else
			return 0; //Exception!!!!
	}*/
	
	public void attachNode(TileEntity te){
		String coord = coord2str(te.xCoord, te.yCoord, te.zCoord);
		if (gridData.gridObjects.containsKey(coord)){
			return;
		}
		
		GridNode node = new GridNode();
		node.coord = coord;
		gridData.gridObjects.put(coord, node);
	}
	
	public void detachNode(TileEntity te){
		String coord = coord2str(te.xCoord, te.yCoord, te.zCoord);
		if (gridData.gridObjects.containsKey(coord)){
			gridData.gridObjects.remove(coord);
		}
	}
	

}
