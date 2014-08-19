/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity.mote;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class MoteDecorator implements Mote {

	@Id
	@ManyToOne(targetEntity = MoteImpl.class,cascade=CascadeType.PERSIST)
	@JoinColumn(name = "mote_id", referencedColumnName = "id")
	private Mote mote;
	
	public MoteDecorator(Mote mote){
		this.mote=mote;
	}
	
	public MoteDecorator(){
		this.mote=new MoteImpl();
	}
	
	public MoteDecorator(String ipv6, String mac) {
		this.mote = new MoteImpl(ipv6,mac);
	}

	public MoteDecorator(String ipv6, String mac, double lat, double lon) {
		// TODO Auto-generated constructor stub
		this.mote = new MoteImpl(ipv6,mac,lat,lon);
	}
	
	public MoteDecorator(int id,String ipv6, String mac, double lat, double lon) {
		// TODO Auto-generated constructor stub
		this.mote = new MoteImpl(id,ipv6,mac,lat,lon);
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return mote.encode();
	}
	
	@Override
	public JsonObject encode(JsonObjectBuilder builder) {
		// TODO Auto-generated method stub
		return mote.encode(builder);
	}

	@Override
	public double getLat() {
		// TODO Auto-generated method stub
		return mote.getLat();
	}

	@Override
	public double getLon() {
		// TODO Auto-generated method stub
		return mote.getLon();
	}

	@Override
	public void setLat(double lat) {
		mote.setLat(lat);
		
	}

	@Override
	public void setLon(double lon) {
		// TODO Auto-generated method stub
		mote.setLon(lon);
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return mote.getId();
	}

	@Override
	public String getIpv6() {
		// TODO Auto-generated method stub
		return mote.getIpv6();
	}

	@Override
	public String getMac() {
		// TODO Auto-generated method stub
		return mote.getMac();
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		mote.setId(id);
	}

	@Override
	public void setIpv6(String ipv6) {
		// TODO Auto-generated method stub
		mote.setIpv6(ipv6);
	}

	@Override
	public void setMac(String mac) {
		// TODO Auto-generated method stub
		mote.setMac(mac);
	}
	
	public boolean equals(Object obj){
		return mote.equals(obj);
	}
}
