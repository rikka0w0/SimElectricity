/**
 * This source code contains code pieces from Lambda Innovation
 */
package simElectricity.Common.EnergyNet;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class GridData extends WorldSavedData{
	public HashMap<String,GridObject> gridObjects = new HashMap<String,GridObject>();
	
	World world;
	Grid grid;
	static String getID(World world) { 
 		return "SE" + world.provider.dimensionId; 
 	} 
	
	public static GridData get(World world, Grid grid) { 
		 if(world.isRemote) { 
		 	throw new RuntimeException("Not allowed to create WiWorldData in client"); 
		 } 
		 
		 String id = getID(world); 
		 GridData ret = (GridData) world.loadItemData(GridData.class, id); 
		 if(ret == null) { 
		 	world.setItemData(id, ret = new GridData(id)); 
		 } 
		 ret.world = world; 
		 ret.grid = grid;
		 return ret; 
	} 

	
	public GridData(String p_i2141_1_) {super(p_i2141_1_);}
	
	/*
	public boolean hasLine(String coord1, String coord2){
		for (Iterator<GridTransmissionLine> i = lines.iterator(); i.hasNext();){
			GridTransmissionLine cur = i.next();
			if ((cur.a==coord1&&cur.b==coord2)||(cur.a==coord2&&cur.b==coord1))
				return true;
		}
		return false;
	}
	*/
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {		
		NBTTagList NBTObjects = nbt.getTagList("Objects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < NBTObjects.tagCount(); i++) {
			NBTTagCompound compound = NBTObjects.getCompoundTagAt(i);
			if (compound.getByte("type") == 0){ //Tower
				GridTower node = new GridTower(compound); 
				gridObjects.put(node.coord, node);					
			}else if (compound.getByte("type") == 1){ //Node
				GridNode node = new GridNode(compound); 
				gridObjects.put(node.coord, node);				
			}
		}
		

		
		for (Iterator<GridObject> i = gridObjects.values().iterator(); i.hasNext(); ){
			GridObject curNode = i.next();
			curNode.resolveConnections(this);
		}
		
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {	
		NBTTagList NBTNodes = new NBTTagList();
		for (Iterator<GridObject> i = gridObjects.values().iterator(); i.hasNext(); ){
			NBTTagCompound tag = new NBTTagCompound();
			i.next().toNBT(tag);
			NBTNodes.appendTag(tag);
		}
		nbt.setTag("Objects", NBTNodes);
	}
	
	@Override 
	public boolean isDirty() { 
	   	return true; 
	} 
}
