package simElectricity;

import simElectricity.Common.EnergyNet.IMatrixResolver;
import simElectricity.Common.EnergyNet.QR;

public class MatrixTest {
	public MatrixTest(){
		QR matrix = new QR();
		matrix.newMatrix(3);
		//1	4 7
		//2	0 0
		//3	0 9
		matrix.setElementValue(0,0,1.0D);
		matrix.setElementValue(0,2,3.0D);
		matrix.setElementValue(0,1,2.0D);
		
		matrix.setElementValue(1,0,4.0D);
		matrix.setElementValue(2,0,7.0D);
		matrix.setElementValue(2,2,9.0D);
		matrix.finishEditing();
		matrix.print();
		
		
		
		//1	4 7
		//2	0 0
		//0	6 8
		matrix.setElementValue(0,2,0.0D);
		matrix.setElementValue(2,1,0.0D);
		matrix.setElementValue(1,2,6.0D);
		matrix.setElementValue(2,2,8.0D);
		matrix.finishEditing();
		matrix.print();
		
		System.out.print(true);
	}
}
