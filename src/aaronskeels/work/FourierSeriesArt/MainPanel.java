package aaronskeels.work.FourierSeriesArt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel{
	
	public MainPanel() {
		setBackground(Color.green);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Main.OffscreenImage == null)
        	return;
        Graphics2D g2d = (Graphics2D) g;
        
        int width = getWidth();
        int height = getHeight();
        
        double scale = Math.min((double) width / Main.OffscreenImage.getWidth(), (double) height / Main.OffscreenImage.getHeight());
        int scaledCanvasWidth = (int) (Main.OffscreenImage.getWidth() * scale);
        int scaledCanvasHeight = (int) (Main.OffscreenImage.getHeight() * scale);
        g2d.drawImage(Main.OffscreenImage, (int) ((width - scaledCanvasWidth) / 2d), (int) ((height - scaledCanvasHeight) / 2d), scaledCanvasWidth, scaledCanvasHeight, null);
    }
	
}
