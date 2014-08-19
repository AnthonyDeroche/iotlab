/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation;

import iotlab.core.beans.entity.JsonEncodable;
import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.Sender;

import java.sql.Timestamp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "GeolocationData")
public class GeolocationData implements JsonEncodable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@JoinColumn(name = "src", referencedColumnName = "mote_id")
	private Sender src;

	@JoinColumn(name = "dest", referencedColumnName = "mote_id")
	private Sender dest;

	private int rssi;

	private Timestamp timestamp;

	public GeolocationData() {

	}
	
	public GeolocationData(Sender src,Sender dest,int rssi, Timestamp timestamp) {
		this.src=src;
		this.dest=dest;
		this.rssi=rssi;
		this.timestamp=timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Mote getSrc() {
		return src;
	}

	public void setSrc(Sender src) {
		this.src = src;
	}

	public Mote getDest() {
		return dest;
	}

	public void setDest(Sender dest) {
		this.dest = dest;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("timestamp", timestamp.getTime())
				.add("src", src.encode()).add("dest", dest.encode())
				.add("rssi", rssi).build();
	}

}
