/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.algorithm;

import iotlab.core.beans.entity.mote.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Singleton
public class AlgorithmTester {

	public static final int REAL = 0;
	public static final int TRILATERATION = 1;
	public static final int BARYCENTER = 2;
	public static final int MULTILATERATION = 3;
	public static final int COMBINED = 4;
	public static final int COMBINED_CUSTOMIZED = 5;
	private static final int COMBINED_OPTIMIZED = 6;
	private static final int VECT_NB = 7;
	private static int VALUE_NB=200;
	private static final String[] algosName = { "real", "trilateration",
			"barycenter", "multilateration", "combined",
			"combined_customized","combined_optimized" };

	private boolean optiEenabled = false;
	private List<Stack<Double>> errors;
	private List<Stack<Double>> x;
	private List<Stack<Double>> y;
	private List<Stack<Sender>> senders;
	private JsonObjectBuilder builder;

	@PostConstruct
	public void init() {
		builder = Json.createObjectBuilder();
		senders = new ArrayList<>(VECT_NB);
		errors = new ArrayList<>(VECT_NB);
		x = new ArrayList<>(VECT_NB);
		y = new ArrayList<>(VECT_NB);
		for (int i = 0; i < VECT_NB; i++) {
			errors.add(new Stack<Double>());
			x.add(new Stack<Double>());
			y.add(new Stack<Double>());
			senders.add(new Stack<Sender>());
		}
	}

	public void start() {
		optiEenabled = true;
		init();
	}

	public void stop() {
		optiEenabled = false;
	}

	public void addResult(double xg, double yg, Sender s, int index) {
		double e = Math.sqrt((xg - s.getLon()) * (xg - s.getLon())
				+ (yg - s.getLat()) * (yg - s.getLat()));

		senders.get(index).add(s);
		errors.get(index).add(e);
		x.get(index).add(xg);
		y.get(index).add(yg);
		while (errors.get(index).size() > VALUE_NB)
			errors.get(index).remove(0);
		while (x.get(index).size() > VALUE_NB)
			x.get(index).remove(0);
		while (y.get(index).size() > VALUE_NB)
			y.get(index).remove(0);
		while (senders.get(index).size() > VALUE_NB)
			senders.get(index).remove(0);
	}

	public List<Stack<Double>> getErrors() {
		return errors;
	}

	public double getMean(int i) {
		if (errors.get(i).size() == 0)
			return 0;
		double sum = 0;
		for (int j = 0; j < errors.get(i).size(); j++)
			sum += errors.get(i).get(j);
		return sum / errors.get(i).size();
	}

	public void display() {
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(algosName[i] + " : " + this.getMean(i));
		}
	}

	public void optimize(final Integer[] enabled) {

		if (enabled.length == 0)
			return;

		for (int i = 0; i < enabled.length; i++) {
			if (!(x.get(enabled[i]).size() == VALUE_NB && y.get(enabled[i])
					.size() == VALUE_NB))
				return;
		}

		if (!(x.get(REAL).size() == VALUE_NB && x.get(REAL).size() == VALUE_NB))
			return;

		OLSMultipleLinearRegression regression;
		double[][] xSample;
		double[] ySample;
		double[] xparam;
		double[] yparam;

		builder = Json.createObjectBuilder();
		{
			regression = new OLSMultipleLinearRegression();
			regression.setNoIntercept(true);
			xSample = new double[VALUE_NB][3];
			ySample = new double[VALUE_NB];
			for (int i = 0; i < VALUE_NB; i++) {
				xSample[i] = new double[enabled.length];
				for (int k = 0; k < enabled.length; k++)
					xSample[i][k] = x.get(enabled[k]).get(i);
				ySample[i] = x.get(REAL).get(i);
			}

			regression.newSampleData(ySample, xSample);
			xparam = regression.estimateRegressionParameters();

			JsonObjectBuilder xbuilder = Json.createObjectBuilder();
			for (int k = 0; k < enabled.length; k++)
				xbuilder.add(algosName[enabled[k]], xparam[k]);
			builder.add("x", xbuilder.build());
		}

		{
			regression = new OLSMultipleLinearRegression();
			regression.setNoIntercept(true);
			xSample = new double[VALUE_NB][3];
			ySample = new double[VALUE_NB];
			for (int i = 0; i < VALUE_NB; i++) {
				xSample[i] = new double[enabled.length];
				for (int k = 0; k < enabled.length; k++) {
					xSample[i][k] = y.get(enabled[k]).get(i);
				}
				ySample[i] = y.get(REAL).get(i);
			}

			regression.newSampleData(ySample, xSample);
			yparam = regression.estimateRegressionParameters();
			JsonObjectBuilder ybuilder = Json.createObjectBuilder();
			for (int k = 0; k < enabled.length; k++) {
				ybuilder.add(algosName[enabled[k]], yparam[k]);
			}
			builder.add("y", ybuilder.build());
		}

		{
			JsonObjectBuilder abuilder = Json.createObjectBuilder();
			for (int i = 0; i < VALUE_NB; i++) {
				double xsum = 0, ysum = 0;
				for (int k = 0; k < enabled.length; k++) {
					xsum += x.get(enabled[k]).get(i) * xparam[k];
					ysum += y.get(enabled[k]).get(i) * yparam[k];
				}
				abuilder.add(senders.get(COMBINED).get(i).getMac(), Json
						.createObjectBuilder().add("x", xsum).add("y", ysum)
						.build());
				this.addResult(xsum, ysum, senders.get(COMBINED).get(i),
						COMBINED_OPTIMIZED);
			}
			builder.add("combined", abuilder.build());
		}

	}

	public JsonObject toJson(final Integer[] enabled) {
		if(!this.isOptimizerStarted())
			builder = Json.createObjectBuilder();
			
		{
			JsonObjectBuilder ebuilder = Json.createObjectBuilder();
			for (int k = 0; k < enabled.length; k++)
				ebuilder.add(algosName[enabled[k]], getMean(enabled[k]));
			ebuilder.add(algosName[COMBINED], getMean(COMBINED));
			ebuilder.add(algosName[COMBINED_CUSTOMIZED], getMean(COMBINED_CUSTOMIZED));
			if (this.optiEenabled)
				ebuilder.add(algosName[COMBINED_OPTIMIZED],
						getMean(COMBINED_OPTIMIZED));
			builder.add("errors", ebuilder.build());
		}
		builder.add("optimizerStarted", this.optiEenabled);
		builder.add("collectProgress", collectProgress());
		return builder.build();
	}

	public double collectProgress() {
		return (double) (errors.get(REAL).size()) / (double) VALUE_NB;
	}

	public boolean isOptimizerStarted() {
		// TODO Auto-generated method stub
		return optiEenabled;
	}
}
