/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.Common.EnergyNet;

import java.util.LinkedList;
import java.util.Set;

import simElectricity.Common.EnergyNet.Components.SEComponent;


/**
 * A baka replacement of JGraph
 *
 * @author <rikka0w0>
 */
public class BakaGraph {
    private LinkedList<SEComponent> nodes;
    
    public BakaGraph() {
    	nodes = new LinkedList<SEComponent>();
    }
    
    /**
     * Return a exact copy of this node graph
     
    @Override
    public Object clone(){
        BakaGraph result = new BakaGraph();
        result.graph.putAll(graph);
        return result;
    }
    */
    
    /**
     * Delete every vertex and edges within this graph
    
    public void clear(){
    	graph.clear();
    }
     */
    
    /**
     * Return existing nodes in the graph
     */
    public LinkedList<SEComponent> getNodes() {
        return nodes;
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
        if (nodes.contains(node))
            return;

        nodes.add(node);
    }

    /**
     * Add a neighbor to a vertex
     */
    public void addEdge(SEComponent node, SEComponent neighbor) {
        if (!nodes.contains(node))
            return;
        
        if (!nodes.contains(neighbor))
            return;

        if (!node.neighbors.contains(neighbor))
        	node.neighbors.add(neighbor);

        if (!neighbor.neighbors.contains(node))
            neighbor.neighbors.add(node);
    }

    /**
     * Remove a vertex
     */
    public void removeVertex(SEComponent node) {
        if (!nodes.contains(node))
            return;

        //Remove this node from its neighbors' list
        for (SEComponent neighbor: node.neighbors){
        	if (neighbor.neighbors.contains(node))
        		neighbor.neighbors.remove(node);
        }

        node.neighbors.clear();
        nodes.remove(node);
    }
    
    /**
     * Remove an edge
     */
    public void removeEdge(SEComponent node, SEComponent neighbor) {
        if (!nodes.contains(node))
            return;
        
        if (!nodes.contains(neighbor))
            return;

        
        
        if (neighbor.neighbors.contains(neighbor))
        	neighbor.neighbors.remove(neighbor);

        if (neighbor.neighbors.contains(node))
        	neighbor.neighbors.remove(node);
    }
    
    /**
     * Remove all edges of a node, returns a list of previous neighbors
     */
    public LinkedList<SEComponent> removeAllEdges(SEComponent node){
        if (!nodes.contains(node))
            return null;
        
        LinkedList<SEComponent> ret = (LinkedList<SEComponent>) node.neighbors.clone();
        
        //Delete all record on the selected node
        node.neighbors.clear();
        
        //Delete this node from its neighbors' list
        for (SEComponent neighbor: ret){
        	if (neighbor.neighbors.contains(node))
        		neighbor.neighbors.remove(node);
        }
        
        return ret;
    }
    
    /**
     * @return the number of vertexes within the graph
     */
    public int size(){
    	return nodes.size();
    }
}
