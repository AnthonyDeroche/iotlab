/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.algorithm;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * 
 * @author Anthony Deroche
 *
 */
public class CombinedAlgorithms {

	private double xg = 0, yg = 0;
	private String name;

	public CombinedAlgorithms(String name) {
		this.name=name;
	}

	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("algo", name)
				.add("x", getX()).add("y", getY()).build();
	}

	public void combine(GeolocationAlgorithm algo, double xcoeff, double ycoeff) {
		xg += algo.getX()*xcoeff;
		yg += algo.getY()*ycoeff;
	}

	public double getX() {
		// TODO Auto-generated method stub
		return xg;
	}

	public double getY() {
		// TODO Auto-generated method stub
		return yg;
	}
}
