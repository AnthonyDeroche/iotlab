/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation;

import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.JsonEncodable;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.module.geolocation.algorithm.AlgorithmTester;
import iotlab.module.geolocation.algorithm.Barycenter;
import iotlab.module.geolocation.algorithm.CombinedAlgorithms;
import iotlab.module.geolocation.algorithm.GeolocationAlgorithm;
import iotlab.module.geolocation.algorithm.Multilateration;
import iotlab.module.geolocation.algorithm.Trilateration;
import iotlab.module.geolocation.calibration.Calibration;
import iotlab.module.geolocation.calibration.CalibrationData;
import iotlab.module.geolocation.calibration.CalibrationMeasure;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Singleton
public class GeolocationManager {

	public static final String GEOLOCATION = "geolocation";
	public static final String CALIBRATION = "calibration";
	public static final String LOCALIZATION = "localization";
	public static final String TRILATERATION = "trilateration";
	public static final String BARYCENTER = "barycenter";
	public static final String MULTILATERATION = "multilateration";

	private static final boolean ENABLED_DEFAULT = true;

	private HashMap<String, Service> services;
	private HashMap<String,double[]> algoWeights;

	@EJB
	private DAOManager dao;
	@EJB
	private Calibration calibration;
	
	@EJB
	private AlgorithmTester tester;

	public GeolocationManager() {

	}

	@PostConstruct
	public void init() {
		services = new HashMap<>();
		services.put(GEOLOCATION, new Service(GEOLOCATION, false, 0));
		services.put(CALIBRATION, new Service(CALIBRATION, ENABLED_DEFAULT, 1));
		services.put(LOCALIZATION,
				new Service(LOCALIZATION, ENABLED_DEFAULT, 1));
		services.put(TRILATERATION, new Service(TRILATERATION, ENABLED_DEFAULT,
				2));
		services.put(BARYCENTER, new Service(BARYCENTER, ENABLED_DEFAULT, 2));
		services.put(MULTILATERATION, new Service(MULTILATERATION, ENABLED_DEFAULT,
				2));
		algoWeights = new HashMap<>();
		algoWeights.put(TRILATERATION,new double[]{0.33,0.33,0.33});
		algoWeights.put(BARYCENTER,new double[]{0.33,0.33,0.33});
		algoWeights.put(MULTILATERATION,new double[]{0.33,0.33,0.33});
		
	}
	
	public void setWeight(String algo, double[] weights){
		algoWeights.put(algo,weights);
	}

	public void executeProcess(JsonObjectBuilder responseBuilder,
			List<GeolocationData> values, Timestamp timestamp) {
		List<Anchor> anchors = dao.getAnchorDAO().getAll();

		if (isEnabled(GeolocationManager.CALIBRATION)) {
			this.doCalibration(responseBuilder, anchors, values, timestamp);

		}

		if (isEnabled(GeolocationManager.LOCALIZATION)) {
			this.doLocalization(responseBuilder, anchors);
		}
	}

	private void doCalibration(JsonObjectBuilder responseBuilder,
			List<Anchor> anchors, List<GeolocationData> values,
			Timestamp timestamp) {

		List<CalibrationData> cdata = new ArrayList<>();
		if (anchors.size() > 0) {
			for (int i = 0; i < values.size(); i++) {
				int srcIndex = -1;
				int destIndex = -1;
				for (int k = 0; k < anchors.size(); k++) {
					if (anchors.get(k).getId() == values.get(i).getSrc()
							.getId())
						srcIndex = k;
					if (anchors.get(k).getId() == values.get(i).getDest()
							.getId())
						destIndex = k;
				}

				if (srcIndex > -1 && destIndex > -1) {
					try {
						CalibrationMeasure measure = dao
								.getCalibrationMeasureDAO().find(
										anchors.get(srcIndex).getId(),
										anchors.get(destIndex).getId());
						CalibrationData d = new CalibrationData(measure, values
								.get(i).getRssi(), timestamp);
						dao.getCalibrationDataDAO().merge(d);
					} catch (DAOException e) {
						//
						CalibrationMeasure cm = new CalibrationMeasure(
								anchors.get(srcIndex), anchors.get(destIndex),
								0);
						dao.getCalibrationMeasureDAO().merge(cm);
						// System.out.println(e.getMessage());
					}
				}
			}

			cdata = dao.getCalibrationDataDAO().getLastDataForEachMote(5);
			if (cdata.size() > 0) {
				calibration.calibrate(cdata);
			}
		}

		responseBuilder.add("calibration", calibration.encode());
	}
	
	private List<Sender> getSendersWithoutAnchors(List<Anchor> anchors){
		List<Sender> senders = dao.getSenderDAO().getAll();
		List<Sender> sendersWithoutAnchors = new ArrayList<>();
		
		for(Sender s : senders){
			boolean found = false;
			for(Anchor a : anchors){
				if(a.getId()==s.getId()){
					found=true; break;
				}
			}
			
			if(!found)
				sendersWithoutAnchors.add(s);
		}
		
		return sendersWithoutAnchors;
	}

	private void doLocalization(JsonObjectBuilder responseBuilder,
			List<Anchor> anchors) {

		JsonObjectBuilder localizationBuilder = Json.createObjectBuilder();
		
		// localization
		
		List<Sender> senders = this.getSendersWithoutAnchors(anchors);
		
		GeolocationAlgorithm trilateration = new Trilateration(calibration, dao
				.getGeolocationDataDAO().getLastDataForEachMote(), anchors);
		GeolocationAlgorithm barycenter = new Barycenter(calibration, dao
				.getGeolocationDataDAO().getLastDataForEachMote(),anchors);
		GeolocationAlgorithm multilateration = new Multilateration(calibration, dao
				.getGeolocationDataDAO().getLastDataForEachMote(), anchors);

		
		for (Sender s : senders) {
			int n=0;
			JsonArrayBuilder senderBuilder = Json.createArrayBuilder();
			CombinedAlgorithms combined = new CombinedAlgorithms("combined");
			CombinedAlgorithms customCombined = new CombinedAlgorithms("custom");
			
			if (isEnabled(GeolocationManager.TRILATERATION))
				n++;
			if (isEnabled(GeolocationManager.BARYCENTER))
				n++;
			if (isEnabled(GeolocationManager.MULTILATERATION))
				n++;
			double c = (double)1/(double)n;
			
			if (isEnabled(GeolocationManager.TRILATERATION)) {
				trilateration.execute(s);
				senderBuilder.add(trilateration.encode());
				combined.combine(trilateration,c,c);
				customCombined.combine(trilateration,algoWeights.get(TRILATERATION)[0],algoWeights.get(TRILATERATION)[1]);
				tester.addResult(trilateration.getX(),trilateration.getY(),s,AlgorithmTester.TRILATERATION);
			}

			if (isEnabled(GeolocationManager.BARYCENTER)) {
				barycenter.execute(s);
				senderBuilder.add(barycenter.encode());
				combined.combine(barycenter,c,c);
				customCombined.combine(barycenter,algoWeights.get(BARYCENTER)[0],algoWeights.get(BARYCENTER)[1]);
				tester.addResult(barycenter.getX(),barycenter.getY(),s,AlgorithmTester.BARYCENTER);
				
			}

			if (isEnabled(GeolocationManager.MULTILATERATION)) {
				multilateration.execute(s);
				senderBuilder.add(multilateration.encode());
				combined.combine(multilateration,c,c);
				customCombined.combine(multilateration,algoWeights.get(MULTILATERATION)[0],algoWeights.get(MULTILATERATION)[1]);
				tester.addResult(multilateration.getX(),multilateration.getY(),s,AlgorithmTester.MULTILATERATION);
			}

			if(n>0){ //1 algo is enabled at least
				senderBuilder.add(combined.encode());
				senderBuilder.add(customCombined.encode());
				tester.addResult(combined.getX(),combined.getY(),s,AlgorithmTester.COMBINED);
				tester.addResult(customCombined.getX(),customCombined.getY(),s,AlgorithmTester.COMBINED_CUSTOMIZED);
				tester.addResult(s.getLon(), s.getLat(), s,AlgorithmTester.REAL);
				
			}
			localizationBuilder.add(s.getMac(),senderBuilder.build());
		}

		responseBuilder.add("localization", localizationBuilder.build());

		
		List<Integer> enabled = new ArrayList<>();
		if(isEnabled(GeolocationManager.BARYCENTER)){
			enabled.add(AlgorithmTester.BARYCENTER); 
		}
		if(isEnabled(GeolocationManager.MULTILATERATION)){
			enabled.add(AlgorithmTester.MULTILATERATION); 
		}
		if(isEnabled(GeolocationManager.TRILATERATION)){
			enabled.add(AlgorithmTester.TRILATERATION); 
		}
		Integer[] enabledArr = enabled.toArray(new Integer[0]);
		if(tester.isOptimizerStarted()){
			tester.optimize(enabledArr);
		}
		responseBuilder.add("tester", tester.toJson(enabledArr));
	}

	public boolean isEnabled(String service) {
		Service s = services.get(service);
		return s != null && s.isEnabled();
	}

	public boolean enable(String service) {
		Service s = services.get(service);
		if (s != null) {
			s.setEnabled(true);
			return true;
		}
		return false;
	}

	public boolean disable(String service) {
		Service s = services.get(service);
		if (s != null) {
			s.setEnabled(false);
			return true;
		}
		return false;
	}

	private static class Service implements JsonEncodable, Comparable<Service> {
		private String name;
		private boolean enabled;
		private int priority;

		public Service(String name, boolean enabled, int priority) {
			this.name = name;
			this.enabled = enabled;
			this.priority = priority;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public JsonObject encode() {
			return Json.createObjectBuilder().add("name", name)
					.add("enabled", enabled).add("priority", priority).build();
		}

		@Override
		public int compareTo(Service s) {
			if (s.name.equals(this.name))
				return 0;
			if (s.priority < this.priority)
				return 1;
			return -1;
		}
	}

	public List<Service> getServices() {
		List<Service> list = new ArrayList<Service>(services.values());
		Collections.sort(list);
		return list;
	}
}
