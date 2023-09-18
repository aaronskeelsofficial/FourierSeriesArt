package aaronskeels.work.FourierSeriesArt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/*
 * This class was my test to get circles which are drawn.
 */

@SuppressWarnings("serial")
public class TestCirclePanel extends JPanel{
	public BufferedImage bi;
	
	public TestCirclePanel() {
		setBackground(Color.green);
		bi = new BufferedImage((int) Main.UNIT_CIRCLE_RADIUS_PIXELS*2, (int) Main.UNIT_CIRCLE_RADIUS_PIXELS*2, BufferedImage.TYPE_INT_RGB);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int width = getWidth();
        int height = getHeight();
        
        g2d.drawImage(bi, (int) (width/2d-bi.getWidth()/2d), (int) (height/2d-bi.getHeight()/2d), bi.getWidth(), bi.getHeight(), null);
    }
	
}
