/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.calibration;

import java.util.List;

import javax.ejb.Local;
import javax.json.JsonObject;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Local
public interface Calibration{

	public void calibrate(List<CalibrationData> data);

	public double getL0();

	public double getAlpha();

	public double getLw();

	public double getB();
	
	public double estimateDistance(double rssi);
	public double estimateDistance(double rssi,int wallNb);
	
	public JsonObject encode();

}
