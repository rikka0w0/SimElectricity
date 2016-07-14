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

public class Gaussian implements IMatrixResolver{
    public static final double EPSILON = (double) 1e-10;

    double[][] matrix;
    int currentRow;
    int currentColumn;
    int nZ;
    
	@Override
	public void newMatrix(int size) {
		matrix = new double[size][size];
		currentRow = 0;
		currentColumn = 0;
		nZ = 0;
	}

	@Override
	public void pushCoefficient(double value) {
		if (Math.abs(value) >= EPSILON)
			nZ++;
			
		matrix[currentColumn][currentRow] = value;
		currentRow++;
	}

	@Override
	public void pushColumn() {
		currentColumn++;
		currentRow = 0;
	}

	@Override	
	public void finalizeLHS(){
	    currentColumn = -1;
	    currentRow = -1;
	}
	
	@Override
	public boolean solve(double[] b) {
		double[][] A = new double[b.length][b.length];
		for (int i=0;i<b.length;i++)
			for (int j=0;j<b.length;j++)
				A[i][j] = matrix[i][j];
		
		double[] x = lsolve(A,b);
		if (x == null)
			return false;
		for (int i=0;i<b.length;i++)
			b[i] = x[i];
		return true;
	}
    
    // Gaussian elimination with partial pivoting
    public static double[] lsolve(double[][] A, double[] b) {
        int N = b.length;
        
        for (int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[p][p]) <= EPSILON) {
            	return null;
                //throw new RuntimeException(	"Matrix is singular or nearly singular");
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                if (A[p][p] != 0) {//Ignore any line with all zero
                    double alpha = A[i][p] / A[p][p];
                    b[i] -= alpha * b[p];
                    for (int j = p; j < N; j++) {
                        A[i][j] -= alpha * A[p][j];
                    }
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            if (A[i][i] != 0) {//Ignore any line with all zero
                double sum = 0.0;
                for (int j = i + 1; j < N; j++) {
                    sum += A[i][j] * x[j];
                }
                x[i] = (b[i] - sum) / A[i][i];
            }
        }
        return x;
    }
    
    @Override
    public void selectColumn(int column){
    	currentColumn = column;
    	currentRow = 0;
    }
    
    @Override
	public void setCell(double value){
		matrix[currentColumn][currentRow] = value;
		currentRow++;
    }
    
	@Override
	public int getTotalNonZeros(){
		return nZ;
	}
}
