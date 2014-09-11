package simElectricity.Common.EnergyNet;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common;
import edu.emory.mathcs.csparsej.tdouble.Dcs_lusol;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;

public class SparseMatrix {
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
		
	    return Dcs_lusol.cs_lusol(1, matrix, b, 1.0); //Result will be in b
	}
}
