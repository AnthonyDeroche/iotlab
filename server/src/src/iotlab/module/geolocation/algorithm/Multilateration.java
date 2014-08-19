/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.algorithm;

import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.module.geolocation.Anchor;
import iotlab.module.geolocation.GeolocationData;
import iotlab.module.geolocation.calibration.Calibration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.json.JsonObject;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.util.Pair;

/**
 * 
 * @author Anthony Deroche
 *
 */
public class Multilateration extends GeolocationAlgorithm implements
		MultivariateJacobianFunction {

	private final double[] initialSolution = { 0, 0 };
	private final double relativeThreshold = 10e-6, absoluteThreshold = 10e-10;
	private final ConvergenceChecker<Evaluation> checker = LeastSquaresFactory
			.evaluationChecker(new SimpleVectorValueChecker(relativeThreshold,
					absoluteThreshold));

	private LevenbergMarquardtOptimizer optimizer;

	private double xg = 0, yg = 0;

	private List<Double> x;
	private List<Double> y;
	private List<Double> d;
	private List<Double> weights;
	private HashMap<Integer,Boolean> anchorsMap;

	public Multilateration(Calibration calibration, List<GeolocationData> data, List<Anchor> anchors) {
		super(calibration, data);
		x = new ArrayList<Double>();
		y = new ArrayList<Double>();
		d = new ArrayList<Double>();
		weights = new ArrayList<Double>();
		anchorsMap = new HashMap<>();
		for(Anchor a : anchors){
			anchorsMap.put(a.getId(),true);
		}
		optimizer = new LevenbergMarquardtOptimizer();
	}

	public void addMeasure(double d, double x, double y) {
		this.x.add(x);
		this.y.add(y);
		this.d.add(d);
	}

	public double[] calculateTarget() {
		double[] target = new double[d.size()];
		for (int i = 0; i < d.size(); i++) {
			target[i] = d.get(i).doubleValue() * d.get(i).doubleValue();
		}
		return target;
	}

	private double[][] jacobian(double[] variables) {
		double[][] jacobian = new double[x.size()][2];
		for (int i = 0; i < jacobian.length; ++i) {
			jacobian[i][0] = 2 * (variables[0] - x.get(i));
			jacobian[i][1] = 2 * (variables[1] - y.get(i));
		}
		return jacobian;
	}

	@Override
	public Pair<RealVector, RealMatrix> value(RealVector vector) {
		double[] variables = vector.toArray();
		double[] values = new double[x.size()];
		for (int i = 0; i < values.length; ++i) {
			values[i] = variables[0] * variables[0] + variables[1]
					* variables[1] - 2 * variables[0] * x.get(i) - 2
					* variables[1] * y.get(i) + x.get(i) * x.get(i) + y.get(i)
					* y.get(i);
		}
		
		return new Pair<RealVector, RealMatrix>(new ArrayRealVector(values),
				new Array2DRowRealMatrix(jacobian(variables)));
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return super.buildEncode().build();
	}

	@Override
	public void execute(Sender s) {
		this.sender = s;
		int mote_id=s.getId();
		xg = 0;
		yg = 0;
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		List<Double> d = new ArrayList<Double>();
		this.x = new ArrayList<Double>();
		this.y = new ArrayList<Double>();
		this.d = new ArrayList<Double>();
		this.weights = new ArrayList<Double>();
		
		if(data.size()>0){
			boolean found;
			
			boolean swap;
			do{ //sort circles
				swap=false;
				for(int i=0;i<data.size()-1;i++){
					if(data.get(i).getRssi()<data.get(i+1).getRssi()){
						GeolocationData temp = data.get(i);
						data.set(i, data.get(i+1));
						data.set(i+1, temp);
						swap=true;
					}
				}
			}
			while(swap);
			
			for (int i = 0; i < data.size(); i++) {
				found = false;
				Mote peer = null;
				if (data.get(i).getSrc().getId() == mote_id && anchorsMap.containsKey(data.get(i).getDest().getId())) {
					peer = data.get(i).getDest();
					found = true;
				}else if (data.get(i).getDest().getId() == mote_id && anchorsMap.containsKey(data.get(i).getSrc().getId())) {
					peer = data.get(i).getSrc();
					found = true;
				}
	
				if (found && peer != null) {
					double de = calibration.estimateDistance(data.get(i).getRssi(),0);
						if(de>0){
							x.add(peer.getLon());
							y.add(peer.getLat());
							d.add(de);
						}
						//System.out.println("rssi="+data.get(i).getRssi()+" d="+de+ "("+peer.getLon()+","+peer.getLat()+")");
				}
			}
			
			List<Double> sorted = new ArrayList<Double>(d);
			Collections.sort(sorted);
			
			int nb = sorted.size()>=6 ? sorted.size()/2 : sorted.size();
			//int nb = sorted.size();
			if(sorted.size()>=nb){
				
				for(int i=0;i<d.size();i++){
					if(d.get(i)<=sorted.get(nb-1)){
						this.x.add(x.get(i));
						this.y.add(y.get(i));
						this.d.add(d.get(i));
						this.weights.add(1/(d.get(i)*d.get(i)));
					}
				}
			 	
				try{
					Optimum optimum = optimizer.optimize(LeastSquaresFactory.weightDiagonal(LeastSquaresFactory.create(this,
						new ArrayRealVector(this.calculateTarget()),
						new ArrayRealVector(initialSolution), checker,
						Integer.MAX_VALUE, Integer.MAX_VALUE),new ArrayRealVector(weights.toArray(new Double[0]))));
					final double[] optimalValues = optimum.getPoint().toArray();
					
					xg = optimalValues[0];
					yg = optimalValues[1];
				}catch(NoDataException e){
					System.out.println("[Multilateration] No Data Exception");
				}
			}
		}else{
			System.out.println("[Multilateration] No data");
		}
	}

	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return xg;
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return yg;
	}

	@Override
	public String getAlgorithmName() {
		// TODO Auto-generated method stub
		return "multilateration";
	}

	public List<Double> getD() {
		return d;
	}

}
