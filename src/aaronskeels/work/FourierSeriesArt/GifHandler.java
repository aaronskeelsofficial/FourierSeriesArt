package aaronskeels.work.FourierSeriesArt;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;

/*Uses https://github.com/square/gifencoder*/

public class GifHandler {
	private BufferedOutputStream outputStream;
	private GifEncoder encoder;
	private ImageOptions options;
	
	public GifHandler(String filePath) {
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
			encoder = new GifEncoder(outputStream, (int) (Main.UNIT_CIRCLE_RADIUS_PIXELS*2), (int) (Main.UNIT_CIRCLE_RADIUS_PIXELS*2), 0);
			options = new ImageOptions();
			options.setDelay((long) (Main.TIME_DELTA*1000d), TimeUnit.MILLISECONDS);
//		    options.setDitherer(FloydSteinbergDitherer.INSTANCE);
//		    options.setDitherer(NearestColorDitherer.INSTANCE);
		    options.setColorQuantizer(null);
		    options.setDitherer(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		new GifEncoder(outputStream, 500, 313, 0)
//        .addImage(convertImageToArray(image1), options)
//        .addImage(convertImageToArray(image2), options)
//        .addImage(convertImageToArray(image3), options)
//        .addImage(convertImageToArray(image4), options)
//        .finishEncoding();
	}
	
	public void addImage(BufferedImage bi) {
		try {
			encoder.addImage(convertImageToArray(bi), options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finishEncoding() {
		try {
			encoder.finishEncoding();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int[][] convertImageToArray(BufferedImage bufferedImage) throws IOException {
	    int[][] rgbArray = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
	    for (int i = 0; i < bufferedImage.getHeight(); i++) {
	      for (int j = 0; j < bufferedImage.getWidth(); j++) {
	        rgbArray[i][j] = bufferedImage.getRGB(j, i);
	      }
	    }
	    return rgbArray;
	}
	
}
