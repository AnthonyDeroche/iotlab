/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.calibration;

import iotlab.core.beans.entity.JsonEncodable;
import iotlab.module.geolocation.Anchor;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "CalibrationMeasure",uniqueConstraints=@UniqueConstraint(columnNames={"first", "second"}))
public class CalibrationMeasure implements JsonEncodable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@ManyToOne
	@JoinColumn(name="first",referencedColumnName = "mote_id")
	private Anchor first;

	@ManyToOne
	@JoinColumn(name="second",referencedColumnName = "mote_id")
	private Anchor second;

	@Column
	private int wallNumber;
	
	@Transient
	private double distance;

	@Transient
	private boolean distanceUpdated=false;

	public CalibrationMeasure() {

	}

	public CalibrationMeasure(Anchor first, Anchor second, int wallNumber) {
		this.first = first;
		this.second = second;
		this.wallNumber = wallNumber;
		this.updateDistance();
	}

	public int getWallNumber() {
		return wallNumber;
	}

	public Anchor getFirst() {
		return first;
	}

	public void setFirst(Anchor first) {
		this.first = first;
	}

	public Anchor getSecond() {
		return second;
	}

	public void setSecond(Anchor second) {
		this.second = second;
	}

	public void updateDistance() {
		this.distanceUpdated=true;
		this.distance = Math.sqrt((first.getLon() - second.getLon())
				* (first.getLon() - second.getLon())
				+ (first.getLat() - second.getLat())
				* (first.getLat() - second.getLat()));
	}

	public double getDistance() {
		if(!distanceUpdated)
			updateDistance();
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setWallNumber(int wallNumber) {
		this.wallNumber = wallNumber;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("first", first.encode())
				.add("second", second.encode())
				.add("wallNumber", wallNumber).add("distance", distance)
				.build();
	}
}
