import java.math.BigDecimal;

public class ComplexBigDecimal{
		
	public BigDecimal rp;
	public BigDecimal ip;
		
	public ComplexBigDecimal(BigDecimal rp, BigDecimal ip){
		this.rp = rp;
		this.ip = ip;
	}
		
	public ComplexBigDecimal add(ComplexBigDecimal a){
		BigDecimal rp = a.rp.add(this.rp);
		BigDecimal ip = a.ip.add(this.ip);
			
		return new ComplexBigDecimal(rp, ip);
	}
		
	public ComplexBigDecimal square(){
		return new ComplexBigDecimal((this.rp.multiply(this.rp)).subtract((this.ip.multiply(this.ip))).setScale(ImageHandler.zoomBD.precision()+ImageHandler.START_SCALE,BigDecimal.ROUND_HALF_UP), 
				this.rp.multiply(this.ip).multiply(new BigDecimal(2)).setScale(ImageHandler.zoomBD.precision()+ImageHandler.START_SCALE,BigDecimal.ROUND_HALF_UP));
	}
	
	public BigDecimal absP2(){
		
		return this.rp.multiply(this.rp).add((this.ip.multiply(this.ip))).setScale(ImageHandler.zoomBD.precision()+ImageHandler.START_SCALE,BigDecimal.ROUND_HALF_UP);

	}
	
	public String toString(){
		
		return (rp + " + " + ip + "i");
	}
}
