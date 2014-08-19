/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.core.beans.dao.DAO;

import java.util.List;

import javax.ejb.Stateless;

/**
 * 
 * @author Arthur Garnier
 *
 */
@Stateless
public class AlertDAO extends DAO<Alert> {
	private static final String ENTITY_NAME = "Alert";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public Alert getLastbyRule(MonitoringRule mr) {
		List<Alert> l = getAll("id", false, -1, "o.monitoringRule.id="+mr.getId()+"", null);
		if(l.size()>0)
			return l.get(0);
		else
			return null;
	}

}
