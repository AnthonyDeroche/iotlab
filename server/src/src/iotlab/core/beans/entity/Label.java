/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Label")
public class Label implements JsonEncodable {

	@Id
	private int label_id;
	@Column(unique = true)
	private String label;

	public Label() {

	}

	public Label(String label) {
		this.label = label;
	}

	public int getLabel_id() {
		return label_id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel_id(int label_id) {
		this.label_id = label_id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("label_id", label_id)
				.add("label", label).build();
	}
}
