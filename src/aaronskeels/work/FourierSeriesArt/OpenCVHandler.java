package aaronskeels.work.FourierSeriesArt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/*
 * This class is meant to handle all OpenCV interactions
 * OpenCV is pretty gross. It has quite small documentation
 * and especially poor documentation for Java despite having
 * a "wrapper." Some functionality just doesn't work.
 */

public class OpenCVHandler {
	public static void FindContours(File file) {
		//IMPORTANT: FOR THIS NATIVE LIBRARY TO RUN PROPERLY, IT MUST BE IN THE USER'S DOWNLOADS FOLDER.
		// The alternative was putting it in System32 or some other typical location, but I didn't want that stress.
		System.load(Main.DOWNLOAD_PATH + Core.NATIVE_LIBRARY_NAME + ".dll");
	    Mat src = Imgcodecs.imread(file.getAbsolutePath());
	    //Converting the source image to binary
	    Mat gray = new Mat(src.rows(), src.cols(), src.type());
	    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
	    Mat binary = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
	    Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY_INV);
	    //Finding Contours
	    List<MatOfPoint> contours = new ArrayList<>();
	    Mat hierarchey = new Mat();
	    Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
//	    Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE); //This looks terrible don't use this
	    //Identify largest contours
	    int[] matrixSizeArray = new int[contours.size()];
	    for (int i = 0;i < contours.size();i++) {
	    	matrixSizeArray[i] = contours.get(i).rows() * contours.get(i).cols();
	    }
	    List<Integer> maxMatrixIndices = findMaxIndices(matrixSizeArray, Main.contourCount);
	    //Get x,y coords and generate decayingpixels
	    Main.originalDrawings = new ArrayList<List<DecayingPixel>>();
	    for (int i = 0; i < maxMatrixIndices.size(); i++) {
	    	int properIndex = maxMatrixIndices.get(i);
	    	MatOfPoint matOfPoint = contours.get(properIndex);
	    	System.out.println("Matrix " + i + " size: " + (matOfPoint.rows()*matOfPoint.cols()));
	    	List<DecayingPixel> dps = new ArrayList<>();
	    	for (Point p : matOfPoint.toArray()) {
	    		//Transform from 0->width to 0->1
	    		double x = p.x/Main.OriginalImage.getWidth();
	    		double y = p.y/Main.OriginalImage.getHeight();
	    		//Transform from 0->1 to 0->2
	    		x *= 2;
	    		y *= 2;
	    		//Transform from 0->2 to -1->1
	    		x -= 1;
	    		y -= 1;
	    		
	    		dps.add(new DecayingPixel(x, y, (Main.duration*.75d)*1000d));
	    	}
	    	Main.originalDrawings.add(dps);
	    }
	}
	
	private static List<Integer> findMaxIndices(int[] arr, int numMaxValues) {
        if (numMaxValues <= 0/* || numMaxValues > arr.length*/) {
            throw new IllegalArgumentException("Invalid number of maximum values.");
        }
        if (numMaxValues > arr.length) {
        	System.out.println("Set contour count to " + numMaxValues + " but only " + arr.length + " contours exist.");
        	numMaxValues = arr.length;
        	Main.contourCount = arr.length;
        }

        List<Integer> maxIndices = new ArrayList<>();
        List<Integer> copy = new ArrayList<>();

        // Create a copy of the original array to preserve its order
        for (int i = 0; i < arr.length; i++) {
            copy.add(i);
        }

        // Sort the copy list based on the values in the original array
        Collections.sort(copy, (a, b) -> Integer.compare(arr[b], arr[a]));

        // Add the indices of the maximum values to the result list
        for (int i = 0; i < numMaxValues; i++) {
            maxIndices.add(copy.get(i));
        }

        return maxIndices;
    }
	
	@SuppressWarnings("unused")
	private static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] bytes = new byte[bufferSize];
        mat.get(0, 0, bytes);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);

        return image;
    }
	
	public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
	    int width = bufferedImage.getWidth();
	    int height = bufferedImage.getHeight();
	    Mat mat = new Mat(height, width, CvType.CV_8UC3);

	    int[] data = new int[width * height * 3];
	    bufferedImage.getRGB(0, 0, width, height, data, 0, width);

	    for (int row = 0; row < height; row++) {
	        for (int col = 0; col < width; col++) {
	            int pixel = data[row * width + col];
	            int red = (pixel) & 0xFF;
	            int green = (pixel >> 8) & 0xFF;
	            int blue = (pixel >> 16) & 0xFF;

	            mat.put(row, col, red, green, blue);
	        }
	    }

	    return mat;
	}
	
	public static int fourCCBuilder(String fourChars) {
		String hexString = "";
		for (char c : fourChars.toCharArray()) {
			String hexNotation = Integer.toHexString((int) c);
			System.out.println(c + " -> " + hexNotation);
			hexString += hexNotation;
		}
		int decimalInt = Integer.parseInt(hexString, 16);
		System.out.println("Hex String: " + hexString);
		System.out.println("Decimal: " + decimalInt);
		return decimalInt;
	}
}

