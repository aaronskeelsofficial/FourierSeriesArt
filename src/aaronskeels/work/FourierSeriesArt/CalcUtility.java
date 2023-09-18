package aaronskeels.work.FourierSeriesArt;

import java.util.List;

/*
 * This class is where I experimented trying to understand Fourier Series in general and the associated integral
 * mechanisms.
 */

public class CalcUtility {

	public static double[] calculateIntegral(int n, int contourIndex) {
		int numberOfPointsInContour = Main.originalDrawings.get(contourIndex).size();
		double sum_r = 0;
		double sum_z = 0;
		double individualWeight = 1d / numberOfPointsInContour;
//VERSION 1: Not sure why I have a double loop
//		for (DecayingPixel dp : Main.originalDrawing) {
//			double a1 = dp.x;
//			double b1 = dp.y;
//			for (double percentOfPath = 0; percentOfPath < 1; percentOfPath += 1d/Main.originalDrawing.size()) {
//				double t = percentOfPath;
//				double A = 1;
//				double theta = -2 * Math.PI * n * t;
//				double a2 = A*Math.cos(theta);
//				double b2 = A*Math.sin(theta);
//				sum_r += (a1*a2 - b1*b2);
//				sum_z += (a1*b2 + a2*b1);
//			}
//		}
//VERSION 2: This just gives 0 for all n != 0. Broken.
//		for (int iterationNum = 0;iterationNum < Main.originalDrawing.size();iterationNum++) {
//			//Get a1 & b1
//			DecayingPixel dp = Main.originalDrawing.get(iterationNum);
//			double a1 = dp.x;
//			double b1 = dp.y;
//			//Get a2 & b2
//			double t = (double) (iterationNum) / (double) (Main.originalDrawing.size());
//			double A = 1;
//			double theta = -2 * Math.PI * n * t;
//			//Calculations (pull common multiples out of for loop for efficiency)
//			double[] result = Main.complexMultiplyCartPolar(a1, b1, A, theta);
//			sum_r += result[0];
//			sum_z += result[1];
//		}
//VERSION 3
		List<DecayingPixel> dps = Main.originalDrawings.get(contourIndex);
		for (int iterationNum = 0;iterationNum < numberOfPointsInContour;iterationNum++) {
			double t = (double) (iterationNum) / (double) (numberOfPointsInContour);
			//Get modified f function
			DecayingPixel dp = dps.get(iterationNum);
			double a1 = dp.x;
			double b1 = dp.y;
			double A_mod = 1;
			double theta_mod = -2 * Math.PI * n * t;
			double[] modifiedFValues = Main.complexMultiplyCartPolar(a1, b1, A_mod, theta_mod);
			sum_r += modifiedFValues[0];
			sum_z += modifiedFValues[1];
		}
		sum_r *= individualWeight;
		sum_z *= individualWeight;
		return new double[] {sum_r, sum_z};
	}
	
}
