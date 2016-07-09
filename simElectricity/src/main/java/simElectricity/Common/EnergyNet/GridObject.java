package simElectricity.Common.EnergyNet;

import net.minecraft.nbt.NBTTagCompound;

public class GridObject {
	public GridData gridData;
	public String coord = "";
	public int maxConnection;
	public int numberOfConnections;
	public GridObject[] connectedObjects = new GridObject[3];
	
	public double R1;
	public double R2;
	public double R3;
	
	//Only used during readFromNBT stage
	private String C1 = "";
	private String C2 = "";
	private String C3 = "";
	
	public void addConnection(GridObject object){
		for (int i=0; i<this.maxConnection; i++){
			if (this.connectedObjects == null){
				this.connectedObjects[i] = object;
				numberOfConnections++;
				return;
			}
		}		
	}
	
	public void delConnection(GridObject object){
		for (int i=0; i<this.maxConnection; i++){
			if (this.connectedObjects[i] == object){
				this.connectedObjects[i] = null;
				numberOfConnections--;
			}
		}			
	}
	
	public void resolveConnections(GridData gridData){
		this.gridData = gridData;
		
		this.numberOfConnections = 0;
		if (this.C1!=""){
			this.connectedObjects[numberOfConnections] = gridData.gridObjects.get(this.C1);
			numberOfConnections++;
		}
		if (this.C2!=""){
			this.connectedObjects[numberOfConnections] = gridData.gridObjects.get(this.C2);
			numberOfConnections++;
		}
		if (this.C3!=""){
			this.connectedObjects[numberOfConnections] = gridData.gridObjects.get(this.C3);
			numberOfConnections++;
		}
	}
	
	public void fromNBT(NBTTagCompound nbt){
		this.coord = nbt.getString("coord");
		this.maxConnection = nbt.getInteger("maxConnection");
		this.C1 = nbt.getString("C1");
		this.R1 = nbt.getDouble("R1");
		this.C2 = nbt.getString("C2");
		this.R1 = nbt.getDouble("R2");
		this.C3 = nbt.getString("C3");
		this.R1 = nbt.getDouble("R3");
	}
	
	public void toNBT(NBTTagCompound nbt){
		nbt.setString("coord", this.coord);	
		nbt.setInteger("maxConnection", this.maxConnection);
		
		if (this.connectedObjects[0]!=null){
			nbt.setString("C1", this.connectedObjects[0].coord);
			nbt.setDouble("R1", this.R1);
		}
		if (this.connectedObjects[1]!=null){
			nbt.setString("C2", this.connectedObjects[1].coord);
			nbt.setDouble("R1", this.R1);
		}
		if (this.connectedObjects[2]!=null){
			nbt.setString("C3", this.connectedObjects[2].coord);
			nbt.setDouble("R1", this.R1);
		}
	}
}
