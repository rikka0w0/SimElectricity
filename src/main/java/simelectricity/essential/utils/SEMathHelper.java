package simelectricity.essential.utils;

public class SEMathHelper {	
	/**
	 * The dimension of Start and End must be the same 
	 * @return the distance between two n-dimension point, n can be any number more than 2
	 */
    public static double distanceOf(double[] Start, double[] End) {
    	double ret = 0;
    	for (int i=0; i<Start.length; i++)
    		ret += (Start[i] - End[i])*(Start[i] - End[i]);

    	return Math.sqrt(ret);
    }
    
    /**
     * Calculate the distance between two points (2d)
     *
     * @param xStart Start X coordinate
     * @param zStart Start Z coordinate
     * @param xEnd   End X coordinate
     * @param zEnd   End Z coordinate
     */
	public static double distanceOf(double xStart, double zStart, double xEnd, double zEnd){
        return Math.sqrt((xStart - xEnd) * (xStart - xEnd) +
                (zStart - zEnd) * (zStart - zEnd));
	}
	
    /**
     * Calculate the distance between two points (3d)
     *
     * @param xStart Start X coordinate
     * @param yStart Start Y coordinate
     * @param zStart Start Z coordinate
     * @param xEnd   End X coordinate
     * @param yEnd   End Y coordinate
     * @param zEnd   End Z coordinate
     */
    public static double distanceOf(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
        return Math.sqrt((xStart - xEnd) * (xStart - xEnd) +
                (yStart - yEnd) * (yStart - yEnd) +
                (zStart - zEnd) * (zStart - zEnd));
    }
}
