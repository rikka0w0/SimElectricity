package simElectricity.Common.EnergyNet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import simElectricity.API.Tile.ISECableTile;
import simElectricity.Common.EnergyNet.Components.*;

public class SEGraph {
	private LinkedList<SEComponent> components;
	private LinkedList<SEComponent> wires;	
	
    public SEGraph() {
    	components = new LinkedList<SEComponent>();
    	wires = new LinkedList<SEComponent>();
    }
	
    public int size(){
    	return components.size() + wires.size();
    }
    
	public boolean isWire(SEComponent node){
		if (node instanceof Cable)
			return true;
		if (node instanceof GridNode){
			if(((GridNode)node).getType() == GridNode.ISEGridNode_Wire)
				return true;
		}
		return false;
	}
	
	public boolean containsNode(SEComponent node){
		if (components.contains(node))
			return true;
		else if (wires.contains(node))
			return true;
		else
			return false;
	}
	
    /**
     * Return a list neighbors of a node
     */
    public LinkedList<SEComponent> neighborListOf(SEComponent node) {
        return node.neighbors;
    }
	
    /**
     * Add a vertex into the graph
     */
    public void addVertex(SEComponent node) {
        if (containsNode(node))
            return;

        if (isWire(node))
        	wires.add(node);
        else
        	components.add(node);
    }
    
    /**
     * Add a neighbor to a vertex
     */
    public void addEdge(SEComponent node, SEComponent neighbor) {
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
    public void removeVertex(SEComponent node) {
        if (!containsNode(node))
            return;

        //Map<ISESimulatable, LinkedList<ISESimulatable>> graph = isWire(node) ? wires : components;
        
        //Remove this node from its neighbors' list
        for (SEComponent neighbor: node.neighbors){
        	LinkedList<SEComponent> list = neighbor.neighbors;
        	if (list.contains(node))
        		list.remove(node);
        }

        node.neighbors.clear();
        (isWire(node) ? wires : components).remove(node);
    }
    
    /**
     * Remove an edge
     */
    public void removeEdge(SEComponent node, SEComponent neighbor) {
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
    public LinkedList<SEComponent> removeAllEdges(SEComponent node){
        if (!containsNode(node))
            return null;
        
        LinkedList<SEComponent> neighbors = node.neighbors;
        LinkedList<SEComponent> ret = (LinkedList<SEComponent>) neighbors.clone();
        
        //Delete all record on the selected node
        neighbors.clear();
        
        //Delete this node from its neighbors' list
        for (SEComponent neighbor: ret){
        	LinkedList<SEComponent> list = neighbor.neighbors;
        	if (list.contains(node))
        		list.remove(node);
        }
        
        return ret;
    }
       
    public static double calcR(SEComponent cur, SEComponent neighbor){
    	if (cur instanceof Cable){
    		Cable curConductor = (Cable) cur;
    		if (neighbor instanceof Cable){
    			return curConductor.data.getResistance() + ((Cable) neighbor).data.getResistance();
    		}else if (neighbor instanceof Junction){
    			return curConductor.data.getResistance() + ((Junction) neighbor).data.getResistance(curConductor);
    		}else{
    			return curConductor.data.getResistance();
    		}
    	} else if (cur instanceof Junction){
    		Junction curJunction = (Junction) cur;
    		if (neighbor instanceof Cable){
    			return ((Cable) neighbor).data.getResistance() + curJunction.data.getResistance(neighbor);
    		}else if (neighbor instanceof Junction){
    			return curJunction.data.getResistance(neighbor) + ((Junction) neighbor).data.getResistance(curJunction);
    		}else if (neighbor instanceof GridNode){
    			return curJunction.data.getResistance(neighbor);
    		}else{
    			throw new RuntimeException("Unaccptable conntection");
    		}
    	} else if (cur instanceof GridNode){
    		GridNode curGridNode = (GridNode)cur;
    		if (neighbor instanceof Junction){
    			return ((Junction) neighbor).data.getResistance(curGridNode);
    		}else if (neighbor instanceof GridNode){
    			return curGridNode.resistances.get((GridNode)neighbor);
    		}else {
    			throw new RuntimeException("Unaccptable conntection");
    		}
    	} else {
    		if (neighbor instanceof Cable){
    			return ((Cable)neighbor).data.getResistance();
    		}
    	}
    	
    	throw new RuntimeException("Unaccptable conntection");
    }
       
    public LinkedList<SEComponent> getTerminalNodes(){
    	LinkedList<SEComponent> terminalNodes = (LinkedList<SEComponent>) components.clone();

    	for (SEComponent node : terminalNodes){
    		node.visited = false;
    		node.optimizedNeighbors.clear();
			node.optimizedResistance.clear();
    	}
    	
    	for (SEComponent wire : wires){
    		wire.visited = false;
			wire.optimizedNeighbors.clear();
			wire.optimizedResistance.clear();
    		if (wire.neighbors.size()>2){
    			terminalNodes.add(wire);
    		}
    	}
    	
    	//terminalNodes, including 
    	
    	for (SEComponent node : terminalNodes){
    		if (!node.visited){
    			node.visited = true;
    			
    			tryNeighbors: for (SEComponent neighbor: node.neighbors){
    				if (neighbor.visited)
    					continue tryNeighbors;
    				
        			SEComponent reach = neighbor;	//Far reach
        			SEComponent prev = node;
        			double resistance = 0.0D;
        			
        			search: do{
        				if (reach == node){
        					reach = null;	//Can not form an edge
        					break search;	//Avoid circulation
        				}
        				
      				
        				resistance += calcR(prev, reach);
        				
        				if (reach instanceof Junction)
        					break search;	//We have to calculate the voltage of the junction
        				
        		    	if (reach.neighbors.size() == 1){
        		    		if (isWire(reach))
        		    			reach = null;	//A single ended wire can not form an edge
        		    		break search;	//Dead end!
        		    	}
        		    	else if (reach.neighbors.size() == 2){	//Always moveforward!
        		    		if (reach.neighbors.getFirst() == prev){
        		    			reach.visited = true;
        		    			prev = reach;
        		    			reach = reach.neighbors.getLast();
        		    		}
        		    		else if (reach.neighbors.getLast() == prev){
        		    			reach.visited = true;
        		    			prev = reach;
        		    			reach = reach.neighbors.getFirst();
        		    		}
        		    	}
        		    	else if (reach.neighbors.size() > 2){
        		    		//form an edge, so leave "reach" unchanged
        		    		break search;	//We have a cable node that has more than 2 connection
        		    	}        				
        			}while (true);	
        			
        			if (reach != null){
        				Iterator<SEComponent> iteratorON = node.optimizedNeighbors.iterator();
        				Iterator<Double> iteratorR = node.optimizedResistance.iterator();
        				
        				checkDupe: while (iteratorON.hasNext()){
        					double prevR = iteratorR.next();
        					if (iteratorON.next() == reach){
        						//Delete previous edge
                				Iterator<SEComponent> iteratorON2 = reach.optimizedNeighbors.iterator();
                				Iterator<Double> iteratorR2 = reach.optimizedResistance.iterator();
                				delete: while (iteratorON2.hasNext()){
                					iteratorR2.next();
                					if (iteratorON2.next() == node){
                						iteratorR2.remove();
                						iteratorON2.remove();
                						break delete;
                					}
                				}

        						iteratorR.remove();
        						iteratorON.remove();
        						
        						resistance = resistance * prevR / (resistance + prevR);
        						break checkDupe;
        					}
        				}
        				

                		node.optimizedNeighbors.add(reach);
                		node.optimizedResistance.add(resistance);      
                			
                		reach.optimizedNeighbors.add(node);
                		reach.optimizedResistance.add(resistance);        					
        			}
        		}
    		}

    	}
    	
    	return terminalNodes;
    }

    public double R0,R1;
    public SEComponent[] getTerminals(SEComponent wire){
    	R0 = 0;
    	R1 = 0;
    	    	
    	if (wire.neighbors.size() > 2)
    		return null;	//Not an node
    	
    	if (wire.neighbors.size() == 0)
    		return new SEComponent[]{}; 	//No connection, 0V!
    	
    	SEComponent prev = wire;
    	SEComponent head = wire.neighbors.getFirst();
    	
    	search1: do{
    		if (head == wire)
    			return new SEComponent[]{};	//Avoid circulation, 0V
    		
    		R0 += calcR(prev, head);
    		
			if (head instanceof Junction)
				break search1;	//We have to calculate the voltage of the junction
    		
	    	if (head.neighbors.size() == 1){	//Single end
	    		if (isWire(head))
	    			head = null;
	    		break search1;	//Dead end!
	    	}
	    	else if (head.neighbors.size() == 2){	//Always moveforward!
	    		if (head.neighbors.getFirst() == prev){
	    			prev = head;
	    			head = head.neighbors.getLast();
	    		}
	    		else if (head.neighbors.getLast() == prev){
	    			prev = head;
	    			head = head.neighbors.getFirst();
	    		}
	    	}
	    	else if (head.neighbors.size() > 2){
	    		break search1;	//We have a cable node that has more than 2 connection
	    	}
    	}while(true);
    	
    	//head == null -> single end
    	//head != null -> something
    	// A--^--A
    	// A--^--
    	// A--^
    	// ---^
    	if (wire.neighbors.size() == 1){
    		if (head == null)
    			return new SEComponent[]{};		//No connection to other nodes, 0V
    		else
    			return new SEComponent[]{head};	//Equal to the voltage of "head"
    	}
    		
    	//wire.neighbors.size() == 2
    	SEComponent terminal1 = head;
    	prev = wire;
    	head = wire.neighbors.getLast();

    	search1: do{
    		R1 += calcR(prev, head);
    		
			if (head instanceof Junction)
				break search1;	//We have to stop at the junction
    		
	    	if (head.neighbors.size() == 1){	//Single end
	    		if (isWire(head))
	    			head = null;
	    		break search1;	//Dead end!
	    	}
	    	else if (head.neighbors.size() == 2){	//Always moveforward!
	    		if (head.neighbors.getFirst() == prev){
	    			prev = head;
	    			head = head.neighbors.getLast();
	    		}
	    		else if (head.neighbors.getLast() == prev){
	    			prev = head;
	    			head = head.neighbors.getFirst();
	    		}
	    	}
	    	else if (head.neighbors.size() > 2){
	    		break search1;	//We have a cable node that has more than 2 connection
	    	}
    	}while(true);
    	
    	if (terminal1 == null){
    		if (head == null)
    			return new SEComponent[]{};		//No connection to other nodes, 0V
    		else
    			return new SEComponent[]{head};	//Equal to the voltage of "head"
    	}else{
    		if (head == null)
    			return new SEComponent[]{terminal1};	//Equal to the voltage of "head"
    		else
    			return new SEComponent[]{terminal1, head};
    	}
    }
}
