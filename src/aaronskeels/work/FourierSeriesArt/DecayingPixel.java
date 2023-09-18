package aaronskeels.work.FourierSeriesArt;

/*
 * This class is used to represent a single "pixel" drawn by the circle trail. They include "decaying" features because
 * I wanted a fading tail effect. The trick was to have the pixels be permanently "dead" before some timestamp, and then
 * put on a cyclical opacity fade at all times after.
 */

public class DecayingPixel {
	private long birthTime = 0;
	private double lifeExpectancy;
	public double x, y;
	public double xPixels, yPixels;
	
	public DecayingPixel(double x, double y, double lifeExpectancy) {
		this.x = x;
		this.y = y;
		this.xPixels = Main.unitCircleToPixel(this.x);
		this.yPixels = Main.unitCircleToPixel(this.y);
		this.lifeExpectancy = lifeExpectancy;
	}
	
	public void birth(long timeMillis) {
		birthTime = timeMillis;
	}
	
	public double getOpacityDecimal() {
		//This function is used to get the opacity fade in accordance to real life timings (in live animations)
		long curTime = System.currentTimeMillis();
		double effectiveTime = (curTime-birthTime) % (Main.duration*1000d);
		if (effectiveTime < 0)
			effectiveTime = lifeExpectancy;
		double opacity = Math.max(1 - effectiveTime/lifeExpectancy, 0);
//		System.out.println(curTime + " : " + birthTime + " : " + effectiveTime + " : " + lifeExpectancy + " : " + (effectiveTime/lifeExpectancy) + " : " + opacity);
		return opacity;
	}
	
	public double getOpacityDecimal(long curTime) {
		//This function is used to get the opacity fade in simulated timings (for pre-rendering the animation, for example)
		double effectiveTime = (curTime-birthTime) % (Main.duration*1000d);
		if (effectiveTime < 0)
			effectiveTime = lifeExpectancy;
		double opacity = Math.max(1 - effectiveTime/lifeExpectancy, 0);
		return opacity;
	}
}
