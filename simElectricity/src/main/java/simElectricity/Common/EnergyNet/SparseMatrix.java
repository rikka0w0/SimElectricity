package simElectricity.Common.EnergyNet;

import java.util.LinkedList;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common;
import edu.emory.mathcs.csparsej.tdouble.Dcs_lusol;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;

public class SparseMatrix implements MatrixResolver{
	public static final double EPSILON = (double) 1e-10;
	
	int size;					//Size of the square matrix
	int[] Ap;					//Column offset (length = size+1)
	LinkedList<Integer> AiList;	//Index of entries
	LinkedList<Double> AxList;	//Value of entries
	int currentColumn;
	int currentRow;
	int nZ;						//Total number of non zero elements
	
	@Override
	public void newMatrix(int size) {
		this.size = size;
		this.Ap = new int[size+1];
		this.Ap[0] = 0;
		this.AiList = new LinkedList<Integer>();
		this.AxList = new LinkedList<Double>();
		this.currentColumn = 0;
		this.currentRow = 0;
		this.nZ = 0;
	}

	@Override
	public void pushCoefficient(double value) {
		if (Math.abs(value) < EPSILON){
			currentRow++;
		}else{
			AiList.add(currentRow);
			AxList.add(value);
			currentRow++;
			nZ++;
		}
	}

	@Override
	public void pushColumn() {
		currentColumn ++;
		currentRow = 0;
		Ap[currentColumn] = nZ;
	}

	@Override
	public void solve(double[] b) {
	    Dcs_common.Dcs matrix = Dcs_util.cs_spalloc(size, size, nZ, true, false);
	    matrix.p = Ap;

	    //Shift the list into array
	    Integer i = AiList.poll();
	    int j = 0;
	    while (i!=null){
	    	matrix.i[j] = i;
	    	i = AiList.poll();
	    	j++;
	    }
	    
	    Double k = AxList.poll();
	    j = 0;
	    while (k!=null){
	    	matrix.x[j] = k;
	    	k = AxList.poll();	    	
	    	j++;
	    }
		
	    Dcs_lusol.cs_lusol(1, matrix, b, 1.0); //Result will be in b
	}
}
