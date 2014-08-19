/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import java.util.Date;
import java.sql.Timestamp;

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
@Table(name = "Alert")
public class Alert {
	
	public Alert() {
		super();
	}

	public Alert(MonitoringRule monitoringRule, double value) {
		super();
		this.monitoringRule = monitoringRule;
		this.value = value;
		this.date = new Timestamp(new Date().getTime());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "monitoringRuleId", referencedColumnName = "id")
	private MonitoringRule monitoringRule;
	
	private double value;
	
	private Timestamp date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MonitoringRule getMonitoringRule() {
		return monitoringRule;
	}

	public void setMonitoringRule(MonitoringRule monitoringRule) {
		this.monitoringRule = monitoringRule;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	

}
