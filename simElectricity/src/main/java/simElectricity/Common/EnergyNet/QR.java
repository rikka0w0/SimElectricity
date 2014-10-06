package simElectricity.Common.EnergyNet;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common;
import edu.emory.mathcs.csparsej.tdouble.Dcs_qrsol;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;

import java.util.LinkedList;

public class QR implements MatrixResolver{
	public static final double EPSILON = (double) 1e-10;

	//Left hand side generation
	int size;					//Size of the square matrix
	int[] Ap;					//Column offset (length = size+1)
	LinkedList<Integer> AiList;	//Index of entries
	LinkedList<Double> AxList;	//Value of entries
	int currentColumn;
	int currentRow;
	int nZ;						//Total number of non zero elements
	//----------------------------------------------------------------
	Dcs_common.Dcs matrix;		//The matrix object

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
	public void finalizeLHS(){
		matrix = Dcs_util.cs_spalloc(size, size, nZ, true, false);
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

	    size = -1;
	    Ap = null;
	    AiList = null;
	    AxList = null;
	    currentColumn = -1;
	    currentRow = -1;
	    nZ = -1;
	}

	@Override
	public boolean solve(double[] b) {
	    return Dcs_qrsol.cs_qrsol(1, matrix, b); //Result will be in b
	}

	@Override
	public void selectColumn(int column){
	    currentColumn = column;
	    currentRow = 0;
	    nZ = 0;
	}

	@Override
	public void setCell(double value) {
		if (Math.abs(value) < EPSILON){
			currentRow++;
		}else{
			matrix.x[matrix.p[currentColumn]+nZ] = value;
			currentRow++;
			nZ++;
		}
	}

	@Override
	public int getTotalNonZeros(){
		if (matrix == null)
			return 0;
		return matrix.nzmax;
	}
}
