/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.algorithm;

import iotlab.core.beans.entity.JsonEncodable;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.module.geolocation.GeolocationData;
import iotlab.module.geolocation.calibration.Calibration;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * 
 * @author Anthony Deroche
 *
 */
public abstract class GeolocationAlgorithm implements JsonEncodable {

	protected List<GeolocationData> data;
	protected Calibration calibration;
	protected Sender sender;

	public GeolocationAlgorithm(Calibration calibration,
			List<GeolocationData> data) {
		this.data = data;
		this.calibration=calibration;
	}

	public abstract void execute(Sender s);

	public abstract double getX();

	public abstract double getY();
	
	public abstract String getAlgorithmName();

	protected JsonObjectBuilder buildEncode() {
		return Json.createObjectBuilder().add("algo",getAlgorithmName()).add("x", getX()).add("y", getY());
	}

}
