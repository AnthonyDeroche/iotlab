/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.algorithm;

import iotlab.core.beans.entity.JsonEncodable;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.module.geolocation.Anchor;
import iotlab.module.geolocation.GeolocationData;
import iotlab.module.geolocation.calibration.Calibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * 
 * @author Anthony Deroche
 *
 */
public class Trilateration extends GeolocationAlgorithm {

	private double xg, yg;
	private List<Intersection> intersections;
	private List<Point> intersectionsPoint;
	private List<Circle> circles;
	private HashMap<Integer, Boolean> anchorsMap;
	private List<Double> xgi,ygi;

	public Trilateration(Calibration calibration, List<GeolocationData> data,
			List<Anchor> anchors) {
		super(calibration, data);
		intersections = new ArrayList<>();
		intersectionsPoint = new ArrayList<>();
		circles = new ArrayList<>();
		xg = 0;
		yg = 0;
		anchorsMap = new HashMap<>();
		for (Anchor a : anchors) {
			anchorsMap.put(a.getId(), true);
		}
	}

	@Override
	public void execute(Sender s) {
		this.sender=s;
		int mote_id = s.getId();
		xg = 0;
		yg = 0;
		circles = new ArrayList<>();
	
			// calculate circles' intersections
			Circle c1;
	
			for (int i = 0; i < data.size(); i++) { // combinations of motes
				if (anchorsMap.containsKey(data.get(i).getSrc().getId())
						&& data.get(i).getSrc().getId() != mote_id
						&& data.get(i).getDest().getId() == mote_id) {
					c1 = new Circle(new Point(data.get(i).getSrc().getLon(), data
							.get(i).getSrc().getLat()),
							calibration.estimateDistance(data.get(i).getRssi())
									, data.get(i).getSrc().getMac(), data
									.get(i).getRssi());
					circles.add(c1);
				} else if (anchorsMap.containsKey(data.get(i).getDest().getId())
						&& data.get(i).getSrc().getId() == mote_id
						&& data.get(i).getDest().getId() != mote_id) {
					for (int k = 0; k < circles.size(); k++) {
						if (circles.get(k).mote.equals(data.get(i).getDest()
								.getMac())) {
							circles.get(k).radius = (circles.get(k).radius +
									calibration.estimateDistance(data.get(i)
											.getRssi())) / 2;
							circles.get(k).rssi = (circles.get(k).rssi + data
									.get(i).getRssi()) / 2;
						}
					}
				}
			}
			
		if(circles.size()>=3){
			boolean swap;
			do{ //sort circles
				swap=false;
				for(int i=0;i<circles.size()-1;i++){
					if(circles.get(i).getRadius()>circles.get(i+1).getRadius()){
						Circle temp = circles.get(i);
						circles.set(i, circles.get(i+1));
						circles.set(i+1, temp);
						swap=true;
					}
				}
			}
			while(swap);

			xgi = new ArrayList<>();
			ygi = new ArrayList<>();
			int k = 3;//combination 3 among k=3
			for(int l=0;l<k && l<circles.size();l++){ 
				for(int m=l+1;m<k && m<circles.size();m++){
					for(int n=m+1;n<k && n<circles.size();n++){
						
						List<Circle> temp = new ArrayList<>(3);
						temp.add(circles.get(l));
						temp.add(circles.get(m));
						temp.add(circles.get(n));
						
						intersections = new ArrayList<>();
						intersectionsPoint = new ArrayList<>();
						HashMap<String, Boolean> done = new HashMap<>();
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								if (i != j && !done.containsKey(i + "-" + j)
										&& !done.containsKey(j + "-" + j)) {
									Intersection inter = intersect(temp.get(i),temp.get(j));
				
									done.put(i + "-" + j, true);
									done.put(j + "-" + i, true);
									if (inter != null) { // if the intersection exists
										intersections.add(inter);
										for (int g = 0; g < inter.points.length; g++)
											intersectionsPoint.add(inter.points[g]);
									}
								}
							}
						}
				
				
						ErrorCorrectionAlgorithm correction = new ErrorCorrectionAlgorithm(
								this, temp, intersections);
						correction.doCorrection();
						xgi.add(correction.getX());
						ygi.add(correction.getY());
					}
				}
			}
			
			for(int i=circles.size()-1;i>k-1;i--){
				circles.remove(i);
			}
				
			
			double sumXg=0;
			double sumYg=0;
			for(int i=0;i<xgi.size();i++){
				sumXg+=xgi.get(i);
				sumYg+=ygi.get(i);
			}
			xg = sumXg/xgi.size();
			yg = sumYg/ygi.size();
		}else
			System.out.println("[Trilateration] Number of circles < 3");

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

	public Intersection intersect(Circle c1, Circle c2) {

		// Find the distance between the centers.
		double dx = c1.getCenter().getX() - c2.getCenter().getX();
		double dy = c1.getCenter().getY() - c2.getCenter().getY();
		double dist = Math.sqrt(dx * dx + dy * dy);

		Point intersection1, intersection2;
		Intersection inter;
		// See how many solutions there are.
		if (dist > c1.getRadius() + c2.getRadius()) {
			inter = new Intersection(null,c1,c2, Intersection.NO_INTERSECT);
		} else if (dist < Math.abs(c1.getRadius() - c2.getRadius())) {
			// console.log("No solutions, one circle contains the other.");
			inter = new Intersection(null,c1,c2, Intersection.SURROUNDED);

		} else if ((dist == 0) && (c1.getRadius() == c2.getRadius())) {
			// console.log("No solutions, the circles coincide.");
			inter = new Intersection(null,c1,c2, Intersection.COINCIDENCE);
		} else {
			// Find a and h.
			double a = (c1.getRadius() * c1.getRadius() - c2.getRadius()
					* c2.getRadius() + dist * dist)
					/ (2 * dist);
			double h = Math.sqrt(c1.getRadius() * c1.getRadius() - a * a);

			// Find P2.
			double cx2 = c1.getCenter().getX() + a
					* (c2.getCenter().getX() - c1.getCenter().getX()) / dist;
			double cy2 = c1.getCenter().getY() + a
					* (c2.getCenter().getY() - c1.getCenter().getY()) / dist;

			// Get the points P3.
			intersection1 = new Point(cx2 + h
					* (c2.getCenter().getY() - c1.getCenter().getY()) / dist,
					cy2 - h * (c2.getCenter().getX() - c1.getCenter().getX())
							/ dist);

			intersection2 = new Point(cx2 - h
					* (c2.getCenter().getY() - c1.getCenter().getY()) / dist,
					cy2 + h * (c2.getCenter().getX() - c1.getCenter().getX())
							/ dist);

			// See if we have 1 or 2 solutions.
			if (dist == c1.getRadius() + c2.getRadius()) {
				inter = new Intersection(new Point[] { new Point(
						intersection1.getX(), intersection1.getY()),new Point(
								intersection1.getX(), intersection1.getY()) }, c1, c2,
						Intersection.INTERSECT);
			} else {
				inter = new Intersection(
						new Point[] {
								new Point(intersection1.getX(),
										intersection1.getY()),
								new Point(intersection2.getX(),
										intersection2.getY()) }, c1, c2,
						Intersection.INTERSECT);
			}
		}
		return inter;
	}

	@Override
	public String getAlgorithmName() {
		// TODO Auto-generated method stub
		return "trilateration";
	}

	@Override
	public JsonObject encode() {
		JsonObjectBuilder circlesBuilder = Json.createObjectBuilder();
		for (int i=0;i<circles.size();i++) {
			circlesBuilder.add(circles.get(i).getMote(), circles.get(i).encode());
		}

		return super.buildEncode().add("circles", circlesBuilder.build())
				.build();
	}

	public class Point implements JsonEncodable {
		protected double x;
		protected double y;
		protected Intersection inter;

		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}
		
		public Point(Intersection inter,double x, double y) {
			super();
			this.x = x;
			this.y = y;
			this.inter=inter;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
		
		
		
		public void setInter(Intersection inter) {
			this.inter = inter;
		}

		public Intersection getInter() {
			return inter;
		}

		@Override
		public JsonObject encode() {
			// TODO Auto-generated method stub
			return Json.createObjectBuilder().add("x", x).add("y", y).build();
		}

		public double distance(Point p) {
			return Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y)
					* (this.y - p.y));
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
				return false;
			if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
				return false;
			return true;
		}

		private Trilateration getOuterType() {
			return Trilateration.this;
		}
		
		public String toString(){
			return "("+x+","+y+")";
		}
	}

	public class Intersection {
		private Point[] points;
		private Circle[] circles;
		private int state;
		public static final int INTERSECT = 0;
		public static final int NO_INTERSECT = 1;
		public static final int SURROUNDED = 2;
		public static final int COINCIDENCE = 3;

		public Intersection(Point[] points, Circle c1, Circle c2, int state) {
			this.state = state;
			this.circles = new Circle[2];
			this.circles[0] = c1;
			this.circles[1] = c2;
			if (points == null)
				this.points = new Point[0];
			else
				this.points = points;
			for(int i=0;i<this.points.length;i++)
				this.points[i].setInter(this);
		}

		public Point[] getPoints() {
			return points;
		}

		public int getState() {
			return state;
		}

		public boolean isIntersect() {
			return state == INTERSECT;
		}

		public boolean isNoIntersect() {
			return state == NO_INTERSECT;
		}

		public boolean isSurrounded() {
			return state == SURROUNDED;
		}

		public boolean isCoincidence() {
			return state == COINCIDENCE;
		}

		public Circle[] getCircles() {
			return circles;
		}
		
		public String toString(){
			return "[State "+state+"] "+circles[0]+" "+circles[1];
		}
	}

	public class Circle implements JsonEncodable {
		private Point center;
		private double radius;
		private String mote;
		private double rssi;

		public Circle(Point center, double radius, String mote, double rssi) {
			super();
			this.center = center;
			this.radius = Math.max(0,radius);
			this.mote = mote;
			this.rssi = rssi;
		}

		public Point getCenter() {
			return center;
		}

		public double getRadius() {
			return radius;
		}

		public String getMote() {
			return mote;
		}
		
		public void setRadius(double radius){
			this.radius=Math.max(0,radius);
		}
		
		

		public double getRssi() {
			return rssi;
		}

		@Override
		public JsonObject encode() {
			// TODO Auto-generated method stub
			return Json.createObjectBuilder().add("center", center.encode())
					.add("radius", radius).add("rssi", rssi).build();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Circle other = (Circle) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (center == null) {
				if (other.center != null)
					return false;
			} else if (!center.equals(other.center))
				return false;
			if (Double.doubleToLongBits(radius) != Double
					.doubleToLongBits(other.radius))
				return false;
			return true;
		}

		private Trilateration getOuterType() {
			return Trilateration.this;
		}
		
		public String toString(){
			return "R="+radius+" "+center;
		}
	}
}
