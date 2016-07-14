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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


/**
 * A baka replacement of JGraph
 *
 * @author <rikka0w0>
 */
public class BakaGraph<T> {
    private Map<T, LinkedList<T>> graph;

    public BakaGraph() {
        graph = new HashMap<T, LinkedList<T>>();
    }

    /**
     * Return a exact copy of this node graph
     */
    @Override
    public Object clone(){
        BakaGraph result = new BakaGraph();
        result.graph.putAll(graph);
        return result;
    }
    
    /**
     * Delete every vertex and edges within this graph
     */
    public void clear(){
    	graph.clear();
    }

    /**
     * Return existing nodes in the graph
     */
    public Set<T> vertexSet() {
        return graph.keySet();
    }

    /**
     * Return a list neighbors of a node
     */
    public LinkedList<T> neighborListOf(T node) {
        return graph.get(node);
    }

    /**
     * Add a vertex into the graph
     */
    public void addVertex(T node) {
        if (graph.containsKey(node))
            return;

        graph.put(node, new LinkedList<T>());
    }

    /**
     * Add a neighbor to a vertex
     */
    public void addEdge(T node, T neighbor) {
        if (!graph.containsKey(node))
            return;
        
        if (!graph.containsKey(neighbor))
            return;

        if (!neighborListOf(node).contains(neighbor))
            neighborListOf(node).add(neighbor);

        if (!neighborListOf(neighbor).contains(node))
            neighborListOf(neighbor).add(node);
    }

    /**
     * Remove a vertex
     */
    public void removeVertex(T node) {
        if (!graph.containsKey(node))
            return;

        //Remove this node from its neighbors' list
        for (T thisNode : neighborListOf(node)) {
            if (neighborListOf(thisNode).contains(node))
                neighborListOf(thisNode).remove(node);
        }

        graph.remove(node);
    }
    
    /**
     * Remove an edge
     */
    public void removeEdge(T node, T neighbor) {
        if (!graph.containsKey(node))
            return;
        
        if (!graph.containsKey(neighbor))
            return;

        
        
        if (neighborListOf(node).contains(neighbor))
            neighborListOf(node).remove(neighbor);

        if (neighborListOf(neighbor).contains(node))
            neighborListOf(neighbor).remove(node);
    }
    
    /**
     * @return the number of vertexes within the graph
     */
    public int size(){
    	return graph.size();
    }
}
