package simElectricity.Common.EnergyNet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISECableTile;

public class SEGraph {
	private Map<ISESimulatable, LinkedList<ISESimulatable>> components;
	private Map<ISESimulatable, LinkedList<ISESimulatable>> wires;
	
    public SEGraph() {
    	components = new HashMap<ISESimulatable, LinkedList<ISESimulatable>>();
    	wires = new HashMap<ISESimulatable, LinkedList<ISESimulatable>>();
    }
	
    public int size(){
    	return components.size() + wires.size();
    }
    
	public boolean isWire(ISESimulatable node){
		if (node instanceof ISECableTile)
			return true;
		if (node instanceof ISEGridNode){
			if(((ISEGridNode)node).getType() == ISEGridNode.ISEGridNode_Wire)
				return true;
		}
		return false;
	}
	
	public boolean containsNode(ISESimulatable node){
		if (components.containsKey(node))
			return true;
		else if (wires.containsKey(node))
			return true;
		else
			return false;
	}
	
    /**
     * Return a list neighbors of a node
     */
    public LinkedList<ISESimulatable> neighborListOf(ISESimulatable node) {
        return isWire(node) ? wires.get(node) : components.get(node);
    }
	
    /**
     * Add a vertex into the graph
     */
    public void addVertex(ISESimulatable node) {
        if (containsNode(node))
            return;

        if (isWire(node))
        	wires.put(node, new LinkedList<ISESimulatable>());
        else
        	components.put(node, new LinkedList<ISESimulatable>());
    }
    
    /**
     * Add a neighbor to a vertex
     */
    public void addEdge(ISESimulatable node, ISESimulatable neighbor) {
        if (!containsNode(node))
            return;
        
        if (!containsNode(neighbor))
            return;

        if (!neighborListOf(node).contains(neighbor))
            neighborListOf(node).add(neighbor);

        if (!neighborListOf(neighbor).contains(node))
            neighborListOf(neighbor).add(node);
    }
    
    /**
     * Remove a vertex
     */
    public void removeVertex(ISESimulatable node) {
        if (!containsNode(node))
            return;

        //Map<ISESimulatable, LinkedList<ISESimulatable>> graph = isWire(node) ? wires : components;
        
        //Remove this node from its neighbors' list
        for (ISESimulatable neighbor: (isWire(node) ? wires : components).get(node)){
        	LinkedList<ISESimulatable> list = (isWire(node) ? wires : components).get(neighbor);
        	if (list.contains(node))
        		list.remove(node);
        }

        (isWire(node) ? wires : components).remove(node);
    }
    
    /**
     * Remove an edge
     */
    public void removeEdge(ISESimulatable node, ISESimulatable neighbor) {
        if (!containsNode(node))
            return;
        
        if (!containsNode(neighbor))
            return;

        if (neighborListOf(node).contains(neighbor))
            neighborListOf(node).remove(neighbor);

        if (neighborListOf(neighbor).contains(node))
            neighborListOf(neighbor).remove(node);
    }
    
    /**
     * Remove all edges of a node, returns a list of previous neighbors
     */
    public LinkedList<ISESimulatable> removeAllEdges(ISESimulatable node){
        if (!containsNode(node))
            return null;
        
        LinkedList<ISESimulatable> neighbors = (isWire(node) ? wires : components).get(node);
        LinkedList<ISESimulatable> ret = (LinkedList<ISESimulatable>) neighbors.clone();
        
        //Delete all record on the selected node
        neighbors.clear();
        
        //Delete this node from its neighbors' list
        for (ISESimulatable neighbor: ret){
        	LinkedList<ISESimulatable> list = (isWire(node) ? wires : components).get(neighbor);
        	if (list.contains(node))
        		list.remove(node);
        }
        
        return ret;
    }
    
    public Set<ISESimulatable> getTerminalNodes(){
    	Set<ISESimulatable> terminalNodes = components.keySet();
    	
    	for (ISESimulatable wire : wires.keySet()){
    		if (wires.get(wire).size()>2){
    			terminalNodes.add(wire);
    		}
    	}
    	
    	
    	
    	return terminalNodes;
    }
}
