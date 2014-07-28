package simElectricity.Common.EnergyNet;

import java.util.HashMap;
import java.util.Map;

import simElectricity.API.EnergyTile.IBaseComponent;

public class ComponentsGraph {
	//tell me which graph have this component
	public static Map<IBaseComponent, ComponentsGraph> componentsMap = new HashMap<IBaseComponent, ComponentsGraph>(); 
	
    private BakaGraph tileEntityGraph = new BakaGraph();
    private BakaGraph optimizedGraph =  (BakaGraph) tileEntityGraph.clone();

    public ComponentsGraph(IBaseComponent firstNode) {
    	componentsMap.put(firstNode, this);
    }
    
    public static boolean connect(IBaseComponent a, IBaseComponent b) {
    	//a and b is exists node, need merge graph;
    	//To-do
    	
    	//a or b is new node
    	ComponentsGraph componentsGraph = componentsMap.get(a);
    	IBaseComponent newNode = b;
    	if(componentsGraph == null) {
    		componentsGraph = componentsMap.get(b);
    		newNode = a;
    	}
    	//a and b is new node, failed
    	if(componentsGraph == null)
    		return false;
    	
    	componentsGraph.addVertex(newNode);
    	componentsGraph.addEdge(a, b);
    	
    	return true;
    }
    
    private void addVertex(IBaseComponent vertex) {
    	tileEntityGraph.addVertex(vertex);
    	optimizedGraph.addVertex(vertex);
    }
    
    private void addEdge(IBaseComponent a, IBaseComponent b) {
    	tileEntityGraph.addEdge(a, b);
    	optimizedGraph.addEdge(a, b);
    }
}
