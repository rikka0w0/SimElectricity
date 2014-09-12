package simElectricity.Common.EnergyNet;

import java.util.LinkedList;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common;
import edu.emory.mathcs.csparsej.tdouble.Dcs_lusol;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;

public class SparseMatrix implements MatrixResolver{
	public static boolean demo(){
	    int size = 4; //Size of the square matrix
	    int nz = 10;  //Number of non zero entries
	    int[] Ap = {0, 3, 6, 8, 10};
	    int[] Ai = {1, 3, 0, 1, 3, 2, 2, 0, 3, 1};
	    double[] Ax= {3.1, 3.5, 4.5, 2.9, 0.4, 1.7, 3.0, 3.2, 1.0, 0.9};
	    double[] b = {2,2,2,2}; //Right hand side
	    
	    Dcs_common.Dcs matrix = Dcs_util.cs_spalloc(size, size, nz, true, false);
	    matrix.p = Ap;
	    matrix.i = Ai;
	    matrix.x = Ax;
		
	    boolean result =  Dcs_lusol.cs_lusol(1, matrix, b, 1.0); //Result will be in b
	    return result;
	}
	
	public static boolean demo2(){
		MatrixResolver matrix = new SparseMatrix();
		matrix.newMatrix(4);
		
		matrix.pushCoefficient(4.5);
		matrix.pushCoefficient(3.1);
		matrix.pushCoefficient(0);
		matrix.pushCoefficient(3.5);
		matrix.pushColumn();

		matrix.pushCoefficient(0);
		matrix.pushCoefficient(2.9);
		matrix.pushCoefficient(1.7);
		matrix.pushCoefficient(0.4);
		matrix.pushColumn();

		matrix.pushCoefficient(3.2);
		matrix.pushCoefficient(0);
		matrix.pushCoefficient(3.0);
		matrix.pushCoefficient(0);
		matrix.pushColumn();
		
		matrix.pushCoefficient(0);
		matrix.pushCoefficient(0.9);
		matrix.pushCoefficient(0);
		matrix.pushCoefficient(1.0);
		matrix.pushColumn();
		
	    //int size = 4; //Size of the square matrix
	    //int nz = 10;  //Number of non zero entries
	    //int[] Ap = {0, 3, 6, 8, 10};
	    //int[] Ai = {1, 3, 0, 1, 3, 2, 2, 0, 3, 1};
	    //double[] Ax= {3.1, 3.5, 4.5, 2.9, 0.4, 1.7, 3.0, 3.2, 1.0, 0.9};
	    double[] b = {2,2,2,2}; //Right hand side
	    
	    //solution = {0.00211652654477031,0.0787818213886764,0.622023634546417,1.96107942853783}
	    
	    matrix.solve(b);
	    return true;
	}

	int size;
	int[] Ap;
	LinkedList<Integer> AiList;
	LinkedList<Double> AxList;
	int currentColumn;
	int currentRow;
	int nZ;
	
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
		if (value == 0){
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
		
	    boolean result =  Dcs_lusol.cs_lusol(1, matrix, b, 1.0); //Result will be in b
	    
	    System.out.println(result);
	}
}
