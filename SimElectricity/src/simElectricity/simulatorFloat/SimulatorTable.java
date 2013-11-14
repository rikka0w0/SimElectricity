package simElectricity.simulatorFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.IBaseComponent;
import simElectricity.API.IConductor;
import simElectricity.API.IPowerSink;
import simElectricity.API.IPowerSource;

public class SimulatorTable {
	private List<Node> unknownVoltageNodes = new ArrayList<Node>();
	private List<Map<Node, Float>> resToOtherNodes = new ArrayList<Map<Node, Float>>();
	private List<TileEntity> tileIndex = new ArrayList<TileEntity>();

	public List<Node> getUnknownVoltageNodes() {
		return unknownVoltageNodes;
	}

	public List<Map<Node, Float>> getResToOtherNodes() {
		return resToOtherNodes;
	}

	public static TileEntity[] getNeighboringComponents(TileEntity component) {
		TileEntity[] result = new TileEntity[7];
		int i = 0;
		TileEntity tmp;

		tmp = component.worldObj.getBlockTileEntity(component.xCoord + 1, component.yCoord, component.zCoord);
		if (tmp instanceof IBaseComponent) {
			result[i++] = tmp;
		}
		tmp = component.worldObj.getBlockTileEntity(component.xCoord - 1, component.yCoord, component.zCoord);
		if (tmp instanceof IBaseComponent) {
			result[i++] = tmp;
		}
		tmp = component.worldObj.getBlockTileEntity(component.xCoord, component.yCoord + 1, component.zCoord);
		if (tmp instanceof IBaseComponent) {
			result[i++] = tmp;
		}
		tmp = component.worldObj.getBlockTileEntity(component.xCoord, component.yCoord - 1, component.zCoord);
		if (tmp instanceof IBaseComponent) {
			result[i++] = tmp;
		}
		tmp = component.worldObj.getBlockTileEntity(component.xCoord, component.yCoord, component.zCoord + 1);
		if (tmp instanceof IBaseComponent) {
			result[i++] = tmp;
		}
		tmp = component.worldObj.getBlockTileEntity(component.xCoord, component.yCoord, component.zCoord - 1);
		if (tmp instanceof IBaseComponent) {
			result[i++] = tmp;
		}

		return result;
	}

	public static int getComponentType(TileEntity component) {
		if (!(component instanceof IPowerSource)) {
			return IBaseComponent.powerSource;
		} else if (!(component instanceof IPowerSink)) {
			return IBaseComponent.powerSink;
		} else if (!(component instanceof IConductor)) {
			return IBaseComponent.conductor;
		}
		return 0;
	}
	
	public int add(TileEntity component) {
		getNeighboringComponents(component);
		return 0;
	}

	public void del(int nodeIndex) {

	}
}
