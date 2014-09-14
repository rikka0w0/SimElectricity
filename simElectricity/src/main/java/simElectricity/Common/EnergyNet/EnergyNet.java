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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.*;
import simElectricity.API.EnergyTile.ITransformer.ITransformerWinding;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.Util;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;

import java.util.*;

public final class EnergyNet {
    //Represents the relationship between components
    private BakaGraph tileEntityGraph = new BakaGraph();
    //A map for storing voltage value of nodes, be private to avoid cheating 0w0
    private Map<IBaseComponent, Double> voltageCache = new HashMap<IBaseComponent, Double>();
    //A flag for energyNet updating
    private boolean calc = false;
    private boolean structureChanged = false;
    private List<IBaseComponent> changedComponents = new LinkedList<IBaseComponent>();
    
    //For partial update
    private MatrixResolver matrix = MatrixResolver.MatrixHelper.newResolver(ConfigManager.matrixSolver);
    private Map<IBaseComponent, Integer> componentIndex = new HashMap<IBaseComponent, Integer>();
    List<IBaseComponent> unknownVoltageNodes = new ArrayList<IBaseComponent>();
    
    public String[] info(){
    	String[] temp = matrix.toString().split("[.]");
    	
    	if (tileEntityGraph.size() == 0){
    		return new String[]{
    				"EnergyNet is empty and idle",
    				"Matrix solving algorithsm: " + temp[temp.length-1].split("@")[0]
    		};
    	}
    	
    	return new String[]{		
    	"Loaded entities: " + String.valueOf(tileEntityGraph.size()),
    	"Non-zero elements: " + String.valueOf(matrix.getTotalNonZeros()),
    	"Sparse rate: " + String.valueOf(matrix.getTotalNonZeros() * 100 / (tileEntityGraph.size() * tileEntityGraph.size())) + "%",
    	"Matrix solving algorithsm: " + temp[temp.length-1].split("@")[0]
    	};
    }
    
    public void reFresh(){
        structureChanged = true;
        calc = true;
    	onTick();
    }
    
    //Simulator------------------------------------------------------------------------
    private double getResistance(IBaseComponent node, IBaseComponent neighbor) {
        if (node instanceof IConductor) {            //IConductor
            return node.getResistance();
        } else if (node instanceof IManualJunction) { //IManualJunction
            if (node.getResistance() == 0) {
                return ((IManualJunction) node).getResistance(neighbor);
            } else {
                return node.getResistance();
            }
        } else {
            return 0;
        }
    }

    private double getCoefficient(int rowIndex,List<IBaseComponent> neighborList, int columnIndex, IBaseComponent currentColumnComponent){
    	double cellData = 0;

        if (currentColumnComponent instanceof IConductor || currentColumnComponent instanceof IManualJunction) {
	    	if (columnIndex == rowIndex) { //Key cell
	    		//Add neighbor resistance
	    		for (IBaseComponent neighbor : neighborList) {
	                if (neighbor instanceof IConductor || neighbor instanceof IManualJunction) {
	                	cellData += 1.0D / (getResistance(currentColumnComponent, neighbor) + getResistance(neighbor, currentColumnComponent));  // IConductor next to IConductor
	                } else {
	                        cellData += 1.0D / getResistance(currentColumnComponent, neighbor);                              // IConductor next to other components
	                }
	            }
	        } else {
	        	IBaseComponent currentrowComponent = unknownVoltageNodes.get(rowIndex);
	            if (neighborList.contains(currentrowComponent)) {
	                if (currentrowComponent instanceof IConductor || currentrowComponent instanceof IManualJunction) {
	                    cellData = -1.0D / (getResistance(currentColumnComponent, currentrowComponent) + getResistance(currentrowComponent, currentColumnComponent));
	                } else {
	                    cellData = -1.0D / getResistance(currentColumnComponent, currentrowComponent);
	                }
	            }
	        }
	    }else{
	    	//For other nodes (can only have a IConductor neighbor or no neighbor!)
            IConductor neighbor = (IConductor) (neighborList.isEmpty() ? null : neighborList.get(0));
            	
            if (columnIndex == rowIndex) { //Key cell
                if (neighbor != null) {
                    cellData += 1.0D / neighbor.getResistance();
                }

                //Add internal resistance for fixed voltage sources
                if (currentColumnComponent instanceof ICircuitComponent) {
                    cellData += 1.0 / currentColumnComponent.getResistance();
                } else if (currentColumnComponent instanceof ITransformerWinding) {
                    ITransformerWinding winding = (ITransformerWinding) currentColumnComponent;
                    if (winding.isPrimary()) {
                         cellData += winding.getRatio() * winding.getRatio() / winding.getResistance();
                    } else {
                         cellData += 1.0 / winding.getResistance();
                    }
                }
            } else {
                if (neighbor == unknownVoltageNodes.get(rowIndex)) {
                	cellData = -1.0D / neighbor.getResistance();
                } else if (currentColumnComponent instanceof ITransformerWinding) { //Add transformer association
                    IBaseComponent currentrowComponent = unknownVoltageNodes.get(rowIndex);
                    ITransformerWinding winding = (ITransformerWinding) currentColumnComponent;
                    ITransformer core = winding.getCore();

                     if ((winding.isPrimary() && core.getSecondary() == currentrowComponent) ||
                        ((!winding.isPrimary()) && core.getPrimary() == currentrowComponent))
                    	 cellData = -winding.getRatio() / winding.getResistance();
                }
            } 
	    }
        return cellData;	
    }
    
    private double getB(IBaseComponent currentColumnComponent){
        //Add fixed voltage sources
        if (currentColumnComponent instanceof ICircuitComponent) {
            //Possible voltage source, getOutputVoltage()=0 for sinks, getOutputVoltage>0 for sources
        	return ((ICircuitComponent) currentColumnComponent).getOutputVoltage()
                    / currentColumnComponent.getResistance();
        } else {
            //Normal conductor nodes
            return 0;
        }    	
    }
    
    private void attemptSolving(double[] b){
        if (!matrix.solve(b)){
        	throw new RuntimeException("Due to incorrect value of components, the energy net has been shutdown!");
        }
        System.out.println("Run!");
    }
    
    private void runSimulator() {
        unknownVoltageNodes.clear();
        unknownVoltageNodes.addAll(tileEntityGraph.vertexSet());

        int matrixSize = unknownVoltageNodes.size();
        matrix.newMatrix(matrixSize);
        double[] b = new double[matrixSize];

        componentIndex.clear();
        for (int columnIndex = 0; columnIndex < matrixSize; columnIndex++) {       	
            IBaseComponent currentColumnComponent = unknownVoltageNodes.get(columnIndex);
        	componentIndex.put(currentColumnComponent, columnIndex);
            
            //Get the constant side of matrix
            b[columnIndex] = getB(currentColumnComponent);

        	List<IBaseComponent> neighborList = tileEntityGraph.neighborListOf(currentColumnComponent);
        	for (int rowIndex = 0; rowIndex < matrixSize; rowIndex++) {
        		matrix.pushCoefficient(getCoefficient(rowIndex, neighborList, columnIndex,currentColumnComponent));
        	}
            matrix.pushColumn();
        }
        
        matrix.finalizeLHS();
        attemptSolving(b);

        voltageCache.clear();
        for (int i = 0; i < matrixSize; i++) {
            voltageCache.put(unknownVoltageNodes.get(i), b[i]);
        }
    }

    private void partialUpdate(){
    	if (componentIndex.size() == 0){
    		runSimulator();
    		return;
    	}
    		
    	List<IBaseComponent> updateList = new LinkedList<IBaseComponent>();
    	for (IBaseComponent c: changedComponents){
    		if (!updateList.contains(c)) updateList.add(c);
    		for (IBaseComponent neighbor: tileEntityGraph.neighborListOf(c)){
    			if (!updateList.contains(neighbor)) updateList.add(neighbor);
    		}
    	}
    	
    	int matrixSize = unknownVoltageNodes.size();
    	double[] b = new double[matrixSize];
    	boolean bCalcutated = false;
    	for (IBaseComponent columnComponent: updateList){
    		int columnIndex = componentIndex.get(columnComponent);
    		matrix.selectColumn(columnIndex);
    		
    		List<IBaseComponent> neighborList = tileEntityGraph.neighborListOf(columnComponent);    		
    		for (int rowIndex = 0; rowIndex<matrixSize; rowIndex++){
    			IBaseComponent rowComponent = unknownVoltageNodes.get(rowIndex);
    			
    			//Calculate the right hand side of the matrix in the first traverse
    			if(!bCalcutated) b[rowIndex] = getB(rowComponent);
    			
    			matrix.setCell(getCoefficient(rowIndex, neighborList, columnIndex, columnComponent));
    		}
    		bCalcutated = true;
    		
    		columnIndex++;
    	}
    	
    	attemptSolving(b);

        voltageCache.clear();
        for (int i = 0; i < matrixSize; i++) {
            voltageCache.put(unknownVoltageNodes.get(i), b[i]);
        }
    }
    
    /*End of Simulator*/


    /**
     * Called in each tick to attempt to do calculation
     */
    public void onTick() {
        //energyNet.calc = true;
        if (calc) {

            
            if(structureChanged){
            	runSimulator();
            }else{
            	partialUpdate();
            }
            
            structureChanged = false;
            calc = false;
        	changedComponents.clear();
            
            //Check power distribution
            try {
                for (IBaseComponent tile : tileEntityGraph.vertexSet()) {
                    //Call onEnergyNetUpdate()
                    if (tile instanceof ITransformerWinding) {
                        ITransformerWinding winding = (ITransformerWinding) tile;
                        if (winding.getCore() instanceof IEnergyNetUpdateHandler && winding.isPrimary())
                            ((IEnergyNetUpdateHandler) winding.getCore()).onEnergyNetUpdate();
                    }
                    if (tile instanceof IEnergyNetUpdateHandler) {
                        ((IEnergyNetUpdateHandler) tile).onEnergyNetUpdate();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    //Editing of the jGraph--------------------------------------------------------------------------------

    /**
     * Internal use only, return a list containing neighbor TileEntities (Just for IBaseComponent)
     */
    private List<IBaseComponent> neighborListOf(TileEntity te) {
        List<IBaseComponent> result = new ArrayList<IBaseComponent>();
        TileEntity temp;

        if (te instanceof IConductor) {
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                temp = Util.getTileEntityonDirection(te, direction);
                if (temp instanceof IConductor) {  //Conductor
                    IConductor wire = (IConductor) te;
                    IConductor neighbor = (IConductor) temp;

                    if (wire.getColor() == 0 ||
                            neighbor.getColor() == 0 ||
                            wire.getColor() == neighbor.getColor()) {
                        result.add(neighbor);
                    }

                } else if (temp instanceof IEnergyTile) {   //IEnergyTile
                    if (((IEnergyTile) temp).getFunctionalSide() == direction.getOpposite())
                        result.add((IEnergyTile) temp);
                } else if (temp instanceof IComplexTile) {  //IComplexTile
                    if (((IComplexTile) temp).getCircuitComponent(direction.getOpposite()) != null)
                        result.add(((IComplexTile) temp).getCircuitComponent(direction.getOpposite()));
                } else if (temp instanceof ITransformer) {
                    if (((ITransformer) temp).getPrimarySide() == direction.getOpposite())
                        result.add(((ITransformer) temp).getPrimary());

                    if (((ITransformer) temp).getSecondarySide() == direction.getOpposite())
                        result.add(((ITransformer) temp).getSecondary());
                } else if (temp instanceof IManualJunction) {
                    List<IBaseComponent> neighbors = new ArrayList<IBaseComponent>();
                    ((IManualJunction) temp).addNeighbors(neighbors);
                    if (neighbors.contains(te))
                        result.add((IManualJunction) temp);
                }
            }
        }


        if (te instanceof IEnergyTile) {
            temp = Util.getTileEntityonDirection(te, ((IEnergyTile) te).getFunctionalSide());

            if (temp instanceof IConductor) {
                result.add((IBaseComponent) temp);
            }
        }

        if (te instanceof IManualJunction) {
            ((IManualJunction) te).addNeighbors(result);
        }

        return result;
    }

    /**
     * Add a TileEntity to the energynet
     */
    public void addTileEntity(TileEntity te) {
        Map<IBaseComponent, IBaseComponent> neighborMap = new HashMap<IBaseComponent, IBaseComponent>();

        if (te instanceof IComplexTile) { // IComplexTile
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                ICircuitComponent subComponent = ((IComplexTile) te).getCircuitComponent(direction);
                if (subComponent instanceof IBaseComponent) {
                    tileEntityGraph.addVertex(subComponent);
                    TileEntity neighbor = Util.getTileEntityonDirection(te, direction);
                    if (neighbor instanceof IConductor)  // Connected properly
                        neighborMap.put((IBaseComponent) neighbor, subComponent);
                }
            }

        } else if (te instanceof ITransformer) { // Transformer
            ITransformer transformer = ((ITransformer) te);
            ITransformerWinding primary = ((ITransformer) te).getPrimary();
            ITransformerWinding secondary = ((ITransformer) te).getSecondary();

            tileEntityGraph.addVertex(primary);
            tileEntityGraph.addVertex(secondary);

            TileEntity neighbor;

            neighbor = Util.getTileEntityonDirection(te, transformer.getPrimarySide());
            if (neighbor instanceof IConductor)
                neighborMap.put((IBaseComponent) neighbor, primary);

            neighbor = Util.getTileEntityonDirection(te, transformer.getSecondarySide());
            if (neighbor instanceof IConductor)
                neighborMap.put((IBaseComponent) neighbor, secondary);

        } else { // IBaseComponent and IConductor
            tileEntityGraph.addVertex((IBaseComponent) te);
            List<IBaseComponent> neighborList = neighborListOf(te);
            for (IBaseComponent neighbor : neighborList)
                neighborMap.put(neighbor, (IBaseComponent) te);
        }

        for (IBaseComponent neighbor : neighborMap.keySet()) {
            tileEntityGraph.addVertex(neighborMap.get(neighbor));
            tileEntityGraph.addEdge(neighbor, neighborMap.get(neighbor));
        }

        calc = true;
        structureChanged = true;
    }

    /**
     * Remove a TileEntity from the energy net
     */
    public void removeTileEntity(TileEntity te) {
        if (te instanceof IComplexTile) { //For a complexTile every subComponents has to be removed!            
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                ICircuitComponent subComponent = ((IComplexTile) te).getCircuitComponent(direction);
                if (subComponent != null)
                    tileEntityGraph.removeVertex(subComponent);
            }
        } else if (te instanceof ITransformer) {
            tileEntityGraph.removeVertex(((ITransformer) te).getPrimary());
            tileEntityGraph.removeVertex(((ITransformer) te).getSecondary());
        } else {  //IBaseComponent and IConductor
            tileEntityGraph.removeVertex((IBaseComponent) te);
        }
        calc = true;
        structureChanged = true;
    }

    /**
     * Refresh a node information for a tile which ALREADY attached to the energy network
     */
    public void rejoinTileEntity(TileEntity te) {
        removeTileEntity(te);
        addTileEntity(te);
    }

    /**
     * Mark the energy net for updating in next tick
     */
    public void markForUpdate(TileEntity te) {
        calc = true;
        if (te instanceof IComplexTile) { //For a complexTile every subComponents has to be removed!            
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                ICircuitComponent subComponent = ((IComplexTile) te).getCircuitComponent(direction);
                if (subComponent != null)
                	changedComponents.add(subComponent);
            }
        } else if (te instanceof ITransformer) {
        	changedComponents.add(((ITransformer) te).getPrimary());
        	changedComponents.add(((ITransformer) te).getSecondary());
        } else {  //IBaseComponent and IConductor
        	changedComponents.add((IBaseComponent)te);
        }
    }

    /**
     * Creation of the energy network
     */
    public EnergyNet(World world) {
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId));
    }

    public double getVoltage(IBaseComponent Tile){
         if (voltageCache.containsKey(Tile)){
        	double voltage = voltageCache.get(Tile);
        	if (!Double.isNaN(voltage))	return voltage;
         }
         return 0;   	
    }
    
    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     */
    public static double getVoltage(IBaseComponent Tile, World world) {
        return WorldData.getEnergyNetForWorld(world).getVoltage(Tile);
    }
}
