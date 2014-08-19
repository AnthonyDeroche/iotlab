/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.core.beans.dao.DAO;
import iotlab.core.beans.dao.DAOException;

import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

/**
 * 
 * @author Arthur Garnier
 *
 */
@Stateless
public class MonitoringRuleDAO extends DAO<MonitoringRule> {
	private static final String ENTITY_NAME = "MonitoringRule";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}
	
	public MonitoringRule find(int id) {
		return em.find(MonitoringRule.class, id);
	}
	
	
	
	public void remove(MonitoringRule mr) throws DAOException {
		em.remove(em.merge(mr));
		try{
			em.flush();
		}catch(PersistenceException e){
			throw new DAOException("Monitoring Rule deletion","The rule '"+mr.getId()+"' cannot be deleted because some filters or data are dependent on it");
		}
	}

}
