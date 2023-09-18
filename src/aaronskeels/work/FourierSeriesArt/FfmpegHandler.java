package aaronskeels.work.FourierSeriesArt;

import java.awt.image.BufferedImage;

import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

/*
 * Uses OpenCV. Export encoding is completely busted. OpenCV is wack.
 */

public class FfmpegHandler {
	VideoWriter vid;
	
	public FfmpegHandler(String filePath) {
//		int fourcc = VideoWriter.fourcc('H', '2', '6', '4'); //Doesn't work
//		int fourcc = VideoWriter.fourcc('V', 'P', '9', '0'); //Doesn't work
//		int fourcc = VideoWriter.fourcc('V', 'P', '0', '9'); //Doesn't work
//		int fourcc = VideoWriter.fourcc('v', 'p', '0', '9'); //Doesn't work
		int fourcc = OpenCVHandler.fourCCBuilder("H264");
		//Note: It doesn't matter what fourcc code is input, even invalid ones.
		// They all give the same result. The only thing which changes the attempted
		// output codec is the target file path extension for some reason. My system
		// freaks out upon trying to output anything other than .mp4 so my files sizes
		// are bloated.
		//My workaround for this is reprocessing the file's encoding via VLC after
		// the original export from this program.
		int fps = (int) (1d / Main.TIME_DELTA);
		vid = new VideoWriter(filePath, fourcc, fps, new Size(Main.UNIT_CIRCLE_RADIUS_PIXELS*2, Main.UNIT_CIRCLE_RADIUS_PIXELS*2));
	}
	
	public void addImage(BufferedImage bi) {
		vid.write(OpenCVHandler.bufferedImageToMat(bi));
	}
	
	public void finishEncoding() {
		vid.release();
	}
}
