/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity.mote;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Sink")
public class Sink extends MoteDecorator {

	private int dodagVersionNumber;

	public Sink() {
		super();
	}

	public Sink(Mote mote) {
		super(mote);
	}

	public Sink(String ipv6, String mac, double lat, double lon) {
		super(ipv6, mac, lat, lon);
	}

	public Sink(String ipv6, String mac, int dvn) {
		super(ipv6, mac);
		this.dodagVersionNumber = dvn;
	}

	public int getDodagVersionNumber() {
		return dodagVersionNumber;
	}

	public void setDodagVersionNumber(int dodagVersionNumber) {
		this.dodagVersionNumber = dodagVersionNumber;
	}
	
	@Override
	public JsonObject encode() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("dodagVersionNumber", dodagVersionNumber);
		return super.encode(builder);
	}

}
