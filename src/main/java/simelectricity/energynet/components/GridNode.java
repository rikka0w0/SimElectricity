package simelectricity.energynet.components;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import simelectricity.api.node.ISEGridNode;
import simelectricity.energynet.SEGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nonnull;

public class GridNode extends SEComponent implements ISEGridNode {
    private final BlockPos pos;
    private final byte numOfParallelConductor;

    //0 - transmission line 1 - transformer primary 2 - transformer secondary
    public byte type;
    //Transformer secondary/primary
    public GridNode complement;
    public double ratio, resistance;
    //Only stores resistances between GridNodes!
    public LinkedList<Double> neighborR = new LinkedList<>();
    //Simulation & Optimization
    public Cable interConnection;
    
    //Only used for loading
    private int[] neighborX;
    private int[] neighborY;
    private int[] neighborZ;
    private double[] resistancesBuf;
    private int complementX, complementY, complementZ;

    public GridNode(BlockPos pos, byte numOfParallelConductor) {
        this.pos = pos;
        type = ISEGridNode.ISEGridNode_Wire;
        this.numOfParallelConductor = numOfParallelConductor;
    }

    ///////////////////////
    /// Read from NBT
    ///////////////////////

    public GridNode(NBTTagCompound nbt) {
        pos = new BlockPos(
                nbt.getInteger("x"),
                nbt.getInteger("y"),
                nbt.getInteger("z")
        );
        type = nbt.getByte("type");
        numOfParallelConductor = nbt.getByte("numOfParallelConductor");

        neighborX = nbt.getIntArray("neigborX");
        neighborY = nbt.getIntArray("neigborY");
        neighborZ = nbt.getIntArray("neigborZ");

        complementY = nbt.getInteger("complementY");
        if (this.complementY > 0) {
            complementX = nbt.getInteger("complementX");
            complementZ = nbt.getInteger("complementZ");
            ratio = nbt.getDouble("ratio");
            resistance = nbt.getDouble("resistance");
        }


        int numOfNeighbors = this.neighborX.length;
        resistancesBuf = new double[numOfNeighbors];
        for (int i = 0; i < numOfNeighbors; i++) {
            this.resistancesBuf[i] = nbt.getDouble("R" + String.valueOf(i));
        }
    }

    public void buildNeighborConnection(HashMap<BlockPos, GridNode> gridNodeMap, SEGraph graph) {
        for (int i = 0; i < this.neighborX.length; i++) {
            GridNode neighbor = gridNodeMap.get(new BlockPos(this.neighborX[i], this.neighborY[i], this.neighborZ[i]));

            graph.addGridEdge(this, neighbor, this.resistancesBuf[i]);
        }

        complement = gridNodeMap.get(new BlockPos(this.complementX, this.complementY, this.complementZ));
    }

    ///////////////////////
    /// Save to NBT
    ///////////////////////
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("x", this.pos.getX());
        nbt.setInteger("y", this.pos.getY());
        nbt.setInteger("z", this.pos.getZ());
        nbt.setByte("type", this.type);
        nbt.setByte("numOfParallelConductor", this.numOfParallelConductor);

        int length = 0;
        for (SEComponent neighbor : this.neighbors) {
            if (neighbor instanceof GridNode)
                length++;
        }

        this.neighborX = new int[length];
        this.neighborY = new int[length];
        this.neighborZ = new int[length];
        int i = 0;
        Iterator<Double> iterator = this.neighborR.iterator();
        for (SEComponent neighbor : this.neighbors) {
            if (neighbor instanceof GridNode) {
                GridNode gridNode = (GridNode) neighbor;
                this.neighborX[i] = gridNode.pos.getX();
                this.neighborY[i] = gridNode.pos.getY();
                this.neighborZ[i] = gridNode.pos.getZ();
                nbt.setDouble("R" + String.valueOf(i), iterator.next());
                i++;
            }
        }
        nbt.setIntArray("neigborX", this.neighborX);
        nbt.setIntArray("neigborY", this.neighborY);
        nbt.setIntArray("neigborZ", this.neighborZ);

        if (this.complement != null) {
            nbt.setInteger("complementX", this.complement.getPos().getX());
            nbt.setInteger("complementY", this.complement.getPos().getY());
            nbt.setInteger("complementZ", this.complement.getPos().getZ());
        } else {
            nbt.setInteger("complementY", -1);
        }
        nbt.setDouble("ratio", this.ratio);
        nbt.setDouble("resistance", this.resistance);
    }

    @Override
    public double getResistance(ISEGridNode neighbor) {
        Iterator<SEComponent> iterator1 = this.neighbors.iterator();
        Iterator<Double> iterator2 = this.neighborR.iterator();
        while (iterator1.hasNext()) {
            SEComponent cur = iterator1.next();
            if (cur instanceof GridNode) {
                double res = iterator2.next();
                if (cur == neighbor)
                    return res;
            }
        }
        return Double.NaN;
    }

    ///////////////////////////////
    ///ISEGridNode
    ///////////////////////////////
    @Override
    @Nonnull
    public ISEGridNode[] getNeighborList() {
        ISEGridNode[] ret = new ISEGridNode[neighbors.size()];
        int i = 0;
        for (SEComponent neighbor : neighbors) {
            ret[i] = (ISEGridNode) neighbor;
            i++;
        }
        return ret;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte numOfParallelConductor() {
        return this.numOfParallelConductor;
    }

    @Override
    public ISEGridNode getComplement() {
        if (type != 1 || type != 2)
            return null;

        return complement;
    }

    @Override
    public double getRatio() {
        if (type != 1 || type != 2)
            return Double.NaN;

        return ratio;
    }
}
