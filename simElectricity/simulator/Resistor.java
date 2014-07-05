package simElectricity.simulator;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

import simElectricity.API.IConductor;

public class Resistor extends DefaultWeightedEdge {
	public List<IConductor> includeFakeNodes = null;
}
