package simElectricity.API.EnergyTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VirtualConductor implements IConductor {
	
	private static Map<IConductor, VirtualConductor> map = new HashMap<IConductor, VirtualConductor>();
	
	public static VirtualConductor getVirtualConductor(IConductor conductor) {
		return map.get(conductor);
	}
	
	public static boolean conductorInVirtual(IConductor conductor) {
		return map.containsKey(conductor);
	}
	
	public static Set<IConductor> allConductorInVirtual() {
		return map.keySet();
	}
	
	public static void mapClear() {
		map.clear();
	}
	
	private List<IConductor> contains = new ArrayList<IConductor>();
	private IBaseComponent[] connections = {null,null};
	private float totalResistance = 0;

	@Override
	public float getResistance() {
		return totalResistance;
	}

	@Override
	public int getMaxPowerDissipation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onOverloaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getInsulationBreakdownVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInsulationBreakdown() {
		// TODO Auto-generated method stub

	}

	public boolean append(IConductor conductor) {
		boolean result = this.contains.add(conductor);
		if(result)
		{
			totalResistance += conductor.getResistance();
			this.map.put(conductor, this);
			
			if(connections[0] == conductor)
				connections[0] = null;
			else if(connections[1] == conductor)
				connections[1] = null;
		}
		
		return result;
	}
	
	public void clear() {
		for (IConductor iConductor : contains) {
			map.remove(iConductor);
		}
		
		this.contains.clear();		
	}

	public boolean appendConnection(IBaseComponent baseCompoent) {
		boolean result = true;
//		if(baseCompoent instanceof IConductor)
			if(contains.contains(baseCompoent))
				return false;
		
		if(connections[0] == null)
			connections[0] = baseCompoent;
		else if(connections[1] == null)
			connections[1] = baseCompoent;
		else
			result = false;
		
		return result;
	}
	
	public IBaseComponent getConnection(int index) {
		return connections[index];
	}
}
