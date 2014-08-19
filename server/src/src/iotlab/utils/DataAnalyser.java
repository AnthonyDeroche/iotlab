/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.utils;

import iotlab.core.beans.dao.DAOManager;
import iotlab.module.admin.Config;
import iotlab.module.admin.ConfigException;
import iotlab.module.data.Data;
import iotlab.module.monitoring.Alert;
import iotlab.module.monitoring.MonitoringRule;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author Arthur Garnier
 *
 */
@Stateless
public class DataAnalyser {

	@EJB
	private DAOManager dao;
	@EJB
	private Config config;

	public DataAnalyser() {
	}

	public void analyse(Data data) {
		List<MonitoringRule> listMR = dao.getMonitoringRuleDAO().getAll("id",true,-1,"o.label.label='" + data.getLabel().getLabel()
						+ "' AND o.mote.ipv6='" + data.getMote().getIpv6()
						+ "'", null);
		for (MonitoringRule mr : listMR) {
			Alert a = dao.getAlertDAO().getLastbyRule(mr);
			int interTime = (config.get("timeBetAlert") != null) ? Integer
					.parseInt(config.get("timeBetAlert").getValue()) : 0;
			if (a == null
					|| (a.getDate().getTime() + interTime * 1000) < data
							.getTimestamp()) {
				// On enregistre l'alerte
				Alert alert = new Alert(mr, data.getValue());
				dao.getAlertDAO().persist(alert);
				// On envoie l'alerte
				try {
					SendMailSSL.setConfig(config);
					SendMailSSL.sendAlert(alert.getMonitoringRule().getMote()
							.getIpv6(), alert.getMonitoringRule().getLabel()
							.getLabel(), alert.getValue());
				} catch (ConfigException e) {
					System.err.println(e.getMessage());
				}
				
			}
		}
	}
}
