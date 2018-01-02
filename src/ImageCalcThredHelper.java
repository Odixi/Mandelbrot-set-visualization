
public class ImageCalcThredHelper extends Thread{
	
	int phase;
	int periodLength;
	ImageCalcThread ict;
	ImageHandler imageHandler;
	
	public ImageCalcThredHelper(ImageCalcThread ict, ImageHandler ih, int phase, int periodLength) {
		this.phase = phase;
		this.periodLength = periodLength;
		this.imageHandler = ih;
		this.ict = ict;
	}
	
	public void run(){
		int[][] itrMat =  imageHandler.calculateIterMatrix(periodLength, phase);
		ict.reciveIterMatrix(itrMat);
	}
	
}
