package simElectricity.Common.EnergyNet;

import java.util.List;
import java.util.Set;

import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;

public class Optimizer {	
	public static void optimize(BakaGraph optimizedTileEntityGraph){
        if (ConfigManager.optimizeNodes) {
            SEUtils.logInfo("raw:" + optimizedTileEntityGraph.vertexSet().size() + " nodes");
            VirtualConductor.mapClear();
            while (mergeIConductorNode(optimizedTileEntityGraph)) ;
            SEUtils.logInfo("optimized:" + optimizedTileEntityGraph.vertexSet().size() + " nodes");
        }
	}
	
	//Optimization--------------------------------------------------------------------
    private static boolean nodeIsLine(IBaseComponent conductor, BakaGraph optimizedTileEntityGraph) {
        if (conductor.getClass() == VirtualConductor.class)
            return false;
        if (!(conductor instanceof IConductor))
            return false;
        if (VirtualConductor.conductorInVirtual((IConductor) conductor))
            return false;

        List<IBaseComponent> list = optimizedTileEntityGraph.neighborListOf(conductor);
        for (IBaseComponent iBaseComponent : list) {
            if (!(iBaseComponent instanceof IConductor))
                return false;
        }

        return list.size() == 2;
    }

    private static VirtualConductor floodFill(IBaseComponent conductor, VirtualConductor virtualConductor, BakaGraph optimizedTileEntityGraph) {
        if (nodeIsLine(conductor, optimizedTileEntityGraph)) {
            if (virtualConductor == null)
                virtualConductor = new VirtualConductor();
            virtualConductor.append((IConductor) conductor);
            List<IBaseComponent> neighborList = optimizedTileEntityGraph.neighborListOf(conductor);
            for (IBaseComponent iBaseComponent : neighborList) {
                floodFill(iBaseComponent, virtualConductor, optimizedTileEntityGraph);
            }
        } else if (virtualConductor != null) {
            virtualConductor.appendConnection(conductor);
        }

        return virtualConductor;
    }

    private static boolean mergeIConductorNode(BakaGraph optimizedTileEntityGraph) {
        boolean result = false;
        VirtualConductor virtualConductor = null;

        Set<IBaseComponent> iBaseComponentSet = optimizedTileEntityGraph.vertexSet();
        for (IBaseComponent iBaseComponent : iBaseComponentSet) {
            virtualConductor = floodFill(iBaseComponent, virtualConductor, optimizedTileEntityGraph);

            if (virtualConductor != null) {
                break;
            }
        }

        if (virtualConductor != null) {
            optimizedTileEntityGraph.addVertex(virtualConductor);
            optimizedTileEntityGraph.addEdge(virtualConductor, virtualConductor.getConnection(0));
            optimizedTileEntityGraph.addEdge(virtualConductor, virtualConductor.getConnection(1));

            for (IConductor conductor : VirtualConductor.allConductorInVirtual())
                optimizedTileEntityGraph.removeVertex(conductor);

            result = true;
        }

        return result;
    }
}
