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

import java.util.HashMap;
import java.util.List;

import javax.json.JsonObject;

/**
 * 
 * @author Anthony Deroche
 *
 */
public class Barycenter extends GeolocationAlgorithm {

	private double xg, yg;
	private HashMap<Integer, Boolean> anchorsMap;

	public Barycenter(Calibration calibration, List<GeolocationData> data,
			List<Anchor> anchors) {
		super(calibration, data);
		xg = 0;
		yg = 0;

		anchorsMap = new HashMap<>();
		for (Anchor a : anchors) {
			anchorsMap.put(a.getId(), true);
		}
	}

	@Override
	public void execute(Sender s) {
		this.sender = s;
		int mote_id = s.getId();
		xg = 0;
		yg = 0;
		Mote peer = null;
		boolean found;
		double weight = 0;
		double sum = 0;

		for (int i = 0; i < data.size(); i++) {

			if (anchorsMap.containsKey(data.get(i).getSrc().getId())
					|| anchorsMap.containsKey(data.get(i).getDest().getId())) {
				found = false;
				if (data.get(i).getSrc().getId() == mote_id) {
					peer = data.get(i).getDest();
					found = true;
				} else if (data.get(i).getDest().getId() == mote_id) {
					peer = data.get(i).getSrc();
					found = true;
				}

				if (found) {
					double d = calibration.estimateDistance(data.get(i)
							.getRssi());
					if (d > 0) {
						weight = 1 / d;
						sum += weight;
						xg += peer.getLon() * weight;
						yg += peer.getLat() * weight;
					}
				}
			}
		}

		if (sum > 0) {
			xg /= sum;
			yg /= sum;
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
		return "barycenter";
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return super.buildEncode().build();
	}

}
