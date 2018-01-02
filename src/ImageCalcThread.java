import java.awt.image.BufferedImage;

public class ImageCalcThread extends Thread {
	
	private ImageHandler imageHandler;
	private UIHandler uiHandler;
	private int[][] itrArray;
	
	private ImageCalcThredHelper t1;
	private ImageCalcThredHelper t2;
	private ImageCalcThredHelper t3;
	private ImageCalcThredHelper t4;
	
	public ImageCalcThread(ImageHandler ih, UIHandler uih) {
		this.imageHandler = ih;
		this.uiHandler = uih;
	}
	
	public void run(){
		
//		boolean useBigDecimal = uiHandler.getUseBigDecimal();
//		BufferedImage img;
//		if (useBigDecimal){
//			img = imageHandler.calculateImageRGBwithBD();
//		}else{
//			img = imageHandler.calculateImageRGB();
//		}
//		uiHandler.reciveImage(img);
		
		// Lets just do four new threads for now. Most systems have four cores so they can
		// all be utilized
		itrArray = new int[imageHandler.getHeight()][imageHandler.getWidth()];
		t1 = new ImageCalcThredHelper(this, imageHandler, 0, 4);
		t2 = new ImageCalcThredHelper(this, imageHandler, 1, 4);
		t3 = new ImageCalcThredHelper(this, imageHandler, 2, 4);
		t4 = new ImageCalcThredHelper(this, imageHandler, 3, 4);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
	}
	
	public synchronized void reciveIterMatrix(int[][] itrMat){
		for (int i = 0; i < itrArray.length; i++){
			for (int j = 0; j < itrArray[0].length; j++){
				itrArray[i][j] = (itrMat[i][j] > 0) ? itrMat[i][j] : itrArray[i][j];
			}
		}
		finished();
	}
	
	private void finished(){
		int deadThreads = 0;
		deadThreads = t1.isAlive() ? deadThreads : deadThreads+1;
		deadThreads = t2.isAlive() ? deadThreads : deadThreads+1;
		deadThreads = t3.isAlive() ? deadThreads : deadThreads+1;
		deadThreads = t4.isAlive() ? deadThreads : deadThreads+1;
		
		if (deadThreads >= 3){
			uiHandler.reciveImage(imageHandler.intArrayToImage(itrArray));
			uiHandler.setLoadingText("");
		}
	}

}
