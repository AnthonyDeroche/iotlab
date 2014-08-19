/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity.mote;

import iotlab.core.beans.entity.JsonEncodable;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public interface Mote extends JsonEncodable{
	
	public double getLat();
	public double getLon();
	public void setLat(double lat);
	public void setLon(double lon);
	public int getId();
	public String getIpv6();
	public String getMac();
	public void setId(int id);
	public void setIpv6(String ipv6);
	public void setMac(String mac);
	public JsonObject encode(JsonObjectBuilder builder);
}
