package simelectricity.energynet;

import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.SEComponent;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Unweighed graph, using Adjacency lists (SEComponent.neighbors)
 */
public class SEGraph {
    // A list of pointers to registered components and cables
    private final LinkedList<SEComponent> components;
    private final LinkedList<SEComponent> wires;

    private final LinkedList<SEComponent> terminalNodes;

    public SEGraph() {
        this.components = new LinkedList<SEComponent>();
        this.wires = new LinkedList<SEComponent>();

        this.terminalNodes = new LinkedList<SEComponent>();
    }

    ////////////////////////////////////////////////
    ///Optimizer
    ////////////////////////////////////////////////
    private static boolean isWire(SEComponent node) {
        if (node instanceof Cable)
            return true;
        return node instanceof GridNode;
    }

    private static boolean shouldCalcVoltage(SEComponent node) {
        if (node.neighbors.size() > 2)                //A node has more than 2 connections
            return true;
        if (SEGraph.isInterconnectionTerminal(node))        //A interconnection terminal
            return true;

        if (node instanceof Cable && ((Cable) node).hasShuntResistance)
            return true;

        return node instanceof GridNode && ((GridNode) node).type != GridNode.ISEGridNode_Wire;

    }

    private static boolean isInterconnectionTerminal(SEComponent node) {
        if (node instanceof Cable && ((Cable) node).connectedGridNode != null)
            return true;
        else return node instanceof GridNode && ((GridNode) node).interConnection != null;
    }

    //////////////////////////
    /// EnergyTiles
    //////////////////////////

    public static double calcR(SEComponent cur, SEComponent neighbor) {
        if (cur instanceof Cable) {
            Cable curConductor = (Cable) cur;
            if (neighbor instanceof Cable) {
                return curConductor.resistance + ((Cable) neighbor).resistance;
            } else {
                return curConductor.resistance;
            }
        } else if (cur instanceof GridNode) {
            GridNode curGridNode = (GridNode) cur;
            if (neighbor instanceof GridNode) {
                return curGridNode.getResistance((GridNode) neighbor);
            } else {
                throw new RuntimeException("Unaccptable conntection");
            }
        } else {
            if (neighbor instanceof Cable) {
                return ((Cable) neighbor).resistance;
            }
        }

        throw new RuntimeException("Unaccptable conntection");
    }

    /**
     * @return total number of registered tiles (the number of nodes)
     */
    public int size() {
        return this.components.size() + this.wires.size();
    }

    public boolean containsNode(SEComponent node) {
        if (this.components.contains(node))
            return true;
        else return this.wires.contains(node);
    }

    /**
     * Add an EnergyTiles/GridNode into the graph
     */
    public void addVertex(SEComponent node) {
        if (this.containsNode(node))
            return;

        if (SEGraph.isWire(node))
            this.wires.addLast(node);
        else
            this.components.addLast(node);

        node.isValid = true;
    }

    /**
     * Remove a vertex and its edges
     */
    public void removeVertex(SEComponent node) {
        if (!this.containsNode(node))
            return;

        //Mark as dead
        node.isValid = false;

        //Cut possible interconnection
        if (node instanceof Cable)
            this.breakInterconnection((Cable) node);

        //Remove this node from its neighbor list
        for (SEComponent neighbor : node.neighbors) {
            LinkedList<SEComponent> list = neighbor.neighbors;
            if (list.contains(node))
                list.remove(node);
        }

        node.neighbors.clear();
        (SEGraph.isWire(node) ? this.wires : this.components).remove(node);
    }

    /**
     * Add a neighbor to a vertex
     */
    public void addEdge(SEComponent node, SEComponent neighbor) {
        if (!this.containsNode(node))
            return;

        if (!this.containsNode(neighbor))
            return;

        if (!node.neighbors.contains(neighbor))
            node.neighbors.addLast(neighbor);

        if (!neighbor.neighbors.contains(node))
            neighbor.neighbors.addLast(node);
    }

    /**
     * Remove an edge
     */
    public void removeEdge(SEComponent node, SEComponent neighbor) {
        if (!this.containsNode(node))
            return;

        if (!this.containsNode(neighbor))
            return;

        if (neighbor.neighbors.contains(neighbor))
            neighbor.neighbors.remove(neighbor);

        if (neighbor.neighbors.contains(node))
            neighbor.neighbors.remove(node);
    }

    //////////////////////////
    /// Grid
    //////////////////////////
    public void addGridEdge(GridNode node1, GridNode node2, double resistance) {
        if (!this.containsNode(node1))
            return;

        if (!this.containsNode(node2))
            return;

        if (!node1.neighbors.contains(node2)) {
            node1.neighbors.addLast(node2);
            node1.neighborR.addLast(resistance);
        }


        if (!node2.neighbors.contains(node1)) {
            node2.neighbors.addLast(node1);
            node2.neighborR.addLast(resistance);
        }
    }

    public LinkedList<GridNode> removeGridVertex(GridNode gridNode) {
        if (!this.containsNode(gridNode))
            return null;

        //Cut possible interconnection
        this.breakInterconnection(gridNode);

        //Break transformer
        this.breakTransformer(gridNode);

        LinkedList<GridNode> ret = new LinkedList<GridNode>();

        //Delete resistance properties of GridNodes
        for (SEComponent neighbor : gridNode.neighbors) {
            if (neighbor instanceof GridNode) {
                Iterator<SEComponent> iterator1 = neighbor.neighbors.iterator();
                Iterator<Double> iterator2 = ((GridNode) neighbor).neighborR.iterator();

                deleteInfoFromNeighbor:
                while (iterator1.hasNext()) {
                    SEComponent seComponent = iterator1.next();
                    if (seComponent instanceof GridNode) {
                        iterator2.next();
                        if (seComponent == gridNode) {
                            iterator2.remove();
                            iterator1.remove();
                            break deleteInfoFromNeighbor;
                        }
                    }
                }

                ret.add((GridNode) neighbor);
            }
        }

        gridNode.neighbors.clear();
        gridNode.neighborR.clear();
        (SEGraph.isWire(gridNode) ? this.wires : this.components).remove(gridNode);

        return ret;
    }

    public void removeGridEdge(GridNode node1, GridNode node2) {
        Iterator<SEComponent> iterator1 = node1.neighbors.iterator();
        Iterator<Double> iterator2 = node1.neighborR.iterator();
        deleteInfoFromNeighbor:
        while (iterator1.hasNext()) {
            iterator2.next();
            if (iterator1.next() == node2) {
                iterator2.remove();
                iterator1.remove();
                break deleteInfoFromNeighbor;
            }
        }

        iterator1 = node2.neighbors.iterator();
        iterator2 = node2.neighborR.iterator();
        deleteInfoFromNeighbor:
        while (iterator1.hasNext()) {
            iterator2.next();
            if (iterator1.next() == node1) {
                iterator2.remove();
                iterator1.remove();
                break deleteInfoFromNeighbor;
            }
        }
    }

    ///////////////////////////////////
    /// Grid - Cable interconnection
    ///////////////////////////////////
    public void interconnection(Cable cable, GridNode gridNode) {
        gridNode.interConnection = cable;
        cable.connectedGridNode = gridNode;
    }

    public void breakInterconnection(Cable cable) {
        if (cable.connectedGridNode != null) {
            cable.connectedGridNode.interConnection = null;
            cable.connectedGridNode = null;
        }
    }

    public void breakInterconnection(GridNode gridNode) {
        if (gridNode.interConnection != null) {
            gridNode.interConnection.connectedGridNode = null;
            gridNode.interConnection = null;
        }
    }

    ///////////////////////////////////
    /// Grid Transformer
    ///////////////////////////////////
    public void makeTransformer(GridNode primary, GridNode secondary, double ratio, double resistance) {
        primary.type = GridNode.ISEGridNode_TransformerPrimary;
        primary.complement = secondary;
        primary.ratio = ratio;
        primary.resistance = resistance;

        secondary.type = GridNode.ISEGridNode_TransformerSecondary;
        secondary.complement = primary;
        secondary.ratio = ratio;
        secondary.resistance = resistance;
    }

    public void breakTransformer(GridNode node) {
        if (node.complement != null) {
            node.complement.type = GridNode.ISEGridNode_Wire;
            node.complement.complement = null;
            node.complement.ratio = 0;
            node.complement.resistance = 0;
        }

        node.type = GridNode.ISEGridNode_Wire;
        node.complement = null;
        node.ratio = 0;
        node.resistance = 0;
    }

    public void clearVoltageCache() {
        for (SEComponent node : this.terminalNodes)
            node.voltageCache = 0;
        for (SEComponent wire : this.wires)
            wire.voltageCache = 0;
    }

    public void optimizGraph() {
        LinkedList<SEComponent> path = new LinkedList<SEComponent>();

        this.terminalNodes.clear();
        this.terminalNodes.addAll(this.components);

        for (SEComponent node : this.terminalNodes) {
            node.visited = false;
            node.optimizedNeighbors.clear();
            node.optimizedResistance.clear();
        }

        for (SEComponent wire : this.wires) {
            wire.visited = false;
            wire.optimizedNeighbors.clear();
            wire.optimizedResistance.clear();


            wire.eliminated = true;

            if (SEGraph.shouldCalcVoltage(wire))
                this.terminalNodes.add(wire);
        }

        //SubComponents, Cable/TransmissionLine with more than 2 connections, Interconnection terminals
        for (SEComponent node : this.terminalNodes) {
            node.eliminated = false;

            if (!node.visited) {
                node.visited = true;

                traverseNeighbors:
                for (SEComponent neighbor : node.neighbors) {
                    if (neighbor.visited)
                        continue traverseNeighbors;

                    //Start a new path, from "node" towards "neighbor"
                    path.clear();
                    SEComponent prev = node;
                    SEComponent reach = neighbor;    //Far reach
                    double resistance = 0.0D;        //The total resistance from "node" to "reach"

                    search:
                    do {
                        if (reach == node) {
                            reach = null;    //Can not form an edge
                            break search;    //Avoid circulation
                        }

                        resistance += SEGraph.calcR(prev, reach);

                        if (SEGraph.shouldCalcVoltage(reach))    //We have to calculate the voltage of the interconnection point
                            break search;

                        else if (reach.neighbors.size() == 1) {
                            if (SEGraph.isWire(reach)) {
                                reach.optimizedNeighbors.add(node);
                                reach.optimizedResistance.add(resistance);

                                reach = null;    //A single ended wire can not form an edge
                            }
                            break search;
                        } else if (reach.neighbors.size() == 2) {    //Always moveforward!
                            reach.optimizedNeighbors.add(node);
                            reach.optimizedResistance.add(resistance);
                            path.add(reach);

                            reach.visited = true;
                            if (reach.neighbors.getFirst() == prev) {
                                prev = reach;
                                reach = reach.neighbors.getLast();
                            } else if (reach.neighbors.getLast() == prev) {
                                prev = reach;
                                reach = reach.neighbors.getFirst();
                            } else {
                                throw new RuntimeException("WTF mate whats going on?!");
                            }
                        }
                    } while (true);

                    //node->reach
                    if (reach != null) {    //node-node connection
                        //Reached another device node (i.e. transformer primary)
                        //resistance = total resistance from "node" to the end
                        Iterator<SEComponent> iterator = path.iterator();
                        while (iterator.hasNext()) {
                            SEComponent nodeOnPath = iterator.next();
                            nodeOnPath.optimizedNeighbors.add(reach);
                            nodeOnPath.optimizedResistance.add(
                                    resistance - nodeOnPath.optimizedResistance.getFirst()
                            );
                        }


                        Iterator<SEComponent> iteratorON = node.optimizedNeighbors.iterator();
                        Iterator<Double> iteratorR = node.optimizedResistance.iterator();

        				/*
        				 * Combine parallel path
        				 * Example:
        				 * 		x
        				 * 		x
        				 * 		xxxx
        				 * 		x  x
        				 * 		x  x
        				 * 		xxxxxxxxxx
        				 */
                        checkParallel:
                        while (iteratorON.hasNext()) {
                            double prevR = iteratorR.next();
                            if (iteratorON.next() == reach) {
                                //Delete previous edge
                                Iterator<SEComponent> iteratorON2 = reach.optimizedNeighbors.iterator();
                                Iterator<Double> iteratorR2 = reach.optimizedResistance.iterator();
                                delete:
                                while (iteratorON2.hasNext()) {
                                    iteratorR2.next();
                                    if (iteratorON2.next() == node) {
                                        iteratorR2.remove();
                                        iteratorON2.remove();
                                        break delete;
                                    }
                                }

                                iteratorR.remove();
                                iteratorON.remove();

                                resistance = resistance * prevR / (resistance + prevR);
                                break checkParallel;
                            }
                        }


                        node.optimizedNeighbors.addLast(reach);
                        node.optimizedResistance.addLast(resistance);

                        reach.optimizedNeighbors.addLast(node);
                        reach.optimizedResistance.addLast(resistance);
                    }
                }
            }

        }
    }

    public LinkedList<SEComponent> getTerminalNodes() {
        return this.terminalNodes;
    }

}
