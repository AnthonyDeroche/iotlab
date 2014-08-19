/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Filter")
public class Filter implements JsonEncodable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int offset;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name = "strategy", referencedColumnName = "id")
	private Strategy strategy;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name = "label_id", referencedColumnName = "label_id")
	private Label label;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name = "type", referencedColumnName = "id")
	private Type type;

	public Filter(int offset, Strategy strategy, Label label, Type type) {
		this.offset = offset;
		this.strategy = strategy;
		this.label = label;
		this.type = type;
	}

	public Filter() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOffset() {
		return offset;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("id",id).add("offset", offset)
				.add("label", label.encode())
				.add("strategy", strategy.encode()).add("type", type.encode())
				.build();
	}

}
