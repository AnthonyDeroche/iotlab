/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.algorithm;

import iotlab.module.geolocation.algorithm.Trilateration.Circle;
import iotlab.module.geolocation.algorithm.Trilateration.Intersection;
import iotlab.module.geolocation.algorithm.Trilateration.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Anthony Deroche
 *
 */
public class ErrorCorrectionAlgorithm {

	private List<Circle> circles;
	private List<Intersection> intersections;
	private List<Point> intersectionPoints;
	private double[] weights;
	private double maxWeight;
	private double xg, yg;
	private Trilateration trilateration;

	public ErrorCorrectionAlgorithm(Trilateration trilateration,
			List<Circle> circles, List<Intersection> intersections) {
		this.trilateration = trilateration;
		this.circles = circles;
		this.intersections = intersections;
		this.init();
	}

	private void init() {
		intersectionPoints = new ArrayList<>();

		for (int i = 0; i < intersections.size(); i++) {
			for (int j = 0; j < intersections.get(i).getPoints().length; j++)
				intersectionPoints.add(intersections.get(i).getPoints()[j]);
		}

		// calculate weights for each intersection
		weights = new double[intersectionPoints.size()];

		for (int i = 0; i < intersectionPoints.size(); i++) {
			weights[i] = 0;
			double d;
			for (int c = 0; c < 3; c++) {
				d = Math.sqrt((circles.get(c).getCenter().getX() - intersectionPoints
						.get(i).getX())
						* (circles.get(c).getCenter().getX() - intersectionPoints
								.get(i).getX())
						+ (circles.get(c).getCenter().getY() - intersectionPoints
								.get(i).getY())
						* (circles.get(c).getCenter().getY() - intersectionPoints
								.get(i).getY()));

				if (d <= circles.get(c).getRadius() + 1) {
					weights[i]++; // +(-1/circles.get(c).rssi);
				}
			}
		}

		maxWeight = 0;
		for (int i = 0; i < weights.length; i++)
			if (weights[i] > maxWeight)
				maxWeight = weights[i];
	}

	public void doCorrection(){
		this.doCorrection(0);
	}
	public void doCorrection(int depth) {
		//System.out.println("Trilateration " + intersectionPoints.size() + " "+ maxWeight);
		if (maxWeight == 3) { // intersection
								// between the 3
								// circles, take the
								// middle of the
								// area (weighted
								// points)
			// estimate x,y by taking into account points where weight=maxWeight
			double sumX = 0, sumY = 0, sumWeights = 0;
			for (int i = 0; i < weights.length; i++) {
				if (weights[i] >= (maxWeight)) {
					sumX += intersectionPoints.get(i).getX() * weights[i];
					sumY += intersectionPoints.get(i).getY() * weights[i];
					sumWeights += weights[i];
				}
			}

			if (sumWeights > 0) {
				xg = sumX / sumWeights;
				yg = sumY / sumWeights;
			}

		} else if (intersectionPoints.size() == 6 && maxWeight == 2) {

			double d, sum;
			Point[] nearest3 = new Point[3];

			double min = Double.MAX_VALUE;
			for (int i = 0; i < intersections.size(); i++) {
				Point[] nearest = new Point[2];
				double[] leastD = new double[2];
				leastD[0] = Double.MAX_VALUE;
				leastD[1] = Double.MAX_VALUE;
				for (int j = 0; j < intersections.size(); j++) {
					if (i != j) {
						d = intersectionPoints.get(i).distance(
								intersectionPoints.get(j));

						if (d < leastD[1]) {
							leastD[1] = d;
							nearest[1] = intersectionPoints.get(j);
						}

						if (leastD[1] < leastD[0]) { // swap 1st and 2nd to sort
														// them
							double temp = leastD[1];
							Point tempp = nearest[1];
							leastD[1] = leastD[0];
							leastD[0] = temp;
							nearest[1] = nearest[0];
							nearest[0] = tempp;
						}
					}
				}

				sum = nearest[0].distance(intersectionPoints.get(i))
						+ nearest[0].distance(intersectionPoints.get(i));
				if (sum < min) {
					min = sum;
					nearest3[0] = intersectionPoints.get(i);
					nearest3[1] = nearest[0];
					nearest3[2] = nearest[1];
				}
			}
			xg = (nearest3[0].getX() + nearest3[1].getX() + nearest3[2].getX()) / 3;
			yg = (nearest3[0].getY() + nearest3[1].getY() + nearest3[2].getY()) / 3;

		} else if (intersectionPoints.size() == 4 && maxWeight == 2) {
			// number of combinations = n(n-1)/2
			double d;
			Point[] nearest2 = new Point[2];

			double min = Double.MAX_VALUE;
			
			
			for (int i = 0; i < intersectionPoints.size(); i++) {
				Point nearest = null;
				double leastD = Double.MAX_VALUE;
				
				for (int j = 0; j < intersectionPoints.size(); j++) {
					if (i != j) {
						d = intersectionPoints.get(i).distance(intersectionPoints.get(j));
						boolean same = sameCircles(intersectionPoints.get(i).getInter(),intersectionPoints.get(j).getInter());
						if (!same && d < leastD) {
							leastD = d;
							nearest = intersectionPoints.get(j);
						}
					}
				}

				if (leastD < min) {
					min = leastD;
					nearest2[0] = intersectionPoints.get(i);
					nearest2[1] = nearest;
				}
			}
			xg = (nearest2[0].getX() + nearest2[1].getX()) / 2;
			yg = (nearest2[0].getY() + nearest2[1].getY()) / 2;
		} else {
			if(intersectionPoints.size() == 2){
				int index = getIndexOfCircleWithNoIntersectWithOthers();
				
				if(index>-1){
					double coeff=1.25;
					Intersection i1,i2;
					int surrounded=0;
					for(int i=0;i<3;i++)
					if(circles.get(i)!=circles.get(index) && circles.get(index).getRadius()>circles.get(i).getRadius() && circles.get(index).getCenter().distance(circles.get(i).getCenter())<Math.abs(circles.get(index).getRadius()-circles.get(i).getRadius()))
						surrounded++;
					
					if(surrounded>0)
						coeff=0.75;
						
					int k=0;
					do {
						circles.get(index).setRadius(circles.get(index).getRadius()*coeff);
						i1 = trilateration.intersect(circles.get(index), circles.get((index+2)%3));
						i2 = trilateration.intersect(circles.get(index), circles.get((index+1)%3));
					} while (!(i1.isIntersect() || i2.isIntersect()) && (k++)<10 && circles.get(index).getRadius()>0);
					
					if(k>9)
						System.out.println("[WARNING] Loop ended because of index");
				
					if(i1.isIntersect() || i2.isIntersect()){
						for(int i=0;i<intersections.size();i++){
							if(sameCircles(intersections.get(i),i1))
								intersections.set(i, i1);
							else if(sameCircles(intersections.get(i),i2))
								intersections.set(i, i2);
						}
						
						if(depth<10){
							 this.init();
							 this.doCorrection(depth+1);
						}
					}
				}
			}
			else if (intersectionPoints.size() == 0) {
				double distance, radiuses;
				Intersection i1,i2;
				int k=0;
				do {
					distance = circles.get(0).getCenter()
							.distance(circles.get(1).getCenter());
					radiuses = circles.get(0).getRadius()
							+ circles.get(1).getRadius();
					if (radiuses < distance) {
						circles.get(0).setRadius(circles.get(0).getRadius()*1.25);
						circles.get(1).setRadius(circles.get(1).getRadius()*1.25);
					} else {
						/*if(circles.get(0).getRadius()>circles.get(1).getRadius())
							circles.get(0).setRadius(circles.get(0).getRadius()*0.75);
						else
							circles.get(1).setRadius(circles.get(1).getRadius()*0.75);*/
					}

					distance = circles.get(0).getCenter().distance(circles.get(2).getCenter());
					radiuses = circles.get(0).getRadius() + circles.get(2).getRadius();
					if (radiuses < distance) {
						circles.get(2).setRadius(circles.get(1).getRadius()*1.25);
					} else {
						/*if(circles.get(0).getRadius()<circles.get(2).getRadius())
							circles.get(2).setRadius(circles.get(1).getRadius()*0.75);*/
					}
					
					i1 = trilateration.intersect(circles.get(0),circles.get(1));
					i2 = trilateration.intersect(circles.get(0),circles.get(2));
					
					
				} while (!(i1.isIntersect() || i2.isIntersect()) && (k++)<10);
				
				if(k>9)
					System.out.println("[WARNING] Loop ended because of index");
				
				for(int i=0;i<intersections.size();i++){
					if(sameCircles(intersections.get(i),i1))
						intersections.set(i, i1);
					else if(sameCircles(intersections.get(i),i2))
						intersections.set(i, i2);
				}
				
				 this.init();
				 this.doCorrection();
			}

			

		}
	}

	private boolean sameCircles(Intersection i1, Intersection i2) {
		return i1.getCircles()[0]==i2.getCircles()[0] && i1.getCircles()[1]==i2.getCircles()[1];
	}

	private int getIndexOfCircleWithNoIntersectWithOthers() {
		
		HashMap<Circle,Boolean> hasInter = new HashMap<>();
		for (int i = 0; i < intersections.size(); i++) {
			if(intersections.get(i).isIntersect())
				hasInter.put(intersections.get(i).getCircles()[0], true);
		}
		
		int index = -1;
		for(int i=0;i<3;i++){
			if(hasInter.get(circles.get(i))==null){
				index=i; break;
			}
		}
		return index;
	}

	public double getX() {
		return xg;
	}

	public double getY() {
		return yg;
	}

}
