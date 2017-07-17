package simelectricity.energynet.matrix;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common;
import edu.emory.mathcs.csparsej.tdouble.Dcs_qrsol;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * A bridging class between EnergyNet and CSprase lib
 * */
public class QR implements IMatrixResolver{
	public static final double EPSILON = 1e-10;

	int size;					//Size of the square matrix
	int[] Ap;
	LinkedList<Integer> AiList;	//Row entries
	LinkedList<Double> AxList;	//Value of entries
	int nZ;						//Total number of non zero elements
	//----------------------------------------------------------------
	Dcs_common.Dcs matrix;		//The matrix object

	@Override
	public void newMatrix(int size) {
		this.size = size;
		Ap = new int[size+1];
		AiList = new LinkedList<Integer>();
		AxList = new LinkedList<Double>();
		nZ = 0;
	}
	
	@Override
	public void setElementValue(int column, int row, double value){		
		//if (Math.abs(value) < EPSILON){
		//	setElementToZero(column, row);
		//	return;
		//}
		
		
		ListIterator<Integer> Ai= AiList.listIterator(Ap[column]);	//Ap[column] -> rowIndexStart, rowIndex -> index in Ai
		ListIterator<Double> Ax= AxList.listIterator(Ap[column]);
						
		
		for (int i = Ap[column]; i < Ap[column+1]; i++){	//Ap[column+1] -> rowIndexEnd
			int rowEntry = Ai.next(); 			//rowEntry = Ai[i];
			Ax.next(); 	//Ax[i];
			
			//The element doesn't exist in the matrix
			if (rowEntry > row){
				Ai.previous();
				Ax.previous();
				Ai.add(row);
				Ax.add(value);
				
				for (int j = column+1;j<size+1;j++){
					Ap[j]++;
				}
				
				nZ++;
				return;
			}
			
			//The element is already existing in the matrix, replace it with a new value
			else if (rowEntry == row){
				Ax.set(value);	//Ax[i] = value;
				return;
			}
		}
		
		//rowIndexEnd < row
		Ai.add(row);
		Ax.add(value);
		
		for (int j = column+1;j<size+1;j++){
			Ap[j]++;
		}
		
		nZ++;
	}
	
	private void setElementToZero(int column, int row){		
		ListIterator<Integer> Ai= AiList.listIterator(Ap[column]);	//Ap[column] -> rowIndexStart, rowIndex -> index in Ai
		ListIterator<Double> Ax= AxList.listIterator(Ap[column]);
						
		
		for (int i = Ap[column]; i < Ap[column+1]; i++){	//Ap[column+1] -> rowIndexEnd
			int rowEntry = Ai.next(); 			//rowEntry = Ai[i];
			double elementValue = Ax.next(); 	//elementValue = Ax[i];
			
			//The element is already existing in the matrix, delete its entry
			if (rowEntry == row){
				Ai.remove();
				Ax.remove();
				
				for (int j = column+1;j<size+1;j++){
					Ap[j]--;
				}
				
				nZ--;
				return;
			}
			//The element doesn't exist
			else if (rowEntry > row){
				return;
			}
		}
	}
	
	@Override
	public void finishEditing(){
		matrix = Dcs_util.cs_spalloc(size, size, nZ, true, false);
		for (int i=0; i<Ap.length; i++)
			matrix.p[i] = Ap[i];

		Iterator<Integer> Ai= AiList.iterator();
		Iterator<Double> Ax= AxList.iterator();
		
		int i = 0;
		while (Ai.hasNext()){
			matrix.i[i] = Ai.next();
			matrix.x[i] = Ax.next();
			i++;
		}
	}
	
	@Override
	public void print(String[] header){
		double[][] matrixIneff = new double[size][size];
		
		for (int columnIndex=0; columnIndex<size; columnIndex++){
			int rowIndex = 0;
			
			//Get column pointer boundaries
			int start = matrix.p[columnIndex];
			int end = matrix.p[columnIndex+1];
			
			for (int i=start; i<end; i++){
				matrixIneff[columnIndex][matrix.i[i]] = matrix.x[i];
			}
		}
		
		String ret = String.format("%-20s", "\\");
		for (int j=0; j<size; j++)
			ret += String.format("%-20s", header[j]);
		ret += "\r\n";
		
		for (int i=0; i<size;i++){
			ret += String.format("%-20s", header[i]);
			for (int j=0; j<size; j++)
				ret += String.format("%-20.5e", matrixIneff[i][j]);
			ret += "\r\n";
		}
		System.out.print(ret+"\r\n");
	}
	
	@Override
	public boolean solve(double[] b) {
	    return Dcs_qrsol.cs_qrsol(1, matrix, b); //Result will be in b
	}

	@Override
	public int getTotalNonZeros(){
		if (matrix == null)
			return 0;
		return matrix.nzmax;
	}

	@Override
	public int getMatrixSize() {
		return size;
	}
}
