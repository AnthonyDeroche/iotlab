/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity;

import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.MoteImpl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Sensor")
public class Sensor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(targetEntity = MoteImpl.class)
	@JoinColumn(name = "mote_id",referencedColumnName = "id")
	private Mote mote;

	private float correction;

	private boolean welded_to_mote;

	private double lat, lon;
	
	@ManyToOne
	@JoinColumn(name = "label_id",referencedColumnName = "label_id")
	private Label label;

	public Sensor() {

	}

	public int getId() {
		return id;
	}

	public Mote getMote() {
		return mote;
	}

	public float getCorrection() {
		return correction;
	}

	public boolean isWelded_to_mote() {
		return welded_to_mote;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMote(Mote mote) {
		this.mote = mote;
	}

	public void setCorrection(float correction) {
		this.correction = correction;
	}

	public void setWelded_to_mote(boolean welded_to_mote) {
		this.welded_to_mote = welded_to_mote;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

}
