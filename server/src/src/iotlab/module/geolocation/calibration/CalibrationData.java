/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.calibration;

import iotlab.core.beans.entity.JsonEncodable;

import java.sql.Timestamp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "CalibrationData")
public class CalibrationData implements JsonEncodable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "measure_id", referencedColumnName = "id")
	private CalibrationMeasure measure;

	@Column
	private Timestamp timestamp;

	@Column
	private double rssi;
	
	public CalibrationData(){
		
	}
	
	public CalibrationData(CalibrationMeasure measure,double rssi,Timestamp timestamp){
		this.measure=measure;
		this.rssi=rssi;
		this.timestamp = timestamp;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("id", id)
				.add("measure", measure.encode())
				.add("timestamp", timestamp.toString()).add("rssi", rssi)
				.build();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CalibrationMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(CalibrationMeasure measure) {
		this.measure = measure;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public double getRssi() {
		return rssi;
	}

	public void setRssi(double rssi) {
		this.rssi = rssi;
	}
	
	

}
