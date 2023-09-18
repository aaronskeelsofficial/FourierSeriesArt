package aaronskeels.work.FourierSeriesArt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aaronskeels.work.FourierSeriesArt.JnaFileChooser.JnaFileChooser;

/*
 * This class handles all the UI interactions. Gross.
 */

@SuppressWarnings("serial")
public class PickImgPanel extends JPanel{
	public static JTextField resolutionPanel_text, contourCountPanel_text, durationPanel_text, circleCountPanel_text, timeStepPanel_text,
		lifetimePanel_text, overlapVisibilityPanel_text;
	
	public PickImgPanel() {
		setBackground(Color.white);
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new FlowLayout());
		
		//Generate contour count
		JPanel resolutionPanel = new JPanel();
		resolutionPanel.setBackground(new Color(255, 0, 0, 200));
		resolutionPanel.setOpaque(true);
		JLabel resolutionPanel_label = new JLabel("Output Resolution [px]:");
		resolutionPanel_text = new JTextField(3);
		resolutionPanel_text.setText("1000");
		resolutionPanel.add(resolutionPanel_label);
		resolutionPanel.add(resolutionPanel_text);
		
		//Generate contour count
		JPanel contourCountPanel = new JPanel();
		contourCountPanel.setBackground(new Color(255, 0, 0, 200));
		contourCountPanel.setOpaque(true);
		JLabel contourCountPanel_label = new JLabel("Unique Contour/Line Count:");
		contourCountPanel_text = new JTextField(3);
		contourCountPanel_text.setText("1");
		contourCountPanel.add(contourCountPanel_label);
		contourCountPanel.add(contourCountPanel_text);
		
		//Generate duration
		JPanel durationPanel = new JPanel();
		durationPanel.setBackground(new Color(0, 255, 0, 200));
		durationPanel.setOpaque(true);
		JLabel durationPanel_label = new JLabel("Cycle Duration [s]:");
		durationPanel_text = new JTextField(3);
		durationPanel_text.setText("3");
		durationPanel.add(durationPanel_label);
		durationPanel.add(durationPanel_text);
		
		//Generate circle count
		JPanel circleCountPanel = new JPanel();
		circleCountPanel.setBackground(new Color(0, 10, 255, 200));
		circleCountPanel.setOpaque(true);
		JLabel circleCountPanel_label = new JLabel("Range Of Circles: (-# -> #)");
		circleCountPanel_text = new JTextField(3);
		circleCountPanel_text.setText("10");
		circleCountPanel.add(circleCountPanel_label);
		circleCountPanel.add(circleCountPanel_text);
		
		//Generate time step
		JPanel timeStepPanel = new JPanel();
		timeStepPanel.setBackground(new Color(0, 255, 255, 200));
		timeStepPanel.setOpaque(true);
		JLabel timeStepPanel_label = new JLabel("Simulation Time Step [s]:");
		timeStepPanel_text = new JTextField(3);
		timeStepPanel_text.setText(".05");
		timeStepPanel.add(timeStepPanel_label);
		timeStepPanel.add(timeStepPanel_text);
		
		//Generate lifetime
		JPanel lifetimePanel = new JPanel();
		lifetimePanel.setBackground(new Color(255, 255, 0, 200));
		lifetimePanel.setOpaque(true);
		JLabel lifetimePanel_label = new JLabel("Trail Lifetime % of Cycle [0 < x <= 1]:");
		lifetimePanel_text = new JTextField(3);
		lifetimePanel_text.setText(".9");
		lifetimePanel.add(lifetimePanel_label);
		lifetimePanel.add(lifetimePanel_text);
		
		//Generate lifetime
		JPanel overlapVisibilityPanel = new JPanel();
		overlapVisibilityPanel.setBackground(new Color(255, 0, 255, 200));
		overlapVisibilityPanel.setOpaque(true);
		JLabel overlapVisibilityPanel_label = new JLabel("Overlap Visibility [0 <= x <= 1]:");
		overlapVisibilityPanel_text = new JTextField(3);
		overlapVisibilityPanel_text.setText(".6");
		overlapVisibilityPanel.add(overlapVisibilityPanel_label);
		overlapVisibilityPanel.add(overlapVisibilityPanel_text);
		
		//Generate Preview Contour Stuff
		JButton previewContourButton = new JButton("Preview Contour Calculation");
		previewContourButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JnaFileChooser fc = new JnaFileChooser();
            	fc.addFilter("Images", "png", "tif", "tiff", "bmp", "jpg", "jpeg", "gif");
            	if (fc.showOpenDialog((Window) Main.frame)) {
            		File f = fc.getSelectedFile();
            		if (!Main.updateInputVars())
            			return;
            		//file -> image
            		try {
            			Main.OriginalImage = ImageIO.read(f);
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
            		//calc contours
            		OpenCVHandler.FindContours(f);
            		Main.debugDrawOriginalDrawing();
            	}
            }
        });
		
		//Generate Live View Stuff
		JButton liveViewButton = new JButton("Live Simulations (High Intensity Data May Clip)");
		liveViewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JnaFileChooser fc = new JnaFileChooser();
            	fc.addFilter("Images", "png", "tif", "tiff", "bmp", "jpg", "jpeg", "gif");
            	if (fc.showOpenDialog((Window) Main.frame)) {
            		File f = fc.getSelectedFile();
            		if (!Main.updateInputVars())
            			return;
            		//file -> image
            		try {
            			Main.OriginalImage = ImageIO.read(f);
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
            		//calc contours
            		OpenCVHandler.FindContours(f);
            		Main.debugDrawOriginalDrawing();
            	    Main.debugDrawAnimation();
            	    Main.debugDrawFinishedTrace();
            	    Main.debugDrawTrail(true);
            	}
            }
        });
		
		//Generate Download Circle Stuff
		JButton mp4CircleButton = new JButton("Circle MP4 Download");
		mp4CircleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JnaFileChooser fc = new JnaFileChooser();
            	fc.addFilter("Images", "png", "tif", "tiff", "bmp", "jpg", "jpeg", "gif");
            	if (fc.showOpenDialog((Window) Main.frame)) {
            		File f = fc.getSelectedFile();
            		if (!Main.updateInputVars())
            			return;
            		//file -> image
            		try {
            			Main.OriginalImage = ImageIO.read(f);
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
            		//calc contours
            		OpenCVHandler.FindContours(f);
            		//Decide name
            		String filename = f.getName();
                    int lastDotIndex = filename.lastIndexOf('.');
                    if (lastDotIndex > 0)
                    	filename = filename.substring(0, lastDotIndex);
                    filename = filename + "_" + Main.circleCount + " Circles_" + ((int) (1d/Main.TIME_DELTA)) + "FPS_C";
            		Main.downloadMedia(true, false, filename);
            	}
            }
        });
		
		//Generate Download Pixel Stuff
		JButton mp4PixelButton = new JButton("Pixel MP4 Download");
		mp4PixelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JnaFileChooser fc = new JnaFileChooser();
            	fc.addFilter("Images", "png", "tif", "tiff", "bmp", "jpg", "jpeg", "gif");
            	if (fc.showOpenDialog((Window) Main.frame)) {
            		File f = fc.getSelectedFile();
            		if (!Main.updateInputVars())
            			return;
            		//file -> image
            		try {
            			Main.OriginalImage = ImageIO.read(f);
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
            		//calc contours
            		OpenCVHandler.FindContours(f);
            		//Decide name
            		String filename = f.getName();
                    int lastDotIndex = filename.lastIndexOf('.');
                    if (lastDotIndex > 0)
                    	filename = filename.substring(0, lastDotIndex);
                    filename = filename + "_" + Main.circleCount + " Circles_" + ((int) (1d/Main.TIME_DELTA)) + "FPS_P";
            		Main.downloadMedia(false, true, filename);
            	}
            }
        });
		
		//Generate Download Combined Stuff
		JButton mp4CombinedButton = new JButton("Circle + Pixel MP4 Download");
		mp4CombinedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JnaFileChooser fc = new JnaFileChooser();
            	fc.addFilter("Images", "png", "tif", "tiff", "bmp", "jpg", "jpeg", "gif");
            	if (fc.showOpenDialog((Window) Main.frame)) {
            		File f = fc.getSelectedFile();
            		if (!Main.updateInputVars())
            			return;
            		//file -> image
            		try {
            			Main.OriginalImage = ImageIO.read(f);
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
            		//calc contours
            		OpenCVHandler.FindContours(f);
            		//Decide name
            		String filename = f.getName();
                    int lastDotIndex = filename.lastIndexOf('.');
                    if (lastDotIndex > 0)
                    	filename = filename.substring(0, lastDotIndex);
                    filename = filename + "_" + Main.circleCount + " Circles_" + ((int) (1d/Main.TIME_DELTA)) + "FPS_CP";
            		Main.downloadMedia(true, true, filename);
            	}
            }
        });
		
		//Generate Overlap Stuff
		JButton pngButton = new JButton("Overlap PNG Download");
		pngButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JnaFileChooser fc = new JnaFileChooser();
            	fc.addFilter("Images", "png", "tif", "tiff", "bmp", "jpg", "jpeg", "gif");
            	if (fc.showOpenDialog((Window) Main.frame)) {
            		File f = fc.getSelectedFile();
            		if (!Main.updateInputVars())
            			return;
            		//file -> image
            		try {
            			Main.OriginalImage = ImageIO.read(f);
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
            		//calc contours
            		OpenCVHandler.FindContours(f);
            		//Decide name
            		String filename = f.getName();
                    int lastDotIndex = filename.lastIndexOf('.');
                    if (lastDotIndex > 0)
                    	filename = filename.substring(0, lastDotIndex);
                    filename = filename + "_" + Main.circleCount + " Circles_Overlap";
            		Main.downloadOverlap(filename);
            	}
            }
        });
		
		add(resolutionPanel);
		add(contourCountPanel);
		add(durationPanel);
		add(circleCountPanel);
		add(timeStepPanel);
		add(lifetimePanel);
		add(overlapVisibilityPanel);
		JPanel lineBreakPanel = new JPanel();
		lineBreakPanel.setPreferredSize(new Dimension(3000, 0)); // Adjust the height as needed
        add(lineBreakPanel);
        add(previewContourButton);
		add(liveViewButton);
		add(mp4CircleButton);
		add(mp4PixelButton);
		add(mp4CombinedButton);
		add(pngButton);
	}
	
}
