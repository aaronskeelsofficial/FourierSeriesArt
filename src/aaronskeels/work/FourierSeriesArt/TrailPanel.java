package aaronskeels.work.FourierSeriesArt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/*
 * This class was my test to get fading trails which are drawn.
 */

@SuppressWarnings("serial")
public class TrailPanel extends JPanel{
	public BufferedImage bi;
	public List<DecayingPixel> pixels;
	public List<List<NTierCircle>> circlesDB;
	
	public TrailPanel() {
		setBackground(Color.green);
		bi = new BufferedImage((int) Main.UNIT_CIRCLE_RADIUS_PIXELS*2, (int) Main.UNIT_CIRCLE_RADIUS_PIXELS*2, BufferedImage.TYPE_INT_ARGB);
		pixels = new ArrayList<>();
		circlesDB = new ArrayList<>();
	}
	
	public void redrawCircles(Graphics2D g2d) {
		for (int i = 0;i < circlesDB.size();i++) {
			List<NTierCircle> circles = circlesDB.get(i);
			circles.get(circles.size()-1).draw(Main.UNIT_CIRCLE_RADIUS_PIXELS, Main.UNIT_CIRCLE_RADIUS_PIXELS, g2d);
		}
	}
	
	public void redrawPixels(Graphics2D g2d) {
		for (int i = 0;i < pixels.size();i++) {
			DecayingPixel dp = pixels.get(i);
			Color color = new Color(1f, 0f, 0f, (float) dp.getOpacityDecimal());
			g2d.setColor(color);
			g2d.drawRect((int) dp.xPixels, (int) dp.yPixels, 1, 1);
		}
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int width = getWidth();
        int height = getHeight();
        
        g2d.drawImage(bi, (int) (width/2d-bi.getWidth()/2d), (int) (height/2d-bi.getHeight()/2d), bi.getWidth(), bi.getHeight(), null);
    }
	
	public String circleString() {
		String str = "";
		for (List<NTierCircle> circles : circlesDB) {
			str += "---------\n";
			for (NTierCircle c : circles) {
				str += c.n + ": " + (c.childCircle != null ? c.childCircle.n : "") + '\n';
			}
		}
		return str;
	}
}
