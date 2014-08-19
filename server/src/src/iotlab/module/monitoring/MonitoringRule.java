/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.core.beans.entity.Label;
import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.MoteImpl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Arthur Garnier
 *
 */
@Entity
@Table(name = "MonitoringRule")
public class MonitoringRule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(targetEntity = MoteImpl.class)
	@JoinColumn(name = "mote_id", referencedColumnName = "id")
	private Mote mote;
	
	@ManyToOne
	@JoinColumn(name = "label_id", referencedColumnName = "label_id")
	private Label label;
	
	private double minVal;
	
	private double maxVal;

	public MonitoringRule() {
	}

	public MonitoringRule(Mote mote, Label label, double minVal,
			double maxVal) {
		this.mote = mote;
		this.label = label;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Mote getMote() {
		return mote;
	}

	public void setMote(Mote mote) {
		this.mote = mote;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public double getMinVal() {
		return minVal;
	}

	public void setMinVal(double minVal) {
		this.minVal = minVal;
	}

	public double getMaxVal() {
		return maxVal;
	}

	public void setMaxval(double maxVal) {
		this.maxVal = maxVal;
	}
	
	
	
	

}
