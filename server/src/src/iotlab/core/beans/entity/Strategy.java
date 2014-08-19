/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Strategy")
public class Strategy implements JsonEncodable {

	@Id
	private int id;
	private String className;
	
	public Strategy(){
		
	}

	public Strategy(String className) {
		this.className=className;
	}

	public int getId() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("id", id)
				.add("className", className).build();
	}

}
