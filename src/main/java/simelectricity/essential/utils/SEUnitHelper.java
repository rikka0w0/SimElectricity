package simelectricity.essential.utils;

public class SEUnitHelper {
	public static String getStringWithoutUnit(double number){
		double tmp = Math.abs(number);
		if (tmp>=1000000)
			return String.format("%.3f", number/1000000) + "M";
		if (tmp>=1000)
			return String.format("%.3f", number/1000) + "K";
		if (tmp>=1)
			return String.format("%.3f", number);
		if (tmp>=0.001)
			return String.format("%.3f", number*1000) + "m";
		if (tmp>=0.000001)
			return String.format("%.3f", number*1000000) + "u";		
		
		return "0";
	}
	
	public static String getVoltageStringWithUnit(double voltage){	
		return getStringWithoutUnit(voltage) + "V";
	}
	
	public static String getCurrentStringWithUnit(double current){	
		return getStringWithoutUnit(current) + "A";
	}
	
	public static String getPowerStringWithUnit(double power){	
		return getStringWithoutUnit(power) + "W";
	}
	
	public static String getEnergyStringInJ(double energy){
		return getStringWithoutUnit(energy) + "J";
	}
	
	/**
	 * @param energy in Joules
	 * @return in KWH
	 */
	public static String getEnergyStringInKWh(double energy){
		energy = energy / 3600;	//Convert J into KWh
		return getStringWithoutUnit(energy) + "Wh";
	}
}
