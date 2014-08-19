/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity.mote;

import iotlab.core.beans.entity.Sensor;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Mote")
public class MoteImpl implements Mote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String ipv6;
	@Column(unique=true)
	private String mac;
	private double lat;
	private double lon;

	@OneToMany(mappedBy = "mote")
	private List<Sensor> sensors;

	public MoteImpl() {
		lat = 0;
		lon = 0;
		ipv6 = "";
		mac = "";
	}

	public MoteImpl(String ipv6, String mac) {
		this.ipv6 = ipv6;
		this.mac = mac;
		lon = 0;
		lat = 0;
	}

	public MoteImpl(String ipv6, String mac, double lat, double lon) {
		this.ipv6 = ipv6;
		this.mac = mac;
		this.lat = lat;
		this.lon = lon;
	}

	public MoteImpl(int id,String ipv6, String mac, double lat, double lon) {
		this.ipv6 = ipv6;
		this.mac = mac;
		this.lat = lat;
		this.lon = lon;
		this.id=id;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public int getId() {
		return id;
	}

	public String getIpv6() {
		return ipv6;
	}

	public String getMac() {
		return mac;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public JsonObject encode() {
		return encode(Json.createObjectBuilder());
	}
	
	@Override
	public JsonObject encode(JsonObjectBuilder builder) {		
		return builder.add("id", id).add("ipv6", ipv6)
		.add("mac", mac).add("lat", lat).add("lon", lon).build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mote other = (Mote) obj;
		if (id != other.getId())
			return false;
		return true;
	}

	
}
