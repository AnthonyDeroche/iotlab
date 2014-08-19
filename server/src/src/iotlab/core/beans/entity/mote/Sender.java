/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity.mote;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Sender")
public class Sender extends MoteDecorator {

	public Sender() {
		super();
	}
	
	public Sender(Mote mote){
		super(mote);
	}

	public Sender(String ipv6, String mac, double lat, double lon) {
		super(ipv6, mac, lat, lon);
	}

	public Sender(String ipv6, String mac) {
		super(ipv6, mac);
	}
}
