package simelectricity.essential.client.grid;

import net.minecraft.util.Mth;
import rikka.librikka.math.MathAssitant;

public class Catenary implements java.util.function.Function<Float,Float>, com.google.common.base.Function<Float,Float>{
	private final float u, x1, k;
	
	public Catenary(float yFrom, float yTo, float d, float h) {
		float h1, a;
		if (yFrom<yTo) {
			h1 = yFrom;
			a = yTo - yFrom;
		} else {
			h1 = yTo;
			a = yFrom - yTo;
		}
		
		if (a<1e-4)
			a = 1e-4F;	//Prevent NAN
		
		float L=calcL(a,h,d);
		float L1=-h*L/a+Mth.sqrt(h*(a+h)*(L*L-a*a))/a;
		float u=2*h/(L1*L1-h*h);
		float x1= MathAssitant.asinh(u*L1)/u;
		float k=h1-h-1/u;
		
		if(yFrom>yTo)
		    x1=x1+d- MathAssitant.acosh(u*(yTo-k))/u-x1;
		
		this.u = u;
		this.x1 = x1;
		this.k = k;
	}
	
	@Override
	public Float apply(Float x) {
		return (float) (Math.cosh(u*(x-x1))/u+k);
	}
	
	public float derivative(float x) {
		return (float) (Math.sinh(u*(x-x1)));
	}
	
	public static float calcL(float a, float h, float d) {
		float lower = Mth.sqrt(d*d+a*a);
		float upper = 2*h+d+a;
		
		float TV=(upper+lower)/2;
		int n = 0;
		while ((upper-lower)>1e-4 && ++n<100) {			
			if (calcD(a,TV,h)>d)
				upper = TV;
			else
				lower = TV;
			
			TV=(upper+lower)/2;
		}
		
		return TV;
	}
	
	public static float calcD(float a, float L, float h) {
		float q = 2*Mth.sqrt(h * (a+h) * (L*L - a*a));
		return ((L*L-a*a)*(a+2*h)-L*q)
				/
				(a*a)* MathAssitant.atanh(a*a/(L*(a+2*h)-q));
	}
}
