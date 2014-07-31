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

import simElectricity.API.EnergyTile.IBaseComponent;

import java.util.*;

/**
 * A baka replacement of JGraph
 *
 * @author <rikka0w0>
 */
public class BakaGraph {
    protected Map<IBaseComponent, ArrayList<IBaseComponent>> graph;

    public BakaGraph() {
        graph = new HashMap<IBaseComponent, ArrayList<IBaseComponent>>();
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
     * Return existing nodes in the graph
     */
    public Set<IBaseComponent> vertexSet() {
        return graph.keySet();
    }

    /**
     * Return a list neighbors of a node
     */
    public List<IBaseComponent> neighborListOf(IBaseComponent node) {
        return graph.get(node);
    }

    /**
     * Add a vertex into the graph
     */
    public void addVertex(IBaseComponent node) {
        if (graph.containsKey(node))
            return;

        graph.put(node, new ArrayList<IBaseComponent>());
    }

    /**
     * Add a neighbor to a vertex
     */
    public void addEdge(IBaseComponent node, IBaseComponent neighbor) {
        if (!graph.containsKey(node))
            return;

        if (!neighborListOf(node).contains(neighbor))
            neighborListOf(node).add(neighbor);

        if (!neighborListOf(neighbor).contains(node))
            neighborListOf(neighbor).add(node);
    }

    /**
     * Remove a vertex
     */
    public void removeVertex(IBaseComponent node) {
        if (!graph.containsKey(node))
            return;

        //Remove this node from its neighbors' list
        for (IBaseComponent thisNode : neighborListOf(node)) {
            if (neighborListOf(thisNode).contains(node))
                neighborListOf(thisNode).remove(node);
        }

        graph.remove(node);
    }
}
