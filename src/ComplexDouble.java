
public class ComplexDouble{
		
	public double rp;
	public double ip;
		
	public ComplexDouble(double rp, double ip){
		this.rp = rp;
		this.ip = ip;
	}
		
	public ComplexDouble add(ComplexDouble a){
		double rp = a.rp + this.rp;
		double ip = a.ip + this.ip;
			
		return new ComplexDouble(rp, ip);
	}
		
	public ComplexDouble square(){
		return new ComplexDouble(this.rp*this.rp-this.ip*this.ip, 2.0*this.rp*this.ip);
	}
	
	public double abs(){
		
		return Math.sqrt(this.rp*this.rp+this.ip*this.ip);
	}
	
	public String toString(){
		
		return (rp + " + " + ip + "i");
	}
}
