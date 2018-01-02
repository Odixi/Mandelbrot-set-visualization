import java.math.BigDecimal;

public class MandelbrotTools {

	public static int diverginNumber(ComplexDouble c, int maxIterations){
		
		ComplexDouble z = new ComplexDouble(0, 0);
		int itr = maxIterations;
		
		for (int i = 0; i < maxIterations; i++){
			z = z.square().add(c);
			if (z.abs() >= 2){
				itr = i;
				break;
			}
		}
		return itr;
	}
	
	public static int diverginNumberWithBD(ComplexBigDecimal c, int maxIterations){
		
		ComplexBigDecimal z = new ComplexBigDecimal(new BigDecimal(0), new BigDecimal(0));
		int itr = maxIterations;
		
		for (int i = 0; i < maxIterations; i++){
			z = z.square().add(c);
			if (z.absP2().compareTo(new BigDecimal(4)) > 0){
				itr = i;
				break;
			}
		}

		return itr;
	}
	
}	

