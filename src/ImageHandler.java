import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class ImageHandler {
	
	public static final int START_SCALE = 4;
	
	private double xmax;
	private double xmin;
	private double ymax;
	private double ymin;
	
	private int imageWidth;
	private int imageHeight;
	
	private double zoom;
	public static BigDecimal zoomBD;
	private ComplexDouble middle;
	private ComplexBigDecimal middleBD;
	
	private int[][] itrArray;
	
	private UIHandler uiHandler;
	
	public ImageHandler(double xmax, double xmin, double ymax, double ymin, 
			int  imageWidth, int imageHeight, double zoom, ComplexDouble c){
		
		this.xmax = xmax;
		this.xmin = xmin;
		this.ymax = ymax;
		this.ymin = ymin;
		
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		
		this.zoom = zoom;
		this.zoomBD = new BigDecimal(zoom);
		
		middle = c;
		middleBD = new ComplexBigDecimal(new BigDecimal(c.rp), new BigDecimal(c.ip));
		
	}
	
	/*
	 * 
	 * calculates iter values for pixel periodically
	 * period lenght > 0;
	 */
	public int[][] calculateIterMatrix(int periodLength, int phase){
		
		boolean bigDecimal = uiHandler.getUseBigDecimal();
		
		double stepX = 0,stepY = 0;
		BigDecimal stepXbd = null ,stepYbd = null;
		if (!bigDecimal){
			stepX = (xmax-xmin)/zoom/(double)imageWidth;
			stepY = (ymax-ymin)/zoom/(double)imageHeight;
		}else{
			stepXbd = new BigDecimal(xmax-xmin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(imageWidth),zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN);
			stepYbd = new BigDecimal(ymax-ymin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(imageHeight),zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN);
		}
		
		int[][] itrationArray = new int[imageHeight][imageWidth];
		
		String etaUnit = "s";
		long eta;
		long timeElapsedFromStart;
		long startTime = System.nanoTime();
		
		Object c;
		int maxItr;
		int colormode = uiHandler.getColorMode();
		
		for (int i = 0; i < imageHeight; i++){
			for (int j = phase; j < imageWidth; j = j + periodLength){
				
				c = (bigDecimal) ? new ComplexBigDecimal((new BigDecimal(xmin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP)).add(new BigDecimal(j).multiply(stepXbd)).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN),
						(new BigDecimal(ymin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP)).add(new BigDecimal(i).multiply(stepYbd)).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN)) 
						: new ComplexDouble(xmin/zoom+j*stepX, ymin/zoom+i*stepY);
				
				int iterAdLevel = 100;
				try{
					iterAdLevel = uiHandler.getIterLevel();
				}catch (Exception e){return itrArray;}
				
				switch (colormode) {
					case UIHandler.BLACK_WHITE:
						maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
						break;
					case UIHandler.CONSTANT_COLORING:
						maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
						break;
					case UIHandler.COLORFUL:
						maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 600) ? 600 : (int)((double)iterAdLevel*Math.log10(zoom));
						break;
					default:
						maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
						break;
				}
						
				itrationArray[i][j] = (bigDecimal) ? MandelbrotTools.diverginNumberWithBD(((ComplexBigDecimal)c).add(middleBD), maxItr)
						: MandelbrotTools.diverginNumber(((ComplexDouble)c).add(middle), maxItr);
				// ---

			}//for width
			
			// Only the "leading" thread should update and calculate eta time
			if (phase == 0){
				timeElapsedFromStart = System.nanoTime()-startTime;
				eta = timeElapsedFromStart/(long)(i+1)*(imageWidth-i-1);
				eta = TimeUnit.NANOSECONDS.toSeconds(eta); //sekunneiksi
				etaUnit = "s";
			
				if (eta > 120){
					eta = TimeUnit.SECONDS.toMinutes(eta);
					etaUnit = " min";
					if (eta > 120){
						eta = TimeUnit.MINUTES.toHours(eta);
						etaUnit = " hours";
						if (eta > 48){
							eta = TimeUnit.HOURS.toDays(eta);
							etaUnit = " days";
						}
					}
				}		
				uiHandler.setLoadingText("Loading image " + (int)(100*((float)i/(float)imageHeight)) + "%  "
					+ "ETA: " + eta+etaUnit);
			}//if 
		}//for height
	
		return itrationArray;

	}
	
	public BufferedImage intArrayToImage(int[][] intArray){
		int colorMode = uiHandler.getColorMode();
		
		switch (colorMode) {
		
		case UIHandler.CONSTANT_COLORING:
			return calculateColorValuesConst(intArray);
			
		case UIHandler.BLACK_WHITE:
			return calculateColorValueBlackWhite(intArray);
			
		case UIHandler.COLORFUL:
			return calculateColorValueColorfaul(intArray);
			
		default:
			return calculateColorValuesConst(intArray);
		}
	}
	
	@Deprecated
	public BufferedImage calculateImageRGB(){
		
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		double stepX = (xmax-xmin)/zoom/(double)imageWidth;
		double stepY = (ymax-ymin)/zoom/(double)imageHeight;
		
		itrArray = new int[imageHeight][imageWidth];
		
		String etaUnit = "s";
		long eta;
		long timeElapsedFromStart;
		long startTime = System.nanoTime();
		
		ComplexDouble c;
		int colorMode = uiHandler.getColorMode();
		
		for (int i = 0; i < imageHeight; i++){
			for (int j = 0; j < imageWidth; j++){
				
				c = new ComplexDouble(xmin/zoom+j*stepX, ymin/zoom+i*stepY);
				
				switch(colorMode){
					case(UIHandler.COLORFUL):
						img.setRGB(j, i, calculateColorValueSYC(c.add(middle)).getRGB());
						break;
					case(UIHandler.BLACK_WHITE):
						img.setRGB(j, i, calculateColorValueBW(c.add(middle)).getRGB());
						break;
					case(UIHandler.CONSTANT_COLORING):
						int iterAdLevel = 100;
						try{
							iterAdLevel = uiHandler.getIterLevel();
						}catch (Exception e){return new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);}
						
						int maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
						itrArray[i][j] = MandelbrotTools.diverginNumber(c.add(middle), maxItr);
						break;
				}
			}//for width
			timeElapsedFromStart = System.nanoTime()-startTime;
			eta = timeElapsedFromStart/(long)(i+1)*(imageWidth-i-1);
			eta = TimeUnit.NANOSECONDS.toSeconds(eta); //sekunneiksi
			etaUnit = "s";
			
			if (eta > 120){
				eta = TimeUnit.SECONDS.toMinutes(eta);
				etaUnit = " min";
				if (eta > 120){
					eta = TimeUnit.MINUTES.toHours(eta);
					etaUnit = " hours";
					if (eta > 48){
						eta = TimeUnit.HOURS.toDays(eta);
						etaUnit = " days";
					}
				}
			}		
			uiHandler.setLoadingText("Loading image " + (int)(100*((float)i/(float)imageHeight)) + "%  "
					+ "ETA: " + eta+etaUnit);
		}//for height
		
		switch (colorMode) {
		case UIHandler.CONSTANT_COLORING:
			img = calculateColorValuesConst(itrArray);
			break;

		default:
			break;
		}
		uiHandler.setLoadingText("");
		return img;
	}
	
	@Deprecated
	public BufferedImage calculateImageRGBwithBD(){
		
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		BigDecimal stepX = new BigDecimal(xmax-xmin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(imageWidth),zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN);
		BigDecimal stepY = new BigDecimal(ymax-ymin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(imageHeight),zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN);
		
		itrArray = new int[imageHeight][imageWidth];
		
		long startTime = System.nanoTime();
		long eta;
		long timeElapsedFromStart;
		String etaUnit = "s";
		
		ComplexBigDecimal c;
		int colorMode = uiHandler.getColorMode();
		
		for (int i = 0; i < imageHeight; i++){
			for (int j = 0; j < imageWidth; j++){
				c = new ComplexBigDecimal((new BigDecimal(xmin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP)).add(new BigDecimal(j).multiply(stepX)).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN),
											(new BigDecimal(ymin).divide(zoomBD,zoomBD.precision()+START_SCALE,BigDecimal.ROUND_HALF_UP)).add(new BigDecimal(i).multiply(stepY)).setScale(zoomBD.precision()+START_SCALE,BigDecimal.ROUND_DOWN));
				switch(colorMode){
					case(UIHandler.COLORFUL):
						img.setRGB(j, i, calculateColorValueSYC(c.add(middleBD)).getRGB());
						break;
					case(UIHandler.BLACK_WHITE):
						img.setRGB(j, i, calculateColorValueBW(c.add(middleBD)).getRGB());
						break;
					case UIHandler.CONSTANT_COLORING:
						int iterAdLevel = 100;
						try{
							iterAdLevel = uiHandler.getIterLevel();
						}catch (Exception e){return new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);}
						
						int maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
						itrArray[i][j] = MandelbrotTools.diverginNumberWithBD(c.add(middleBD), maxItr);
						break;
				}//Switch
			}//for width
			
			timeElapsedFromStart = System.nanoTime()-startTime;
			eta = timeElapsedFromStart/(long)(i+1)*(imageWidth-i-1);
			eta = TimeUnit.NANOSECONDS.toSeconds(eta); //sekunneiksi
			etaUnit = "s";
			
			if (eta > 120){
				eta = TimeUnit.SECONDS.toMinutes(eta);
				etaUnit = " min";
				if (eta > 120){
					eta = TimeUnit.MINUTES.toHours(eta);
					etaUnit = " hours";
					if (eta > 48){
						eta = TimeUnit.HOURS.toDays(eta);
						etaUnit = " days";
					}
				}
			}

					
			uiHandler.setLoadingText("Loading image " + (int)(100*((float)i/(float)imageHeight)) + "%  "
					+ "ETA: " + eta+etaUnit);
			
		}//for height
		switch (colorMode) {
		case UIHandler.CONSTANT_COLORING:
			img = calculateColorValuesConst(itrArray);
			break;

		default:
			break;
		}
		uiHandler.setLoadingText("");
		return img;
	}
	
	private BufferedImage calculateColorValuesConst(int[][] itrArray){
		
		double minItr = itrArray[0][0];
		double maxItr = itrArray[0][0];
		
		double peakPointRed = 0.6;
		double peakPointGreen = 0.4;
		
		
		// Lets check what is the minimum and maximum iteration in the calculated array
		for (int i = 0; i < itrArray.length; i++){
			for (int j = 0; j < itrArray[0].length; j++){
				minItr = (itrArray[i][j] < minItr) ? itrArray[i][j] : minItr;
				maxItr = (itrArray[i][j] > maxItr) ? itrArray[i][j] : maxItr;
			}
		}
		
		double minGreen = minItr + (maxItr-minItr)*0.35;
		
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		int r = 0,g = 0,b = 0;
		for (int i = 0; i < itrArray.length; i++){
			for (int j = 0; j < itrArray[0].length; j++){
				r = (itrArray[i][j] < peakPointRed*maxItr) ? 
						(int)(Math.sin(Math.PI*(itrArray[i][j] - minItr)/(peakPointRed*2.0*(maxItr-minItr)))*255) 
						:(int)(Math.sin(Math.PI*((itrArray[i][j] - minItr+(2*peakPointRed-1)*(minItr-maxItr)))/((1.0-peakPointRed)*2.0*(maxItr-minItr)))*255) ;
				
				g = (itrArray[i][j] < peakPointGreen*maxItr) ? 
						(int)(Math.sin(Math.PI*(itrArray[i][j] - minGreen)/(peakPointGreen*2.0*(maxItr-minGreen)))*255) 
						:(int)(Math.sin(Math.PI*((itrArray[i][j] - minGreen+(2*peakPointGreen-1)*(minGreen-maxItr)))/((1.0-peakPointGreen)*2.0*(maxItr-minGreen)))*255) ;
				g = (itrArray[i][j] < minGreen) ? 0 : g;
				if (r < 0 || g < 0 || b < 0){
					r = Math.abs(r);
					g = Math.abs(g);
					b = Math.abs(b);
				}
				img.setRGB(j, i, new Color(r, g, b).getRGB());
			}
		}
		return img;
	}
	
	private BufferedImage calculateColorValueBlackWhite(int[][] itrArray){
		
		int iterAdLevel = 100;
		try{
			iterAdLevel = uiHandler.getIterLevel();
		}catch (Exception e){}
		
		int maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
		int brightValue;
		
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		
		for (int i = 0; i < itrArray.length; i++){
			for (int j = 0; j < itrArray[0].length; j++){
				if (maxItr <= 255){
					brightValue =  ((int)(255.0 * (double)itrArray[i][j]/(double)maxItr));
					img.setRGB(j, i, new Color(brightValue,brightValue,brightValue).getRGB());
				}else{
					int lowerBound = maxItr-255;
					brightValue = (itrArray[i][j]-lowerBound <= 0) ? 0 : itrArray[i][j]-lowerBound;
					img.setRGB(j, i, new Color(brightValue,brightValue,brightValue).getRGB());
				}
			}
		}
		return img;
	}
	
	private BufferedImage calculateColorValueColorfaul(int[][] itrArray){
		
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		
		int iterAdLvl = 100;
		try{
			iterAdLvl = uiHandler.getIterLevel();
		}catch (Exception e){}
		
		int r,g,b;
		
		int itr = 0;
		
		for (int i = 0; i < imageHeight; i++){
			for (int j = 0; j < imageWidth; j++){
				
				itr = itrArray[i][j];
				r = 0;
				g = 0;
				b = 0;
				
				if (itr <= 40){
					r = (int)(Math.sin(Math.PI*itr/80)*255);
				}else if (itr <= 100){
					r = 255;
				}
				
				while (itr > 900){
					itr -= 900;
				}
			
				if (itr <= 100){
					g = (int)(((double)255/(double)100) * itr);
				}else if (itr <= 200){
					g = 255;
					r = 255 - (int)(((double)255/(double)100) * (itr-100));
				}else if (itr <= 300){
					b = (int)(((double)255/(double)100) * (itr-200));
					g = 255 - (int)(((double)255/(double)100) * (itr-200));
				}else if (itr <= 400){
					r = (int)(((double)255/(double)100) * (itr-300));
					b = 255;
				}else if (itr <= 500){
					r = 255;
					b = 255;
					g = (int)(((double)255/(double)100) * (itr-400));
				}else if (itr <= 600){
					r = 255 - (int)(((double)255/(double)100) * (itr-500));
					b = 255 - (int)(((double)255/(double)100) * (itr-500));
					g = 255 - (int)(((double)255/(double)100) * (itr-500));
				}else if (itr <= 700){
					r = (int)(((double)255/(double)100) * (itr-600));
				}else if (itr <= 800){
					r = 255 - (int)(((double)255/(double)100) * (itr-700));
					b = (int)(((double)255/(double)100) * (itr-700));
				}else if (itr <= 900){
					b = 255 - (int)(((double)255/(double)100) * (itr-800));
				}
				img.setRGB(j, i, new Color(r,g,b).getRGB());
			}
		}
		
		return img;
	}
	
	@Deprecated
	private Color calculateColorValueBW(ComplexDouble c){
		int iterAdLevel = 100;
		try{
			iterAdLevel = uiHandler.getIterLevel();
		}catch (Exception e){
			return new Color(255,0,0);
		}

		int maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
		int brightValue;
		
		int itr = MandelbrotTools.diverginNumber(c, maxItr);
		if (maxItr <= 255){
			brightValue = (int)(255.0 * (double)itr/(double)maxItr);
		}else{
			int lowerBound = maxItr-255;
			brightValue = (itr-lowerBound <= 0) ? 0 : itr-lowerBound;
		}
		return new Color(brightValue,brightValue,brightValue);
	}
	@Deprecated
	private Color calculateColorValueBW(ComplexBigDecimal c){
		int iterAdLevel = 100;
		try{
			iterAdLevel = uiHandler.getIterLevel();
		}catch (Exception e){
			return new Color(255,0,0);
		}

		int maxItr = ((int)((double)iterAdLevel*Math.log10(zoom)) < 50) ? 50 : (int)((double)iterAdLevel*Math.log10(zoom));
		int brightValue;
		
		int itr = MandelbrotTools.diverginNumberWithBD(c, maxItr);
		if (maxItr <= 255){
			brightValue = (int)(255.0 * (double)itr/(double)maxItr);
		}else{
			int lowerBound = maxItr-255;
			brightValue = (itr-lowerBound <= 0) ? 0 : itr-lowerBound;
		}
		return new Color(brightValue,brightValue,brightValue);
	}
	
	private Color calculateColorValueSYC(ComplexDouble c){
		
		int iterAdLvl = 100;
		
		try{
			iterAdLvl = uiHandler.getIterLevel();
		}catch (Exception e) {
			return new Color(255,0,0);
		}
		
		int maxItr = ((int)((double)iterAdLvl*Math.log10(zoom)) < 600) ? 600 : (int)((double)iterAdLvl*Math.log10(zoom));
		
		
		int itr =  MandelbrotTools.diverginNumber(c, maxItr);
		

		int r = 0;
		int g = 0;
		int b = 0;
		
		if (itr <= 40){
			r = (int)(Math.sin(Math.PI*itr/80)*255);
		}else if (itr <= 100){
			r = 255;
		}
		
		while (itr > 900){
			itr -= 900;
		}
	
		if (itr <= 100){
			g = (int)(((double)255/(double)100) * itr);
		}else if (itr <= 200){
			g = 255;
			r = 255 - (int)(((double)255/(double)100) * (itr-100));
		}else if (itr <= 300){
			b = (int)(((double)255/(double)100) * (itr-200));
			g = 255 - (int)(((double)255/(double)100) * (itr-200));
		}else if (itr <= 400){
			r = (int)(((double)255/(double)100) * (itr-300));
			b = 255;
		}else if (itr <= 500){
			r = 255;
			b = 255;
			g = (int)(((double)255/(double)100) * (itr-400));
		}else if (itr <= 600){
			r = 255 - (int)(((double)255/(double)100) * (itr-500));
			b = 255 - (int)(((double)255/(double)100) * (itr-500));
			g = 255 - (int)(((double)255/(double)100) * (itr-500));
		}else if (itr <= 700){
			r = (int)(((double)255/(double)100) * (itr-600));
		}else if (itr <= 800){
			r = 255 - (int)(((double)255/(double)100) * (itr-700));
			b = (int)(((double)255/(double)100) * (itr-700));
		}else if (itr <= 900){
			b = 255 - (int)(((double)255/(double)100) * (itr-800));
		}
		
		
		return new Color(r,g,b);
	}
	
	private Color calculateColorValueSYC(ComplexBigDecimal c){
		
		int iterAdLvl = 100;
		
		try{
			iterAdLvl = uiHandler.getIterLevel();
		}catch (Exception e) {
			return new Color(255,0,0);
		}
		
		int maxItr = ((int)((double)iterAdLvl*Math.log10(zoom)) < 600) ? 600 : (int)((double)iterAdLvl*Math.log10(zoom));
		
		
		int itr =  MandelbrotTools.diverginNumberWithBD(c, maxItr);
		

		int r = 0;
		int g = 0;
		int b = 0;
		
		if (itr <= 40){
			r = (int)(Math.sin(Math.PI*itr/80)*255);
		}else if (itr <= 100){
			r = 255;
		}
		
		while (itr > 900){
			itr -= 900;
		}
	
		if (itr <= 100){
			g = (int)(((double)255/(double)100) * itr);
		}else if (itr <= 200){
			g = 255;
			r = 255 - (int)(((double)255/(double)100) * (itr-100));
		}else if (itr <= 300){
			b = (int)(((double)255/(double)100) * (itr-200));
			g = 255 - (int)(((double)255/(double)100) * (itr-200));
		}else if (itr <= 400){
			r = (int)(((double)255/(double)100) * (itr-300));
			b = 255;
		}else if (itr <= 500){
			r = 255;
			b = 255;
			g = (int)(((double)255/(double)100) * (itr-400));
		}else if (itr <= 600){
			r = 255 - (int)(((double)255/(double)100) * (itr-500));
			b = 255 - (int)(((double)255/(double)100) * (itr-500));
			g = 255 - (int)(((double)255/(double)100) * (itr-500));
		}else if (itr <= 700){
			r = (int)(((double)255/(double)100) * (itr-600));
		}else if (itr <= 800){
			r = 255 - (int)(((double)255/(double)100) * (itr-700));
			b = (int)(((double)255/(double)100) * (itr-700));
		}else if (itr <= 900){
			b = 255 - (int)(((double)255/(double)100) * (itr-800));
		}
		
		return new Color(r,g,b);
	}
	
	public ComplexDouble TransformCoordinateToComplex(int x, int y){
		
		double stepX = (double)(xmax-xmin)/zoom/(double)imageWidth;
		double stepY = (double)(ymax-ymin)/zoom/(double)imageHeight;
		
		return new ComplexDouble((double)xmin/zoom+x*stepX, (double)ymin/zoom+y*stepY).add(middle);
	}
	
	public ComplexBigDecimal TransformCoordinateToComplexBD(int x, int y){
		
		BigDecimal stepX = new BigDecimal(xmax-xmin).divide(zoomBD,zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(imageWidth),zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_HALF_UP).setScale(zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_DOWN);
		BigDecimal stepY = new BigDecimal(ymax-ymin).divide(zoomBD,zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(imageHeight),zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_HALF_UP).setScale(zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_DOWN);

		
		return new ComplexBigDecimal(
				(new BigDecimal(xmin).divide(zoomBD,zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_HALF_UP)).add(new BigDecimal(x).multiply(stepX)).setScale(zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_DOWN),
				(new BigDecimal(ymin).divide(zoomBD,zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_HALF_UP)).add(new BigDecimal(y).multiply(stepY)).setScale(zoomBD.precision()+START_SCALE+16,BigDecimal.ROUND_DOWN))
				.add(middleBD);
	}
	
	public void setUIHandler(UIHandler uih){
		this.uiHandler = uih;
	}
	
	public void setMiddle(ComplexDouble c){
		middle = c;
	}
	
	public void setMiddleBD(ComplexBigDecimal c){
		middleBD = c;
	}
	
	public void setZoom(double z){
		this.zoom = z;
	}
	
	public void setZoomBD(BigDecimal z){
		zoomBD = z;
	}
	
	public double getZoom(){
		return zoom;
	}
	
	public BigDecimal getZoomBD(){
		return zoomBD;
	}
	
	public void setWidth(int w){
		this.imageWidth = w;
	}
	
	public void setHeight(int h){
		this.imageHeight = h;
	}
	
	public int getWidth(){
		return imageWidth;
	}
	
	public int getHeight(){
		return imageHeight;
	}
	
	public void setXYMinMax(double xmin, double xmax, double ymin, double ymax){
		this.xmax = xmax;
		this.xmin = xmin;
		this.ymax = ymax;
		this.ymin = ymin;
	}

}
