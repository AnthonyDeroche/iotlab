/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.admin;

import iotlab.core.beans.entity.JsonEncodable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "Config")
@Cacheable(false)
public class Parameter implements JsonEncodable{
	@Id
	@Column(name = "parameter")
	private String key;
	private String value;

	public Parameter() {

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("key", key).add("value", value).build();
	}

}