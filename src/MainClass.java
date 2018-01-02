import java.math.BigDecimal;

public class MainClass {
	
	public static void main(String[] args){
		
		int imageWidth = 1280;
		int imageHeight = 720;
		
		double xmax = 1;
		double xmin = -1;
		
		double ymax = ((double)imageHeight/(double)imageWidth)*(xmax-xmin)/2;
		double ymin = -((double)imageHeight/(double)imageWidth)*(xmax-xmin)/2;
		
		double zoom = 0.5;
		ComplexDouble c = new ComplexDouble(0, 0);
		
		ImageHandler imgHandler = new ImageHandler(xmax, xmin, ymax, ymin, imageWidth, imageHeight, zoom, c);
		new UIHandler(imgHandler);
		
		
	}
	

	
}
