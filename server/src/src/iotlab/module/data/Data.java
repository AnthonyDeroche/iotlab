/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.data;

import iotlab.core.beans.entity.Experiment;
import iotlab.core.beans.entity.JsonEncodable;
import iotlab.core.beans.entity.Label;
import iotlab.core.beans.entity.mote.Sender;

import java.sql.Timestamp;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonObject;
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
@Table(name = "Data")
public class Data implements JsonEncodable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@JoinColumn(name = "mote_id", referencedColumnName = "mote_id")
	private Sender mote;

	private double value;

	@ManyToOne
	@JoinColumn(name = "label_id", referencedColumnName = "label_id")
	private Label label;

	private Timestamp timestamp;

	@ManyToOne
	@JoinColumn(name = "experiment", referencedColumnName = "id")
	private Experiment experiment;

	public Data(double value, Label label, Timestamp timestamp, Sender mote,
			Experiment experiment) {
		this.value = value;
		this.label = label;
		this.timestamp = timestamp;
		this.mote = mote;
		this.experiment = experiment;
	}

	public long getTimestamp() {
		return timestamp.getTime();
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Data() {

	}

	public int getId() {
		return id;
	}

	public Sender getMote() {
		return mote;
	}

	public double getValue() {
		return value;
	}

	public String getValueToStr() {
		return String.format(Locale.ENGLISH,"%.2f", value);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMote(Sender mote) {
		this.mote = mote;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public String toString() {
		return String.format(Locale.ENGLISH,"%.2f", value);
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("timestamp", timestamp.getTime())
				.add("label", label.getLabel())
				.add("value", Double.valueOf(String.format(Locale.ENGLISH,"%.2f", value)))
				.add("mote", mote.getMac())
				.build();
	}

}
