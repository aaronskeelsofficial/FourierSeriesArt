package aaronskeels.work.FourierSeriesArt;

import java.awt.Color;
import java.awt.Graphics2D;

/*
 * This class represents a single 'n' circle of the Fourier Series. It handles finding the coefficients,
 * drawing the circles, and calculating where the endpoint trace of the chain of circles would be.
 */

public class NTierCircle {
	public double originX, originY;
	public int n;
	public NTierCircle childCircle;
	public double coefficient_r, coefficient_z, A;
	private Color color;
	private double innerClockTAccumulated = 0d;
	
	public NTierCircle(int n, int contourIndex, Color color) {
		this(n, contourIndex, color, null);
	}
	public NTierCircle(int n, int contourIndex, Color color, NTierCircle childCircle) {
		this.n = n;
		this.color = color;
		this.childCircle = childCircle;
		double[] coefficient = CalcUtility.calculateIntegral(n, contourIndex);
		coefficient_r = coefficient[0];
		coefficient_z = coefficient[1];
		A = Math.sqrt(Math.pow(coefficient_r, 2) + Math.pow(coefficient_z, 2));
//		System.out.println("Contour " + contourIndex + " Circle " + n + ": [" + coefficient_r + ", " + coefficient_z + "] : " + A);
		/* Instability Fix
		 *  This fix is kinda a ghetto, low effort hack.
		 *  It's imperfect, but I'd assume any iteration past 100 should not have influence greater than arbitrary tol
		 *  I'm getting wierd instability anywhere from n=~260 -> ~500 depending on the contour line
		 *  	where influence is CRANKED to like .2 which feels very incorrect.
		 *  Also I have to do SOMETHING because whatever is causing the instability just factually is
		 *  	completely degenerating the output worse and worse with more iterations bc they are faulty.
		 *  Update: A single band pass of tolerance checks wasn't cutting it. Now doing kinda multi-band checking.
		 */
		if (Math.abs(n) > 100) {
			boolean edited = false;
			double tol = .0001d;
			if (Math.abs(coefficient_r) > tol) {
				coefficient_r = 0d;
				edited = true;
			}
			if (Math.abs(coefficient_z) > tol) {
				coefficient_z = 0d;
				edited = true;
			}
			if (edited)
				A = Math.sqrt(Math.pow(coefficient_r, 2) + Math.pow(coefficient_z, 2));
		} else if (Math.abs(n) > 50) {
			boolean edited = false;
			double tol = .001d;
			if (Math.abs(coefficient_r) > tol) {
				coefficient_r = 0d;
				edited = true;
			}
			if (Math.abs(coefficient_z) > tol) {
				coefficient_z = 0d;
				edited = true;
			}
			if (edited)
				A = Math.sqrt(Math.pow(coefficient_r, 2) + Math.pow(coefficient_z, 2));
		} else if (Math.abs(n) > 40) {
			boolean edited = false;
			double tol = .01d;
			if (Math.abs(coefficient_r) > tol) {
				coefficient_r = 0d;
				edited = true;
			}
			if (Math.abs(coefficient_z) > tol) {
				coefficient_z = 0d;
				edited = true;
			}
			if (edited)
				A = Math.sqrt(Math.pow(coefficient_r, 2) + Math.pow(coefficient_z, 2));
		}
//		System.out.println("Contour " + contourIndex + " Circle " + n + ": [" + coefficient_r + ", " + coefficient_z + "] : " + A);
	}
	
	public void draw(double originX, double originY, Graphics2D g2d) {
		g2d.setColor(color);
		double amplitudeDiameter = (2 * A) * Main.UNIT_CIRCLE_RADIUS_PIXELS;
		g2d.drawOval((int) (originX-amplitudeDiameter/2d), (int) (originY-amplitudeDiameter/2d), (int) amplitudeDiameter, (int) amplitudeDiameter);
		double curX = originX + (getXAtT(innerClockTAccumulated % Main.duration) * Main.UNIT_CIRCLE_RADIUS_PIXELS);
		double curY = originY + (getYAtT(innerClockTAccumulated % Main.duration) * Main.UNIT_CIRCLE_RADIUS_PIXELS);
		double diameter = 5;
		g2d.fillOval((int) (curX-diameter/2d), (int) (curY-diameter/2d), (int) diameter, (int) diameter);
		if (childCircle != null)
			childCircle.draw(curX, curY, g2d, innerClockTAccumulated);
		innerClockTAccumulated += Main.TIME_DELTA / Main.duration;
	}
	public void draw(double originX, double originY, Graphics2D g2d, double t) {
		g2d.setColor(color);
		double amplitudeDiameter = (2 * A) * Main.UNIT_CIRCLE_RADIUS_PIXELS;
		g2d.drawOval((int) (originX-amplitudeDiameter/2d), (int) (originY-amplitudeDiameter/2d), (int) amplitudeDiameter, (int) amplitudeDiameter);
		double curX = originX + (getXAtT(t) * Main.UNIT_CIRCLE_RADIUS_PIXELS);
		double curY = originY + (getYAtT(t) * Main.UNIT_CIRCLE_RADIUS_PIXELS);
		if (childCircle == null)
			g2d.setColor(Color.red);
		double diameter = childCircle == null ? 10 : 5;
		g2d.fillOval((int) (curX-diameter/2d), (int) (curY-diameter/2d), (int) diameter, (int) diameter);
		if (childCircle != null)
			childCircle.draw(curX, curY, g2d, t);
	}
	
	public double getTotalOffsetXOfChain(double t) {
		if (childCircle == null)
			return getXAtT(t);
		else
			return getXAtT(t) + childCircle.getTotalOffsetXOfChain(t);
	}
	public double getTotalOffsetYOfChain(double t) {
		if (childCircle == null)
			return getYAtT(t);
		else
			return getYAtT(t) + childCircle.getTotalOffsetYOfChain(t);
	}
	
	public double getXAtT(double t) {
		double a1 = coefficient_r;
		double b1 = coefficient_z;
		double A = 1;
		double theta = 2 * Math.PI * n * t;
		double[] result = Main.complexMultiplyCartPolar(a1, b1, A, theta);
		return result[0];
	}
	public double getYAtT(double t) {
		double a1 = coefficient_r;
		double b1 = coefficient_z;
		double A = 1;
		double theta = 2 * Math.PI * n * t;
		double[] result = Main.complexMultiplyCartPolar(a1, b1, A, theta);
		return result[1];
	}
	
	public void setTimeAccumulated(long time) {
		innerClockTAccumulated = time;
	}
	
}
