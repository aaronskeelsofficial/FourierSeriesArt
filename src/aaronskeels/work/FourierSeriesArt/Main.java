package aaronskeels.work.FourierSeriesArt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class Main {
	//Config Numbers
//	public static final double UNIT_CIRCLE_RADIUS_PIXELS = 500;
	//Graphics objects
	public static final JFrame frame = new JFrame("Fourier Series Art");
	public static BufferedImage OriginalImage;
	public static BufferedImage OffscreenImage = new BufferedImage(4000, 4000, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage TargetImage = new BufferedImage(4000, 4000, BufferedImage.TYPE_INT_ARGB);
	public static final MainPanel mainPanel = new MainPanel();
	public static final PickImgPanel pickImgPanel = new PickImgPanel();
	//Other global objects
	public static int contourCount;
	public static List<List<DecayingPixel>> originalDrawings;
	public static double duration;
	public static int circleCount;
	public static double TIME_DELTA; //Capitalized because it USED to be final, but is configurable now. Don't want to change.
	public static double trailLifetimePercent;
	public static final String DOWNLOAD_PATH = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
	public static double overlapVisibility;
	public static double UNIT_CIRCLE_RADIUS_PIXELS = 500; //Capitalized because it USED to be final, but is configurable now. Don't want to change.
	
	public static void main(String[] args) {
		//Setup frame config
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        //Add components to frame
        //Functionality handling is passed off to pickImgPanel
        frame.add(pickImgPanel);
        
        //Finally show frame
        frame.setVisible(true);
	}
	
	public static void debugDrawOriginalDrawing() {
		//Draws the "contour" interpretations
		BufferedImage contourPixelatedOriginal = new BufferedImage(OriginalImage.getWidth(), OriginalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int contourNumber = 0;contourNumber < originalDrawings.size();contourNumber++) {
			debugDrawOriginalDrawing(contourNumber, contourPixelatedOriginal);
		}
		openMaximizedImage(contourPixelatedOriginal);
	}
	public static void debugDrawOriginalDrawing(int contourNumber, BufferedImage contourPixelatedOriginal) {
		//This shows the contour interpretation from OpenCV
		//Includes an "optional shift for debug" to visually show what is happening with the Fourier and multiplying f(t) by some complex number to target it's coefficients
		// The shift is just multiplying f(t) by target complex number and showing what is actually being considered by the f(t) integral (aka center of mass computation)
		int red = Color.red.getRGB();
//		int index = 0;
		for (DecayingPixel dp : originalDrawings.get(contourNumber)) {
			double x = clamp(mapToRange(dp.x, -1, 1, 0, contourPixelatedOriginal.getWidth()), 0, contourPixelatedOriginal.getWidth());
			double y = clamp(mapToRange(dp.y, -1, 1, 0, contourPixelatedOriginal.getHeight()), 0, contourPixelatedOriginal.getHeight());
			//Optional shift for debug
//			double x = dp.x;
//			double y = dp.y;
//			double A = 1;
//			double t = (double) index / (double) originalDrawing.size();
//			double theta = -2 * Math.PI * 1 * t;
//			double[] result = complexMultiplyCartPolar(x, y, A, theta);
//			x = clamp(mapToRange(result[0], -1, 1, 0, contourPixelatedOriginal.getWidth()), 0, contourPixelatedOriginal.getWidth()-1);
//			y = clamp(mapToRange(result[1], -1, 1, 0, contourPixelatedOriginal.getHeight()), 0, contourPixelatedOriginal.getHeight()-1);
//			index++;
			contourPixelatedOriginal.setRGB((int) x, (int) y, red);
		}
	}
	
	private static Map<Integer, List<NTierCircle>> debugDrawAnimation_db; //<contourNumber, list of circles>
	public static void debugDrawAnimation() {
		//Draws circles rotating across time
		//Reset db
		debugDrawAnimation_db = new HashMap<>();
		//Setup Window
		JFrame frame2 = new JFrame();
		frame2.setExtendedState(JFrame.MAXIMIZED_BOTH);
		TestCirclePanel panel2 = new TestCirclePanel();
		panel2.setBackground(Color.blue);
		frame2.add(panel2);
		//TODO: Add gif encoding functionality for download.
//		JButton button = new JButton("Download GIF");
		frame2.setVisible(true);
		//Draw on window
		Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	clearImage(panel2.bi, Color.white);
        		for (int contourNumber = 0;contourNumber < originalDrawings.size();contourNumber++) {
        			debugDrawAnimation(contourNumber, panel2);
        		}
        		panel2.repaint();
            }
        };
        timer.scheduleAtFixedRate(task, 0, (long) (TIME_DELTA*1000));
        //Handle frame close (reset timer so new "duration" can carry over and cancel timer so no memory leak)
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.cancel();
            }
        };
        frame2.addWindowListener(windowListener);
	}
	public static void debugDrawAnimation(int contourNumber, TestCirclePanel panel2) {
		//This is called on a timer over and over, so we cache the circles to prevent having to compute them every time
		if (!debugDrawAnimation_db.containsKey(contourNumber)) {
			List<NTierCircle> circles = new ArrayList<NTierCircle>();
			Random rand = new Random();
			Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), .5f);
			for (int i = circleCount;i > 0;i--) {
				NTierCircle negative = new NTierCircle(-i, contourNumber, color);
				if (!circles.isEmpty())
					negative.childCircle = circles.get(circles.size()-1);
				NTierCircle positive = new NTierCircle(i, contourNumber, color, negative);
				circles.add(negative);
				circles.add(positive);
			}
			NTierCircle c0 = new NTierCircle(0, contourNumber, color);
			if (!circles.isEmpty())
				c0.childCircle = circles.get(circles.size()-1);
			circles.add(c0);
			debugDrawAnimation_db.put(contourNumber, circles);
		}
		List<NTierCircle> circles = debugDrawAnimation_db.get(contourNumber);
		Graphics2D g2d = panel2.bi.createGraphics();
    	circles.get(circles.size()-1).draw(UNIT_CIRCLE_RADIUS_PIXELS, UNIT_CIRCLE_RADIUS_PIXELS, g2d);
    	g2d.dispose();
	}
	
	public static void debugDrawFinishedTrace() {
		//Draws the completed trace from t=0 -> t=1 (0% -> 100% of a cycle)
		JFrame frame2 = new JFrame();
		frame2.setExtendedState(JFrame.MAXIMIZED_BOTH);
		TestCirclePanel panel2 = new TestCirclePanel();
		panel2.setBackground(Color.blue);
		frame2.add(panel2);
		frame2.setVisible(true);
		clearImage(panel2.bi, Color.white);
		for (int contourNumber = 0;contourNumber < originalDrawings.size();contourNumber++) {
			debugDrawFinishTrace(contourNumber, panel2);
		}
		panel2.repaint();
	}
	public static void debugDrawFinishTrace(int contourNumber, TestCirclePanel panel2) {
		//Auto-build up circle hierarchy in accordance to configured circles
		List<NTierCircle> circles = new ArrayList<>();
		Random rand = new Random();
		Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), .5f);
		for (int i = circleCount;i > 0;i--) {
			NTierCircle negative = new NTierCircle(-i, contourNumber, color);
			if (!circles.isEmpty())
				negative.childCircle = circles.get(circles.size()-1);
			NTierCircle positive = new NTierCircle(i, contourNumber, color, negative);
			circles.add(negative);
			circles.add(positive);
		}
		NTierCircle c0 = new NTierCircle(0, contourNumber, color);
		if (!circles.isEmpty())
			c0.childCircle = circles.get(circles.size()-1);
		//Draw
		for (double t = 0;t < 1;t += .001d) {
			//Starts mapped -1->1
			double x = c0.getTotalOffsetXOfChain(t);
			double y = c0.getTotalOffsetYOfChain(t);
			//Convert to -Pixel->Pixel
			x *= UNIT_CIRCLE_RADIUS_PIXELS;
			y *= UNIT_CIRCLE_RADIUS_PIXELS;
			//Shift origin
			x += UNIT_CIRCLE_RADIUS_PIXELS;
			y += UNIT_CIRCLE_RADIUS_PIXELS;
			//Clamp
			x = clamp(x, 0, 2*UNIT_CIRCLE_RADIUS_PIXELS-1);
			y = clamp(y, 0, 2*UNIT_CIRCLE_RADIUS_PIXELS-1);
//			System.out.println("(" + x + "," + y + ")");
			panel2.bi.setRGB((int) x, (int) y, Color.red.getRGB());
			Graphics2D g2d = panel2.bi.createGraphics();
			g2d.setColor(Color.red);
			g2d.drawRect((int) x, (int) y, 1, 1);
			g2d.dispose();
		}
	}
	
	public static void debugDrawTrail(boolean includeCircles) {
		//Draws the animated trail. First pass has majority dead pixels, second and subsequent passes show trailing pixels
		//Setup Window
		JFrame frame2 = new JFrame();
		frame2.setExtendedState(JFrame.MAXIMIZED_BOTH);
		TrailPanel panel2 = new TrailPanel();
		panel2.setBackground(Color.blue);
		frame2.add(panel2);
		//TODO: Add gif encoding functionality for download.
//		JButton button = new JButton("Download GIF");
		frame2.setVisible(true);
		//Add circles and pixels to jpanel
		for (int contourNumber = 0;contourNumber < originalDrawings.size();contourNumber++) {
			debugDrawTrail(contourNumber, panel2);
		}
		//Draw on window
//		panel2.redrawPixels();
		Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	Main.clearImage(panel2.bi, Color.white);
            	Graphics2D g2d = panel2.bi.createGraphics();
            	if (includeCircles)
            		panel2.redrawCircles(g2d);
            	panel2.redrawPixels(g2d);
            	g2d.dispose();
            	panel2.repaint();
            }
        };
        timer.scheduleAtFixedRate(task, 0, (long) (TIME_DELTA*1000));
        //Handle frame close (reset timer so new "duration" can carry over and cancel timer so no memory leak)
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.cancel();
            }
        };
        frame2.addWindowListener(windowListener);
	}
	public static void debugDrawTrail(int contourNumber, TrailPanel panel) {
		long curTime = System.currentTimeMillis();
		//Load circles
		List<NTierCircle> circles = new ArrayList<NTierCircle>();
		Random rand = new Random();
		Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), .5f);
		for (int i = circleCount;i > 0;i--) {
			NTierCircle negative = new NTierCircle(-i, contourNumber, color);
			if (!circles.isEmpty())
				negative.childCircle = circles.get(circles.size()-1);
			NTierCircle positive = new NTierCircle(i, contourNumber, color, negative);
			circles.add(negative);
			circles.add(positive);
		}
		NTierCircle c0 = new NTierCircle(0, contourNumber, color);
		if (!circles.isEmpty())
			c0.childCircle = circles.get(circles.size()-1);
		circles.add(c0);
		panel.circlesDB.add(circles);
		//Load in pixels
		for (double t = 0;t < 1;t += .001d) {
			//Starts mapped -1->1
			double x = c0.getTotalOffsetXOfChain(t);
			double y = c0.getTotalOffsetYOfChain(t);
			DecayingPixel dp = new DecayingPixel(x, y, duration*trailLifetimePercent*1000d);
			dp.birth((long) (curTime + t*duration*1000));
			panel.pixels.add(dp);
		}
	}
	
	public static void downloadMedia(boolean includeCircles, boolean includePixels, String filename) {
		//Download animations as MP4 (gif took too long to encode, gross)
		// Unfortunately OpenCV exports don't allow encoding customization and AV1 is forced yielding LARGE file size and still relatively slowish export (GIF is much slower though)
		// Re-export properly by converting to actually useful encoding via VLC externally after.
		List<List<NTierCircle>> circleDB = new ArrayList<>();
		List<List<DecayingPixel>> pixelDB = new ArrayList<>();
		System.out.println("Starting process.");
		for (int contourNumber = 0;contourNumber < originalDrawings.size();contourNumber++) {
			//Load Circles
			List<NTierCircle> circles = new ArrayList<>();
			Random rand = new Random();
			Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), .5f);
			for (int i = circleCount;i > 0;i--) {
				NTierCircle negative = new NTierCircle(-i, contourNumber, color);
				if (!circles.isEmpty())
					negative.childCircle = circles.get(circles.size()-1);
				NTierCircle positive = new NTierCircle(i, contourNumber, color, negative);
				circles.add(negative);
				circles.add(positive);
			}
			NTierCircle c0 = new NTierCircle(0, contourNumber, color);
			if (!circles.isEmpty())
				c0.childCircle = circles.get(circles.size()-1);
			circles.add(c0);
			circleDB.add(circles);
			//Load Pixels
			if (includePixels) {
				List<DecayingPixel> pixels = new ArrayList<>();
				for (double t = 0;t < 1;t += .001d) {
					//Starts mapped -1->1
					double x = c0.getTotalOffsetXOfChain(t);
					double y = c0.getTotalOffsetYOfChain(t);
					DecayingPixel dp = new DecayingPixel(x, y, duration*trailLifetimePercent*1000d);
					dp.birth((long) (t*duration*1000d));
					pixels.add(dp);
				}
				pixelDB.add(pixels);
			}
		}
		System.out.println("Generated circles and pixels");
		//Generate media type handler
//		GifHandler gif = new GifHandler("C:\\Users\\Aaron\\Downloads\\gif.gif");
		FfmpegHandler ffmpeg = new FfmpegHandler(DOWNLOAD_PATH + filename + ".mp4");
		//Generate frames
		BufferedImage bi = new BufferedImage((int) (2*UNIT_CIRCLE_RADIUS_PIXELS), (int) (2*UNIT_CIRCLE_RADIUS_PIXELS), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		for (double t = 0;t < 2;t += TIME_DELTA / duration) { //We use a final time of t = 2 because the first passover has majority invis points, second passover has trail
			System.out.println("Working on t = " + t + " (2)");
			g2d.setColor(Color.white); // Transparent black color
	        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        	//Draw circles
	        if (includeCircles) {
	        	for (int i = 0;i < circleDB.size();i++) {
	    			List<NTierCircle> circles = circleDB.get(i);
	    			circles.get(circles.size()-1).draw(Main.UNIT_CIRCLE_RADIUS_PIXELS, Main.UNIT_CIRCLE_RADIUS_PIXELS, g2d, t);
	    		}
	        }
        	System.out.println("    Circles drawn.");
        	//Draw pixels
        	if (includePixels) {
        		for (int j = 0;j < pixelDB.size();j++) {
            		List<DecayingPixel> pixels = pixelDB.get(j);
            		for (int i = 0;i < pixels.size();i++) {
            			DecayingPixel dp = pixels.get(i);
            			Color color = new Color(1f, 0f, 0f, (float) dp.getOpacityDecimal((long) (duration*t*1000d)));
            			g2d.setColor(color);
            			g2d.drawRect((int) dp.xPixels, (int) dp.yPixels, 1, 1);
            		}
            	}
        	}
        	System.out.println("    Pixels drawn. Adding image to gif encoding.");
        	//Add image
//        	gif.addImage(bi);
        	ffmpeg.addImage(bi);
        	System.out.println("    Img added.");
		}
		g2d.dispose();
		//Finalize media encoding
//		gif.finishEncoding();
		ffmpeg.finishEncoding();
	}
	
	public static void downloadOverlap(String filename) {
		//Download a PNG of the overlap between the original image and the tracing done via the circles
		List<List<NTierCircle>> circleDB = new ArrayList<>();
		List<List<DecayingPixel>> pixelDB = new ArrayList<>();
		System.out.println("Starting process.");
		for (int contourNumber = 0;contourNumber < originalDrawings.size();contourNumber++) {
			//Load Circles
			List<NTierCircle> circles = new ArrayList<>();
			Random rand = new Random();
			Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), .5f);
			for (int i = circleCount;i > 0;i--) {
				NTierCircle negative = new NTierCircle(-i, contourNumber, color);
				if (!circles.isEmpty())
					negative.childCircle = circles.get(circles.size()-1);
				NTierCircle positive = new NTierCircle(i, contourNumber, color, negative);
				circles.add(negative);
				circles.add(positive);
			}
			NTierCircle c0 = new NTierCircle(0, contourNumber, color);
			if (!circles.isEmpty())
				c0.childCircle = circles.get(circles.size()-1);
			circles.add(c0);
			circleDB.add(circles);
			//Load Pixels
			List<DecayingPixel> pixels = new ArrayList<>();
			for (double t = 0;t < 1;t += .001d) {
				//Starts mapped -1->1
				double x = c0.getTotalOffsetXOfChain(t);
				double y = c0.getTotalOffsetYOfChain(t);
				DecayingPixel dp = new DecayingPixel(x, y, duration*trailLifetimePercent*1000d);
				dp.birth((long) (t*duration*1000d));
				pixels.add(dp);
			}
			pixelDB.add(pixels);
		}
		System.out.println("Generated circles and pixels");
		//Generate frame
		BufferedImage bi = new BufferedImage((int) (2*UNIT_CIRCLE_RADIUS_PIXELS), (int) (2*UNIT_CIRCLE_RADIUS_PIXELS), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		//Draw original image
		g2d.drawImage(OriginalImage, 0, 0, bi.getWidth(), bi.getHeight(), null);
		//Draw trail
		BufferedImage trailImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d_trail = trailImage.createGraphics();
		for (List<DecayingPixel> pixels : pixelDB) {
			for (DecayingPixel dp : pixels) {
				Color color = new Color(1f, 0f, 0f, (float) overlapVisibility);
				g2d_trail.setColor(color);
				g2d_trail.drawRect((int) dp.xPixels, (int) dp.yPixels, 1, 1);
			}
		}
		g2d_trail.dispose();
		//Merge trail to bi
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) overlapVisibility));
		g2d.drawImage(trailImage, 0, 0, bi.getWidth(), bi.getHeight(), null);
		System.out.println("    Pixels drawn.");
		try {
			ImageIO.write(bi, "PNG", new File(DOWNLOAD_PATH + filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println("    Img saved.");
		g2d.dispose();
	}
	
	public static boolean updateInputVars() {
		//Process all the UI inputs. I HATE UI.
		try {
			int value = Integer.valueOf(PickImgPanel.resolutionPanel_text.getText());
    		Main.UNIT_CIRCLE_RADIUS_PIXELS = value/2d;
		} catch (Exception e) {return false;}
		try {
			int value = Integer.valueOf(PickImgPanel.contourCountPanel_text.getText());
    		Main.contourCount = value;
		} catch (Exception e) {return false;}
		try {
			double value = Double.valueOf(PickImgPanel.durationPanel_text.getText());
    		Main.duration = value;
		} catch (Exception e) {return false;}
		try {
			int value = Integer.valueOf(PickImgPanel.circleCountPanel_text.getText());
    		Main.circleCount = value;
		} catch (Exception e) {return false;}
		try {
			double value = Double.valueOf(PickImgPanel.timeStepPanel_text.getText());
    		Main.TIME_DELTA = value;
		} catch (Exception e) {return false;}
		try {
			double value = Double.valueOf(PickImgPanel.lifetimePanel_text.getText());
    		Main.trailLifetimePercent = value;
		} catch (Exception e) {return false;}
		try {
			double value = Double.valueOf(PickImgPanel.overlapVisibilityPanel_text.getText());
    		Main.overlapVisibility = value;
		} catch (Exception e) {return false;}
		
		return true;
	}
	
	//
	// MISC UTILITIES
	//
	
	public static double clamp(double value, double min, double max) {
		//Clamp values between a min and max (used because high-circle instability of points near edge often times go out of canvas bounds)
		return Math.min(Math.max(value, min), max);
	}
	
	public static void clearImage(BufferedImage targetImage) {
		//Wipe image
		Graphics2D g2d = targetImage.createGraphics();
		g2d.setColor(new Color(0, 0, 0, 0)); // Transparent black color
        g2d.fillRect(0, 0, targetImage.getWidth(), targetImage.getHeight());
        g2d.dispose();
	}
	public static void clearImage(BufferedImage targetImage, Color overrideColor) {
		//Wipe image to color
		Graphics2D g2d = targetImage.createGraphics();
		g2d.setColor(overrideColor); // Transparent black color
        g2d.fillRect(0, 0, targetImage.getWidth(), targetImage.getHeight());
        g2d.dispose();
	}
	
	public static double[] complexMultiplyPolarPolar (double A1, double theta1, double A2, double theta2) {
		//Multiply a complex number in polar form by another complex number in polar form
		double a1 = A1*Math.cos(theta1);
		double b1 = A1*Math.sin(theta1);
		return complexMultiply(a1, b1, A2, theta2);
	}
	public static double[] complexMultiplyCartPolar (double a1, double b1, double A2, double theta2) {
		//Multiply a complex number in cartesian form by another complex number in polar form
		double a2 = A2*Math.cos(theta2);
		double b2 = A2*Math.sin(theta2);
		return complexMultiply(a1, b1, a2, b2);
	}
	public static double[] complexMultiply (double a1, double b1, double a2, double b2) {
		//Multiply two complex numbers in cartesian form
		return new double[] {(a1*a2 - b1*b2), (a1*b2 + a2*b1)};
	}
	
	public static boolean getIfShouldResizeAccordingToWidth(BufferedImage refImage) {
		//I don't think I used this in this program, but it would be useful to implement
		double widthPercentage = refImage.getWidth() / OffscreenImage.getWidth();
		double heightPercentage = refImage.getHeight() / OffscreenImage.getHeight();
		return Double.compare(widthPercentage, heightPercentage) >= 0d;
	}
	
	public static double getInterpolatedValue(double x1, double x2, double y1, double y2, double x) {
		//Linear interpolation
		return y1 + (x-x1)*(y2-y1)/(x2-x1);
	}
	
	public static double mapToRange(double value, double oldMin, double oldMax, double newMin, double newMax) {
		//Linear remap
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }
	
	public static void openMaximizedImage(BufferedImage targetImage) {
		//Shortcut to open a bufferedimage in an easily zoomable/scrollable way
		JFrame frame = new JFrame("Image Preview");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        //Create JScroll
        JScrollPane pane = new JScrollPane();
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        //Create JPanel
        ZoomableJPanel panel = new ZoomableJPanel(targetImage);
        pane.setViewportView(panel);
        
        //Add MouseWheelListener for zooming
        pane.addMouseWheelListener(e -> {
            int notches = -e.getWheelRotation();
            boolean isControlDown = e.isControlDown();

            if (isControlDown) {
                double scaleFactor = Math.pow(1.05, notches);
                double newScale = panel.getScale() * scaleFactor;

                // Limit the scale to a reasonable range
                if (newScale > 0.1 && newScale < 10.0) {
                    panel.setScale(newScale);
                    Dimension scaledSize = new Dimension((int) (panel.getPreferredSize().width * panel.getScale()), (int) (panel.getPreferredSize().height * panel.getScale()));
                    panel.setPreferredSize(scaledSize);
                    pane.revalidate();
                }
            }
        });
        //Adjust scroll speed
        JScrollBar verticalScrollBar = pane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(20);
        JScrollBar horizontalScrollBar = pane.getHorizontalScrollBar();
        horizontalScrollBar.setUnitIncrement(20);
        
        frame.add(pane);
        
        frame.setVisible(true);
	}
	
	public static double unitCircleToPixel(double unitCircleValue) {
		//Most values are handled via a -1->1 scale, and when drawing visually we need that converted to 0->pixelresolution
		// An important note is that the Unit Circle Radius is HALF the canvas resolution. That may make some calculations confusing.
		//Starts mapped -1->1
		double x = unitCircleValue;
		//Convert to -Pixel->Pixel
		x *= UNIT_CIRCLE_RADIUS_PIXELS;
		//Shift origin
		x += UNIT_CIRCLE_RADIUS_PIXELS;
		//Clamp
		x = clamp(x, 0, 2*UNIT_CIRCLE_RADIUS_PIXELS-1);
		return x;
	}
	
}