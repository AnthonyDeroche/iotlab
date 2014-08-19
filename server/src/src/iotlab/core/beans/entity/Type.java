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
@Table(name = "Type")
public class Type implements JsonEncodable {

	@Id
	private int id;
	private String description = "";
	private String streamName;
	private int minDataNumber;

	public Type() {

	}

	public Type(int id, String description, String streamName, int minDataNumber) {
		this.id = id;
		this.description = description;
		this.streamName = streamName;
		this.minDataNumber = minDataNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("id", id)
				.add("description", description).add("streamName", streamName)
				.add("minDataNumber", minDataNumber).build();
	}

	public int getMinDataNumber() {
		return minDataNumber;
	}

	public void setMinDataNumber(int minDataNumber) {
		this.minDataNumber = minDataNumber;
	}

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

}
