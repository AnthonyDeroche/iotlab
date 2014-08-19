/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation;

import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.MoteDecorator;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "Anchor")
public class Anchor extends MoteDecorator{
	
	public Anchor(int id,String ipv6,String mac,double lat, double lon){
		super(id,ipv6,mac,lat,lon);
	}
	
	public Anchor(String ipv6, String mac) {
		super(ipv6, mac);
	}
	
	public Anchor(Mote mote){
		super(mote);
	}
	
	public Anchor() {
		super();
	}
}
