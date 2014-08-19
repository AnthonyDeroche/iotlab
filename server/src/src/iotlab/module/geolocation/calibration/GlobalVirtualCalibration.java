/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.calibration;

import java.util.List;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * 
 * @author Anthony Deroche
 * 
 */
@Stateless
public class GlobalVirtualCalibration implements Calibration{
	/*
	 * General model : L(d) = l0 + 10α log(d) + WAF + FAF we assume one type of
	 * wall This leads to : L(d) db = l0 + 10α log(d) + k lw (k number of wall,
	 * lw attenuation introduced by a wall)
	 */

	private static final double l0 = -20;
	private double alpha=0, b=0, lw=0;

	private OLSMultipleLinearRegression regression;
	
	//private double[] residuals;

	public GlobalVirtualCalibration() {

	}

	public String toString() {
		return "L(d) = " + l0 + " + 10*" + alpha + "*log(d) + k*" + lw;
	}

	public void calibrate(List<CalibrationData> calibrationData) {
		regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		
		if (calibrationData.size() > 2) {
			//System.out.println("Data sample size = " + calibrationData.size());
			double[][] xSample = new double[calibrationData.size()][1];
			double[] ySample = new double[calibrationData.size()];

			int i = 0;
			for (CalibrationData cd : calibrationData) {
				xSample[i] = new double[] { 10 * Math.log10(cd.getMeasure()
						.getDistance())/*, cd.getMeasure().getWallNumber() */};
				ySample[i] = cd.getRssi() - l0;
				i++;
				/* System.out.println(cd.getMeasure().getDistance() + "/" +
				  cd.getRssi());*/
			}

			regression.newSampleData(ySample, xSample);
			double[] parameters = regression.estimateRegressionParameters();
			//residuals = regression.estimateResiduals();


			if(!Double.isNaN(parameters[0]))
				alpha = parameters[0];
			//lw = parameters[1];

		}else{
			System.out.println("Calibration : not enough data");
		}
	}

	public double getL0() {
		return l0;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getLw() {
		return lw;
	}

	public double getB() {
		return b;
	}

	public JsonObject encode() {
		return Json
				.createObjectBuilder()
				.add("a", alpha)
				.add("lw", lw).build();
	}

	
	public double estimateDistance(double rssi, int wallNb) {
		double d = Math.pow(10, (rssi-l0/*-wallNb*lw*/)/(10*alpha));
		return 0.75*d;
	}
	
	@Override
	public double estimateDistance(double rssi) {
		return estimateDistance(rssi,0);
	}
}
